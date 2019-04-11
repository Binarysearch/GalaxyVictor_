CREATE OR REPLACE FUNCTION test.trade_route_dto(data_ json)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin
  
  assert json_typeof(data_->'id') = 'number', 'trade_route.id not a number';
  assert json_typeof(data_->'resourceType') = 'string', 'trade_route.resourceType not a string';
  assert json_typeof(data_->'quantity') = 'number', 'trade_route.quantity not a number';
  assert json_typeof(data_->'receivedQuantity') = 'number', 'trade_route.receivedQuantity not a number';
  assert json_typeof(data_->'origin') = 'number', 'trade_route.origin not a number';
  assert json_typeof(data_->'destination') = 'number', 'trade_route.destination not a number';

end;$function$;