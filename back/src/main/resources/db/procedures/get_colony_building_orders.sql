CREATE OR REPLACE FUNCTION core.get_colony_building_orders()
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
begin

  result_ = (with x as (

    select cbo.building_type as "buildingTypeId", cbt.name, cbo.colony, cbo.started_time as "startedTime", c.civilization,
    (select array_to_json(array_agg(r)) from (select resource_type as "resourceType", quantity from core.colony_building_types_costs where building_type=cbo.building_type) as r) as costs,
    (select array_to_json(array_agg(r)) from (select resource_type as "resourceType", quantity from core.colony_resources where colony=c.id) as r) as colonyResources
     from core.colony_building_orders cbo 
     join core.colonies c on c.id=cbo.colony 
     join core.colony_building_types cbt on cbt.id=cbo.building_type
      where cbo.building_type is not null
  ) select array_to_json(array_agg(x)) from x);

  return format('{"orders":%s}', coalesce(result_, '[]'))::json;
end;$function$;