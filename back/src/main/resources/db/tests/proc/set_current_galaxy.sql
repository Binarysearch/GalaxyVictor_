CREATE OR REPLACE FUNCTION test.set_current_galaxy()
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
begin
  
  insert into core.galaxies(id, name) values(1000, 'test_galaxy');
  insert into core.users(id, email, password) values(1001,'test','test');
  insert into core.sessions(id,usr) values('token', 1001);

  result_ = core.set_current_galaxy(1000, 'token');
  perform test.galaxy_dto(result_);

end;$function$;