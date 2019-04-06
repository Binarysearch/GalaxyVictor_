CREATE OR REPLACE FUNCTION test.get_current_civilization()
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  token_ text;
begin
  
  token_ = test.civ_test_bed();
  
  result_ = core.get_current_civilization('token_1');
  perform test.user_civilization_dto(result_);

end;$function$;