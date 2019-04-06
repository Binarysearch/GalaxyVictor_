CREATE OR REPLACE FUNCTION test.civ_test_bed()
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  id_ bigint;
  galaxy_ bigint;
begin
  
  perform test.clear();
  
  galaxy_ = core.create_galaxy('test_galaxy', 10, 1);

  FOR id_ IN 1..3 LOOP
    insert into core.users(id, email, password, galaxy) values(id_,'email'||id_,crypt('12345', gen_salt('bf', 8)),galaxy_);
    insert into core.sessions(id,usr) values('token_'||id_, id_);
    perform core.create_civilization('civ_'||id_||'_name', 'civ_'||id_||'_home_star', 'token_'||id_);
    update core.civilizations set id=id_ where name='civ_'||id_||'_name';
    update core.star_systems set id=id_ where name='civ_'||id_||'_home_star';
  END LOOP;

end;$function$;