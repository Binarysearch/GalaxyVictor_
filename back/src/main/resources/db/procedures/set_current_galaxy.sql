CREATE OR REPLACE FUNCTION core.set_current_galaxy(id_ bigint, token_ text)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  user_id_ bigint;
  galaxy_id_ bigint;
begin

  user_id_ = usr from core.sessions where id=token_;
  
  if (user_id_ is null) then
    perform core.error(401, 'Invalid token');
  end if;

  galaxy_id_ = id from core.galaxies where id=id_;

  if (user_id_ is null) then
    perform core.error(404, 'Galaxy not found');
  end if;

  update core.users set galaxy = galaxy_id_ where id=user_id_;

  result_ = (with x as (

    select id, name from core.galaxies where id=galaxy_id_

  ) select row_to_json(x) from x);

  return coalesce(result_, '{}')::json;
end;$function$;