

CREATE OR REPLACE FUNCTION core.get_planets(token_ text)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  user_id_ bigint;
begin

  user_id_ = usr from core.sessions where id=token_;
  
  if (user_id_ is null) then
    perform core.error(401, 'Invalid token');
  end if;


  result_ = (with ps as (

    select p.id, p.star_system as "starSystem", p.orbit, p.type, p.size from core.planets p 
    join core.known_star_systems k on k.star_system=p.star_system
    join core.civilizations c on k.civilization=c.id
    join core.users u on c.usr=u.id and c.galaxy=u.galaxy
    where u.id=user_id_

  ) select array_to_json(array_agg(ps)) from ps);

  return coalesce(result_, '[]')::json;
end;$function$;




CREATE OR REPLACE FUNCTION core.find_random_unexplored_system(galaxy_ bigint)
 RETURNS bigint
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ bigint;
  count_ bigint;
begin

  count_ = (select count(*) from core.star_systems where not explored and galaxy=galaxy_);

  result_ = (SELECT id FROM core.star_systems where not explored and galaxy=galaxy_ OFFSET floor(random()*count_) LIMIT 1);
  
  return result_;
end;$function$;




CREATE OR REPLACE FUNCTION core.get_civilizations(token_ text)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  user_id_ bigint;
  civilization_id_ bigint;
begin

  user_id_ = usr from core.sessions where id=token_;
  
  if (user_id_ is null) then
    perform core.error(401, 'Invalid token');
  end if;

  civilization_id_ = (select c.id from core.civilizations c join core.users u on u.galaxy=c.galaxy and u.id=c.usr where u.id=user_id_);

  if (civilization_id_ is null) then
    perform core.error(400, 'User does not have civilization or galaxy selected');
  end if;

  result_ = (with cs as (

    select c.id, c.name, c.homeworld from core.civilizations c join core.known_civilizations k on k.known=c.id where k.knows=civilization_id_

  ) select array_to_json(array_agg(cs)) from cs);

  return coalesce(result_, '[]')::json;
end;$function$;




CREATE OR REPLACE FUNCTION core.get_fleets(token_ text)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  user_id_ bigint;
  civilization_id_ bigint;
begin

  user_id_ = usr from core.sessions where id=token_;
  
  if (user_id_ is null) then
    perform core.error(401, 'Invalid token');
  end if;

  civilization_id_ = (select c.id from core.civilizations c join core.users u on u.galaxy=c.galaxy and u.id=c.usr where u.id=user_id_);

  if (civilization_id_ is null) then
    perform core.error(400, 'User does not have civilization or galaxy selected');
  end if;

  result_ = (with fs as (

    select f.id, f.civilization, f.destination, f.origin, f.travel_start_time as "travelStartTime", colony_ships>0 as "canColonize" from core.fleets f join core.visible_star_systems v on v.star_system=f.destination where v.civilization=civilization_id_ and f.civilization <> civilization_id_
    union
    select f.id, f.civilization, f.destination, f.origin, f.travel_start_time as "travelStartTime", colony_ships>0 as "canColonize" from core.fleets f where f.civilization = civilization_id_

  ) select array_to_json(array_agg(fs)) from fs);

  return coalesce(result_, '[]')::json;
end;$function$;



CREATE OR REPLACE FUNCTION core.get_travels()
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
begin

  result_ = (with ts as (

    select t.fleet, f.civilization, ss0.x as x0, ss1.x as x1, ss0.y as y0, ss1.y as y1, 1 as speed, t.start_time as "startTime" from core.travels t join core.fleets f on f.id=t.fleet join core.star_systems ss0 on ss0.id=t.origin join core.star_systems ss1 on ss1.id=t.destination

  ) select array_to_json(array_agg(ts)) from ts);

  return format('{"travels":%s}', coalesce(result_, '[]'))::json;
end;$function$;








CREATE OR REPLACE FUNCTION core.generate_planets(star_system_ bigint)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  i integer;
begin

  i = 1;

  while i <= 15 loop
    if (random() < 0.3) then
      insert into core.planets(star_system, orbit, type, size)
      select star_system_, i, (random()*10+1)::integer, (random()*4+1)::integer;
    end if;
    i = i + 1;
  end loop;

end;$function$;




CREATE OR REPLACE FUNCTION core.get_ships(fleet_id_ bigint, token_ text)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  user_id_ bigint;
  civilization_id_ bigint;
  fleet_ core.fleets;
begin

  user_id_ = usr from core.sessions where id=token_;
  
  if (user_id_ is null) then
    perform core.error(401, 'Invalid token');
  end if;

  civilization_id_ = (select c.id from core.civilizations c join core.users u on u.galaxy=c.galaxy and u.id=c.usr where u.id=user_id_);

  if (civilization_id_ is null) then
    perform core.error(400, 'User does not have civilization or galaxy selected');
  end if;

  select * from core.fleets where id=fleet_id_ into fleet_;

  if (fleet_.civilization <> civilization_id_) then
    if (fleet_.destination <> fleet_.origin) then
      return '[]'::json;
    end if;
    if (not exists(select 1 from core.visible_star_systems where civilization=civilization_id_ and star_system=fleet_.destination)) then
      return '[]'::json;
    end if;

  end if;

  result_ = (with x as (

    select id, fleet, model_name as "modelName", can_colonize as "canColonize", can_fight as "canFight" from core.ships where fleet=fleet_id_

  ) select array_to_json(array_agg(x)) from x);

  return coalesce(result_, '[]')::json;
end;$function$;