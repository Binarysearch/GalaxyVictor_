CREATE OR REPLACE FUNCTION test.register()
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  email_ text := 'test@test.com';
  password_ text := '12345';
begin
  
  perform test.clear();

  result_ = core.register(email_, password_, 12345);
  perform test.session_dto(result_);
  assert json_typeof((result_->'user')->'currentGalaxy') = 'null', 'CURRENT GALAXY MUST BE NULL AFTER REGISTER';

end;$function$;