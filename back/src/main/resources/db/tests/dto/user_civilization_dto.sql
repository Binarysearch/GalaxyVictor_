CREATE OR REPLACE FUNCTION test.user_civilization_dto(data_ json)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin
  
  assert json_typeof(data_->'id') = 'number', 'user_civilization.id not a number';
  assert json_typeof(data_->'name') = 'string', 'user_civilization.name not a string';
  
  perform test.planet_dto(data_->'homeworld');

end;$function$;