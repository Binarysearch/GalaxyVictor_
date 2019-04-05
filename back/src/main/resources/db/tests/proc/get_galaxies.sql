CREATE OR REPLACE FUNCTION test.get_galaxies()
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
begin
  
  perform test.clear();
  
  insert into core.galaxies(id, name) values(1000, 'test_galaxy');
  insert into core.galaxies(id, name) values(1001, 'test_galaxy2');
  insert into core.users(id, email, password, galaxy) values(1002,'email',crypt('12345', gen_salt('bf', 8)),1000);
  insert into core.sessions(id,usr) values('token', 1002);

  result_ = core.get_galaxies('token');
  perform test.galaxy_dto(result_->0);
  perform test.galaxy_dto(result_->1);

end;$function$;