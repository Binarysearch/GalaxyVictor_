CREATE OR REPLACE FUNCTION test.create_civilization()
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
begin
  
  perform test.clear();
  
  insert into core.galaxies(id, name) values(1000, 'test_galaxy');
  insert into core.star_systems(id, galaxy,x,y,type,size,explored)
  values
    (1001, 1000, 0,12.5,5,8,false),
    (1002, 1000, 230,0,5,8,false),
    (1003, 1000, 10,17.5,5,8,false);

  insert into core.users(id, email, password, galaxy) values(1002,'email',crypt('12345', gen_salt('bf', 8)),1000);
  insert into core.sessions(id,usr) values('token', 1002);

  result_ = core.create_civilization('civ_name', 'home_star_name', 'token');
  perform test.user_civilization_dto(result_);

end;$function$;