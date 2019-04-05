CREATE OR REPLACE FUNCTION test.fleet_dto(data_ json)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin
  
  assert json_typeof(data_->'id') = 'number', 'fleet.id not a number';
  assert json_typeof(data_->'civilization') = 'number', 'fleet.civilization not a number';
  assert json_typeof(data_->'destination') = 'number', 'fleet.destination not a number';
  assert json_typeof(data_->'origin') = 'number', 'fleet.origin not a number';
  assert json_typeof(data_->'travelStartTime') = 'number', 'fleet.travelStartTime not a number';
  assert json_typeof(data_->'canColonize') = 'boolean', 'fleet.canColonize not a boolean';

end;$function$;