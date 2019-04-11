CREATE OR REPLACE FUNCTION test.destruction_trade_route_dto(data_ json)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin
  
  assert json_typeof(data_->'id') = 'number', 'destruction_trade_route.id not a number';
  assert json_typeof(data_->'resourceType') = 'string', 'destruction_trade_route.resourceType not a string';
  assert json_typeof(data_->'receivedQuantity') = 'number', 'destruction_trade_route.receivedQuantity not a number';
  assert json_typeof(data_->'finishTime') = 'number', 'destruction_trade_route.finishTime not a number';
  assert json_typeof(data_->'origin') = 'number', 'destruction_trade_route.origin not a number';
  assert json_typeof(data_->'destination') = 'number', 'destruction_trade_route.destination not a number';

end;$function$;