CREATE OR REPLACE FUNCTION core.finish_colony_building_order(colony_ bigint)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare

begin

  insert into core.colony_buildings(colony, building_type) select colony, building_type from core.colony_building_orders where colony=colony_;
  delete from core.colony_building_orders where colony=colony_;

  return '{}'::json;
end;$function$;