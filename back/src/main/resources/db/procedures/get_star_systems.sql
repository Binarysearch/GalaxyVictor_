CREATE OR REPLACE FUNCTION core.get_star_systems(token_ text)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  user_id_ bigint;
begin

  user_id_ = usr from core.sessions where id=token_;
  
  if (user_id_ is null) then
    perform core.error(401, 'Invalid token');
  end if;


  result_ = (with ss as (

    select id, name, x, y, type, size, explored from core.star_systems where galaxy=(select galaxy from core.users where id=user_id_)

  ) select array_to_json(array_agg(ss)) from ss);

  return coalesce(result_, '[]')::json;
end;$function$;