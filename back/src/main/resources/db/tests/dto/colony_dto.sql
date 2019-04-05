CREATE OR REPLACE FUNCTION test.colony_dto(data_ json)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin
  
  assert json_typeof(data_->'id') = 'number', 'colony.id not a number';
  assert json_typeof(data_->'planet') = 'number', 'colony.planet not a number';
  assert json_typeof(data_->'civilization') = 'number', 'colony.civilization not a number';

end;$function$;