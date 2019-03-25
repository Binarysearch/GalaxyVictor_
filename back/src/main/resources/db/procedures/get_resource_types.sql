CREATE OR REPLACE FUNCTION core.get_resource_types(token_ text)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  civilization_id_ bigint;
begin

  civilization_id_ = core.require_civ(token_);


  result_ = (with x as (
    
    select id, name from core.resource_types

  ) select array_to_json(array_agg(x)) from x);

  return coalesce(result_, '[]')::json;
end;$function$;