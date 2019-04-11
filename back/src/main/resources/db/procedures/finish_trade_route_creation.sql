CREATE OR REPLACE FUNCTION core.finish_trade_route_creation(id_ bigint)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  trade_route_ json;
  message_orders json;
  trade_route_id_ bigint;
  origin_civilization_ bigint;
  destination_civilization_ bigint;
begin
  
  origin_civilization_ = civilization from core.colonies where id=(select origin from core.trade_routes_creation where id=id_);
  destination_civilization_ = civilization from core.colonies where id=(select destination from core.trade_routes_creation where id=id_);

  insert into core.trade_routes(origin, destination, resource_type, quantity) select origin, destination, resource_type, quantity from core.trade_routes_creation where id=id_ returning id into trade_route_id_;
  delete from core.trade_routes_creation where id=id_;
  
  trade_route_ = (select row_to_json(r) from (select tr.id, tr.resource_type as "resourceType", tr.quantity, tr.received_quantity as "receivedQuantity", tr.origin_planet as origin, tr.destination_planet as destination from core.trade_routes tr where id=trade_route_id_) as r);


  if (origin_civilization_ = destination_civilization_) then
    message_orders = format('[
      {"type": "FinishTradeRouteCreation", "payload": {"id": %s, "tradeRoute": %s}, "civilizations": [%s]}
    ]', id_, trade_route_, origin_civilization_);
  else
    message_orders = format('[
      {"type": "FinishTradeRouteCreation", "payload": {"id": %s, "tradeRoute": %s}, "civilizations": [%s, %s]}
    ]', id_, trade_route_, origin_civilization_, destination_civilization_);
  end if;

  return format('{"messageOrders": %s}', message_orders);

end;$function$;