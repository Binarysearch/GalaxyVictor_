CREATE OR REPLACE FUNCTION test.session_dto(data_ json)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin
  
  assert json_typeof(data_->'token') = 'string', 'session.token not a string';
  assert json_typeof((data_->'user')->'id') = 'number', 'session.user.id not a number';
  assert json_typeof((data_->'user')->'email') = 'string', 'session.user.email not a string';
  
  if (json_typeof((data_->'user')->'currentGalaxy') <> 'null') then
    perform test.galaxy_dto((data_->'user')->'currentGalaxy');
  end if;

end;$function$;