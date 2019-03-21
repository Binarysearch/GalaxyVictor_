

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




CREATE OR REPLACE FUNCTION core.get_current_civilization(token_ text)
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

  result_ = (with ss as (

    select c.id, c.name, 
    (select row_to_json(h) from (select p.id, p.star_system as "starSystem", p.orbit, p.type, p.size from core.planets p where p.id=c.homeworld) as h) as homeworld 
    from core.civilizations c 
    where c.id=civilization_id_

  ) select row_to_json(ss) from ss);

  return coalesce(result_, '{}')::json;
end;$function$;




CREATE OR REPLACE FUNCTION core.create_civilization(name_ text, home_star_name_ text, token_ text)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  user_id_ bigint;
  galaxy_id_ bigint;
  star_system_ bigint;
  homeworld_id_ bigint;
  civilization_id_ bigint;
  fleet_id_ bigint;
begin

  user_id_ = usr from core.sessions where id=token_;
  
  if (user_id_ is null) then
    perform core.error(401, 'Invalid token');
  end if;

  galaxy_id_ = galaxy from core.users where id=user_id_;

  if (galaxy_id_ is null) then
    perform core.error(400, 'User does not have selected galaxy');
  end if;

  star_system_ = core.find_random_unexplored_system(galaxy_id_);
  perform core.generate_planets(star_system_);

  update core.star_systems set name = home_star_name_, explored=true where id=star_system_;

  insert into core.planets(star_system, orbit, type, size) select star_system_, 3, 10, 3 
  where not exists(select 1 from core.planets where star_system=star_system_ and orbit=3);
  homeworld_id_ = id from core.planets where star_system = star_system_ and orbit=3;

  update core.planets set type=10, size=3 where star_system=star_system_ and orbit=3;

  insert into core.civilizations(galaxy, name, homeworld, usr) values(galaxy_id_, name_, homeworld_id_, user_id_) returning id into civilization_id_;

  insert into core.known_star_systems(civilization, star_system) values(civilization_id_, star_system_);

  insert into core.known_civilizations(knows, known) values(civilization_id_, civilization_id_);

  insert into core.colonies(civilization, planet) values(civilization_id_, homeworld_id_);

  insert into core.fleets(civilization, destination, origin, travel_start_time) values(civilization_id_, star_system_, star_system_, 0) returning id into fleet_id_;

  insert into core.ships(fleet, model_name, can_colonize, can_fight) values (fleet_id_, 'Colonizador', true, false),(fleet_id_, 'Explorador', false, false);

  return core.get_current_civilization(token_);
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

  result_ = (SELECT id FROM core.star_systems OFFSET floor(random()*count_) LIMIT 1);
  
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



CREATE OR REPLACE FUNCTION core.get_colonies(token_ text)
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

    select c.id, c.planet, c.civilization from core.colonies c join core.planets p on p.id=c.planet join core.visible_star_systems v on v.star_system=p.star_system where v.civilization=civilization_id_

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

    select f.id, f.civilization, f.destination, f.origin, f.travel_start_time as "travelStartTime" from core.fleets f join core.visible_star_systems v on v.star_system=f.destination where v.civilization=civilization_id_ and f.civilization <> civilization_id_
    union
    select f.id, f.civilization, f.destination, f.origin, f.travel_start_time as "travelStartTime" from core.fleets f where f.civilization = civilization_id_

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




CREATE OR REPLACE FUNCTION core.finish_travel(fleet_ bigint)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  destination_ bigint;
  civilization_ bigint;
  ss_was_known_ boolean;
  ss_was_visible_ boolean;

begin

  destination_ = destination from core.fleets where id=fleet_;
  civilization_ = civilization from core.fleets where id=fleet_;
  delete from core.travels where fleet=fleet_;
  update core.fleets set travel_start_time=0, origin=destination where id=fleet_;

  ss_was_known_ = (exists(select 1 from core.known_star_systems where star_system=destination_ and civilization=civilization_));
  ss_was_visible_ = (exists(select 1 from core.visible_star_systems where star_system=destination_ and civilization=civilization_));

  insert into core.visible_star_systems(star_system, civilization) values(destination_, civilization_);
  
  if (not ss_was_known_) then
    insert into core.known_star_systems(star_system, civilization) values(destination_, civilization_);
  end if;

  if (not (select explored from core.star_systems where id=destination_)) then
    update core.star_systems set explored = true where id=destination_;
    perform core.generate_planets(destination_);
  end if;

  result_ = (
    with x as (select
      (select array_to_json(array_agg(p)) from (select id, star_system as "starSystem", orbit, type, size from core.planets where star_system=destination_ and not ss_was_known_) as p) as planets,
      (select array_to_json(array_agg(flee)) from (
        select f.id, f.civilization, f.destination, f.origin, f.travel_start_time as "travelStartTime" from core.fleets f where civilization <> civilization_ and destination=destination_ and not ss_was_visible_
      ) as flee) as fleets,
      (select array_to_json(array_agg(cols)) from (
        select c.id, c.planet, c.civilization from core.colonies c join core.planets p on p.id=c.planet where c.civilization <> civilization_ and p.star_system=destination_ and not ss_was_visible_
      ) as cols) as colonies,
      (select array_to_json(array_agg(civs)) from (
        select c.id, c.name, c.homeworld from core.civilizations c where id <> civilization_ and id in(select co.civilization from core.colonies co join core.planets p on p.id=co.planet where p.star_system=destination_ and not ss_was_visible_ union select f.civilization from core.fleets f where destination=destination_ and not ss_was_visible_)
      ) as civs) as civilizations
    ) select row_to_json(x) from x
  );

  return coalesce(result_, '{}')::json;
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