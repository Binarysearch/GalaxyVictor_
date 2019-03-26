CREATE OR REPLACE FUNCTION core.set_colony_building_order(colony_ bigint, building_type_ text, time_ bigint, token_ text)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  civilization_id_ bigint;
  lacking_resource_ text;
begin

  civilization_id_ = core.require_civ(token_);

  -- if civilization does not own the colony throw error
  if (not exists(select 1 from core.colonies where id=colony_ and civilization=civilization_id_)) then
    perform core.error(401, format('Colony %L not yours', colony_));
  end if;

  -- Check that there are enought resources in colony
  if (exists(select 1 from core.colony_building_types_resources cbtr
      join core.colony_resources cr on cr.resource_type=cbtr.resource_type
      where cbtr.building_type=building_type_ and cr.colony=colony_ and cr.quantity+cbtr.quantity<0)) then
    perform core.error(400, 'Not enought resources');
  end if;

  -- Check that the building type is buildable
  if (not exists(select 1 from core.colony_building_types where id=building_type_ and buildable)) then
    perform core.error(400, 'Building type not buildable');
  end if;

  update core.colony_building_orders set building_type = building_type_, started_time=time_ where colony=colony_;
  insert into core.colony_building_orders(colony, building_type, started_time) select colony_, building_type_, time_ where not exists(select 1 from core.colony_building_orders where colony=colony_);
  

  result_ = (with x as (

    select id as "buildingTypeId", name, colony_ as colony, time_ as "startedTime", civilization_id_ as civilization,
    (select array_to_json(array_agg(r)) from (select resource_type as "resourceType", quantity from core.colony_building_types_costs where building_type=id) as r) as costs,
    (select array_to_json(array_agg(r)) from (select resource_type as "resourceType", quantity from core.colony_resources where colony=colony_) as r) as colonyResources
     from core.colony_building_types where id=building_type_

  ) select row_to_json(x) from x);

  return coalesce(result_, '{}')::json;
end;$function$;