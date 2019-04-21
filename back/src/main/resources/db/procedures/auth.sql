CREATE OR REPLACE FUNCTION core.auth(token_ text, time_ bigint)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  user_ json;
  user_id_ bigint;
begin

  user_id_ = usr from core.sessions where id=token_;
  
  if (user_id_ is null) then
    perform core.error(401, 'Invalid token');
  end if;


  user_ = (with x as (

    select u.id, u.email, time_ as "serverTime",
    (select row_to_json(cg) from (select id,name from core.galaxies where id=u.galaxy) as cg) as "currentGalaxy",
    (select row_to_json(cc) from (

    select c.id, c.name, 
    (select row_to_json(h) from (select p.id, p.star_system as "starSystem", p.orbit, p.type, p.size from core.planets p where p.id=c.homeworld) as h) as homeworld,
    trade_routes_cache as "tradeRoutesCache", 
    research_orders_cache as "researchOrdersCache", 
    ship_models_cache as "shipModelsCache", 
    civilizations_cache as "civilizationsCache", 
    planets_cache as "planetsCache"
    from core.civilizations c 
    where c.usr=user_id_ and c.galaxy=u.galaxy

    ) as cc) as "currentCivilization" 
    from core.users u where u.id = user_id_

  ) select row_to_json(x) from x);

  return format('{"token": "%s", "user": %s, "serverTime": %s}', token_, user_, time_)::json;
end;$function$;