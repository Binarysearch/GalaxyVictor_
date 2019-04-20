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
    (select row_to_json(h) from (select p.id, p.star_system as "starSystem", p.orbit, p.type, p.size from core.planets p where p.id=c.homeworld) as h) as homeworld,
    trade_routes_cache as "tradeRoutesCache", 
    research_orders_cache as "researchOrdersCache", 
    ship_models_cache as "shipModelsCache", 
    civilizations_cache as "civilizationsCache", 
    planets_cache as "planetsCache", 
    colonies_cache as "coloniesCache", 
    fleets_cache as "fleetsCache" 
    from core.civilizations c 
    where c.id=civilization_id_

  ) select row_to_json(ss) from ss);

  return coalesce(result_, '{}')::json;
end;$function$;