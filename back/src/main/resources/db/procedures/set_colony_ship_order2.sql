CREATE OR REPLACE FUNCTION core.set_colony_ship_order2(colony_ bigint, ship_model_ bigint, time_ bigint, token_ text)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  civilization_id_ bigint;
  message_orders json;
  asinc_task_orders json;
begin

  civilization_id_ = core.require_civ(token_);

  -- if civilization does not own the colony throw error
  if (not exists(select 1 from core.colonies where id=colony_ and civilization=civilization_id_)) then
    perform core.error(401, format('Colony %L not yours', colony_));
  end if;

  update core.colony_building_orders set ship_model = ship_model_, building_type = null, started_time=time_ where colony=colony_;
  insert into core.colony_building_orders(colony, ship_model, started_time) select colony_, ship_model_, time_ where not exists(select 1 from core.colony_building_orders where colony=colony_);
  

  result_ = (with x as (

    select id as "shipModelId", name, colony_ as colony, time_ as "startedTime", civilization_id_ as civilization
     from core.civilization_ship_models where id=ship_model_

  ) select row_to_json(x) from x);

  message_orders = format('[{"type": "ShipBuildingOrder", "payload": %s, "civilizations": [%s]}]', result_, civilization_id_);
  asinc_task_orders = format('[{"type":"BUILD_SHIP", "asincTaskData": %s}]', result_);

  return format('{"messageOrders": %s, "asincTaskCancelOrders": [%s], "asincTaskOrders": %s}', message_orders, colony_, asinc_task_orders);
end;$function$;