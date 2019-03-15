

CREATE OR REPLACE FUNCTION core.get_civilization_by_token(token_ text)
 RETURNS bigint
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ bigint;
begin

  result_ = 1;

  return result_;
end;$function$;



CREATE OR REPLACE FUNCTION core.register(email_ text, password_ text)
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

  return core.login(email_, password_);
end;$function$;


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

    select id, email from core.users where id = user_id_

  ) select row_to_json(x) from x);

  return format('{"token": "%s", "user": %s}', token_, user_)::json;
end;$function$;




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

    select id, email from core.users where id = user_id_

  ) select row_to_json(x) from x);

  return format('{"token": "%s", "user": %s}', token_, user_)::json;
end;$function$;


CREATE OR REPLACE FUNCTION core.get_galaxies(token_ text)
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


  result_ = (with x as (

    select id, name from core.galaxies

  ) select array_to_json(array_agg(x)) from x);

  return coalesce(result_, '[]')::json;
end;$function$;





CREATE OR REPLACE FUNCTION core.error(code_ integer, message_ text, data_ json)
 RETURNS boolean
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin
  RAISE exception using errcode=format('GV%s', code_), message=format('{"data":%s, "message":"%s"}', data_::text, message_);
end;$function$;

CREATE OR REPLACE FUNCTION core.error(code_ integer, message_ text)
 RETURNS boolean
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin
  RAISE exception using errcode=format('DC%s', code_), message=format('{"data":{}, "message":"%s"}', message_);
end;$function$;

CREATE OR REPLACE FUNCTION core.random_string(length integer)
 RETURNS text
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  chars text[] := '{0,1,2,3,4,5,6,7,8,9,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z}';
  result text := '';
  i integer := 0;
begin
  if length < 0 then
    raise exception 'Length must be positive';
  end if;
  for i in 1..length loop
    result := result || chars[1+random()*(array_length(chars, 1)-1)];
  end loop;
  return result;
end;$function$;