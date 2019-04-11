CREATE OR REPLACE FUNCTION test.creation_trade_route_dto(data_ json)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin
  
  assert json_typeof(data_->'id') = 'number', 'creation_trade_route.id not a number';
  assert json_typeof(data_->'resourceType') = 'string', 'creation_trade_route.resourceType not a string';
  assert json_typeof(data_->'quantity') = 'number', 'creation_trade_route.quantity not a number';
  assert json_typeof(data_->'startedTime') = 'number', 'creation_trade_route.startedTime not a number';
  assert json_typeof(data_->'finishTime') = 'number', 'creation_trade_route.finishTime not a number';
  assert json_typeof(data_->'origin') = 'number', 'creation_trade_route.origin not a number';
  assert json_typeof(data_->'destination') = 'number', 'creation_trade_route.destination not a number';

end;$function$;