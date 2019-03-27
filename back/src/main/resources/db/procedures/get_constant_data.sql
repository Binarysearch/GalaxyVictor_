CREATE OR REPLACE FUNCTION core.get_constant_data(token_ text)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  resource_types_ json;
  colony_building_capability_types_ json;
  colony_building_types_ json;
  civilization_id_ bigint;
begin

  civilization_id_ = core.require_civ(token_);


  --RESOURCE TYPES
  resource_types_ = (with x as (
    
    select id, name from core.resource_types

  ) select array_to_json(array_agg(x)) from x);


  --CAPABILITY TYPES
  colony_building_capability_types_ = (with x as (
    
    select id, name from core.colony_building_capability_types

  ) select array_to_json(array_agg(x)) from x);


  --COLONY BUILDING TYPES
  colony_building_types_ = (with x as (
    
    select id, name, buildable, repeatable,
    (select array_to_json(array_agg(r)) from (select resource_type as type, quantity from core.colony_building_types_resources where building_type=id) as r) as resources,
    (select array_to_json(array_agg(r)) from (select resource_type as type, quantity from core.colony_building_types_costs where building_type=id) as r) as costs,
    (select array_to_json(array_agg(r)) from (select capability_type as type from core.colony_building_types_capabilities where building_type=id) as r) as capabilities
     from core.colony_building_types

  ) select array_to_json(array_agg(x)) from x);


  return format('{"resourceTypes": %s, "colonyBuildingTypes": %s, "colonyBuildingCapabilityTypes": %s}', resource_types_, colony_building_types_, colony_building_capability_types_)::json;
end;$function$;