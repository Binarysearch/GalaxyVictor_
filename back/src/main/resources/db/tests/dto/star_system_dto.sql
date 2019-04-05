CREATE OR REPLACE FUNCTION test.star_system_dto(data_ json)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin
  
  assert json_typeof(data_->'id') = 'number', 'star_system.id not a number';
  assert json_typeof(data_->'name') = 'string', 'star_system.name not a string';
  assert json_typeof(data_->'x') = 'number', 'star_system.x not a number';
  assert json_typeof(data_->'y') = 'number', 'star_system.y not a number';
  assert json_typeof(data_->'type') = 'number', 'star_system.type not a number';
  assert json_typeof(data_->'size') = 'number', 'star_system.size not a number';
  assert json_typeof(data_->'explored') = 'boolean', 'star_system.explored not a boolean';

end;$function$;