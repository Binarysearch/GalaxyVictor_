CREATE OR REPLACE FUNCTION core.register(email_ text, password_ text, time_ bigint)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  id_ bigint;
begin

  if (exists(select 1 from core.users where email=email_)) then
    perform core.error(409, 'Duplicated email', format('{"email": "%s"}', email_)::json);
  end if;

  INSERT INTO core.users(email, password) VALUES(email_, crypt(password_, gen_salt('bf', 8))) RETURNING id INTO id_;

  return core.login(email_, password_, time_);
end;$function$;