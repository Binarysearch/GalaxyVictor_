CREATE OR REPLACE FUNCTION test.remove_fleet_dto(data_ json)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin
  
  assert json_typeof(data_->'id') = 'number', 'remove_fleet.id not a number';

end;$function$;