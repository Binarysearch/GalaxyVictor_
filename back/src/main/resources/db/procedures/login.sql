CREATE OR REPLACE FUNCTION core.login(email_ text, password_ text)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  user_ json;
  user_id_ bigint;
  token_ text;
begin

  user_id_ = id from core.users where email=email_ and password = crypt(password_, password);
  
  if (user_id_ is null) then
    perform core.error(401, 'Incorrect email or password');
  end if;

  token_ = core.random_string(40);

  INSERT INTO core.sessions(id, usr) VALUES(token_, user_id_);

  user_ = (with x as (

    select id, email, 
    (select row_to_json(cg) from (select id,name from core.galaxies where id=users.galaxy) as cg) as "currentGalaxy" 
    from core.users where id = user_id_

  ) select row_to_json(x) from x);

  return format('{"token": "%s", "user": %s}', token_, user_)::json;
end;$function$;