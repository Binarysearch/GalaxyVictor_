CREATE OR REPLACE FUNCTION core.auth(token_ text)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  user_ json;
  user_id_ bigint;
begin

  user_id_ = usr from core.sessions where id=token_;
  
  if (user_id_ is null) then
    perform core.error(401, 'Invalid token');
  end if;


  user_ = (with x as (

    select id, email, 
    (select row_to_json(cg) from (select id,name from core.galaxies where id=users.galaxy) as cg) as "currentGalaxy" 
    from core.users where id = user_id_

  ) select row_to_json(x) from x);

  return format('{"token": "%s", "user": %s}', token_, user_)::json;
end;$function$;