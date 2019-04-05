CREATE OR REPLACE FUNCTION test.galaxy_dto(data_ json)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin
  
  assert json_typeof(data_->'id') = 'number', 'galaxy.id not a number';
  assert json_typeof(data_->'name') = 'string', 'galaxy.name not a string';

end;$function$;