CREATE OR REPLACE FUNCTION test.asinc_task_order_dto(data_ json)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin
  
  assert json_typeof(data_->'id') = 'number', 'asinc_task_order.id not a number';
  assert json_typeof(data_->'endTime') = 'number', 'asinc_task_order.endTime not a number';
  assert json_typeof(data_->'procedureName') = 'string', 'asinc_task_order.procedureName not a string';

end;$function$;