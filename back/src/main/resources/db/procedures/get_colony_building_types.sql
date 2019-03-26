CREATE OR REPLACE FUNCTION core.get_colony_building_types(token_ text)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  civilization_id_ bigint;
begin

  civilization_id_ = core.require_civ(token_);


  result_ = (with x as (
    
    select id, name, buildable,
    (select array_to_json(array_agg(r)) from (select resource_type as type, quantity from core.colony_building_types_resources where building_type=id) as r) as resources
     from core.colony_building_types

  ) select array_to_json(array_agg(x)) from x);

  return coalesce(result_, '[]')::json;
end;$function$;