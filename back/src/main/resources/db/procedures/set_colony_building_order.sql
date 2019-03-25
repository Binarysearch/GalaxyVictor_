CREATE OR REPLACE FUNCTION core.set_colony_building_order(colony_ bigint, building_type_ text, time_ bigint, token_ text)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  civilization_id_ bigint;
begin

  civilization_id_ = core.require_civ(token_);

  -- if civilization does not own the colony throw error
  if (not exists(select 1 from core.colonies where id=colony_ and civilization=civilization_id_)) then
    perform core.error(401, format('Colony %L not yours', colony_));
  end if;

  update core.colony_building_orders set building_type = building_type_, started_time=time_ where colony=colony_;

  result_ = (with x as (

    select id, name,
    (select array_to_json(array_agg(r)) from (select resource_type as type, quantity from core.colony_building_types_resources where building_type=id) as r) as resources
     from core.colony_building_types where id=building_type_

  ) select row_to_json(x) from x);

  return coalesce(result_, '{}')::json;
end;$function$;