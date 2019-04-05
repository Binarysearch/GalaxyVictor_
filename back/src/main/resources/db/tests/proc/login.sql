CREATE OR REPLACE FUNCTION test.login()
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
begin
  
  perform test.clear();
  
  insert into core.galaxies(id, name) values(1000, 'test_galaxy');
  insert into core.users(id, email, password, galaxy) values(1001,'email',crypt('12345', gen_salt('bf', 8)),1000);
  insert into core.sessions(id,usr) values('token', 1001);

  result_ = core.login('email', '12345');
  perform test.session_dto(result_);

end;$function$;