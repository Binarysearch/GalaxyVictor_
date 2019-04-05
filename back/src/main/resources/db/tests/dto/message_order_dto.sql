CREATE OR REPLACE FUNCTION test.message_order_dto(data_ json)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin
  
  assert json_typeof(data_->'type') = 'string', 'message_order.type not a string';
  assert json_typeof(data_->'payload') = 'object', 'message_order.payload not an object';
  assert json_typeof(data_->'civilizations') = 'array', 'message_order.civilizations not an array';
  assert json_typeof(data_ #> '{civilizations, 0}') = 'number', 'message_order.civilizations[0] not a number';

end;$function$;