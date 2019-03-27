CREATE OR REPLACE FUNCTION core.get_colony_ship_orders()
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
begin

  result_ = (with x as (

    select cbo.ship_model as "shipModelId", csm.name, cbo.colony, cbo.started_time as "startedTime", c.civilization
    from core.colony_building_orders cbo 
     join core.colonies c on c.id=cbo.colony 
     join core.civilization_ship_models csm on csm.id=cbo.ship_model
      where cbo.ship_model is not null

  ) select array_to_json(array_agg(x)) from x);

  return format('{"orders":%s}', coalesce(result_, '[]'))::json;
end;$function$;