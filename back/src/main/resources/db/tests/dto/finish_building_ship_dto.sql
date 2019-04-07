CREATE OR REPLACE FUNCTION test.finish_building_ship_dto(data_ json)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin
  
  assert json_typeof(data_->'colony') = 'number', 'finish_building_ship.colony not a number';

end;$function$;