CREATE OR REPLACE FUNCTION test.planet_dto(data_ json)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin
  
  assert json_typeof(data_->'id') = 'number', 'planet.id not a number';
  assert json_typeof(data_->'starSystem') = 'number', 'planet.starSystem not a number';
  assert json_typeof(data_->'type') = 'number', 'planet.type not a number';
  assert json_typeof(data_->'size') = 'number', 'planet.size not a number';
  assert json_typeof(data_->'orbit') = 'number', 'planet.orbit not a number';

end;$function$;