CREATE OR REPLACE FUNCTION core.get_civilization_by_token(token_ text)
 RETURNS bigint
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  user_id_ bigint;
  civilization_id_ bigint;
begin

  user_id_ = usr from core.sessions where id=token_;
  
  if (user_id_ is null) then
    perform core.error(401, 'Invalid token');
  end if;

  civilization_id_ = (select c.id from core.civilizations c join core.users u on u.galaxy=c.galaxy and u.id=c.usr where u.id=user_id_);

  if (civilization_id_ is null) then
    perform core.error(400, 'User does not have civilization or galaxy selected');
  end if;

  return civilization_id_;
end;$function$;