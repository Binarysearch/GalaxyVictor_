CREATE OR REPLACE FUNCTION test.finish_creation_trade_route_dto(data_ json)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin
  
  assert json_typeof(data_->'id') = 'number', 'trade_route.id not a number';
  
  perform test.trade_route_dto(data_->'tradeRoute');

end;$function$;