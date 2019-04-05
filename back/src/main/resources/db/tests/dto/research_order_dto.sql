CREATE OR REPLACE FUNCTION test.research_order_dto(data_ json)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin
  
  assert json_typeof(data_->'starSystem') = 'number', 'research_order.starSystem not a number';
  assert json_typeof(data_->'technology') = 'string', 'research_order.technology not a string';
  assert json_typeof(data_->'startedTime') = 'number', 'research_order.startedTime not a number';
  assert json_typeof(data_->'finishTime') = 'number', 'research_order.finishTime not a number';

end;$function$;