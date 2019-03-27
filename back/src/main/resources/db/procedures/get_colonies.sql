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

    select c.id, c.planet, c.civilization, bo.building_type as "buildingOrder", bt.name as "buildingOrderName", bo.ship_model as "shipOrder", csm.name as "shipOrderName" 
    from core.colonies c 
    join core.planets p on p.id=c.planet 
    join core.visible_star_systems v on v.star_system=p.star_system 
    left join core.colony_building_orders bo on bo.colony=c.id 
    left join core.colony_building_types bt on bt.id=bo.building_type 
    left join core.civilization_ship_models csm on csm.id=bo.ship_model 
    where v.civilization=civilization_id_

  ) select array_to_json(array_agg(cs)) from cs);

  return coalesce(result_, '[]')::json;
end;$function$;