

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

    select id, email, 
    (select row_to_json(cg) from (select id,name from core.galaxies where id=users.galaxy) as cg) as "currentGalaxy" 
    from core.users where id = user_id_

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

    select id, email, 
    (select row_to_json(cg) from (select id,name from core.galaxies where id=users.galaxy) as cg) as "currentGalaxy" 
    from core.users where id = user_id_

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



CREATE OR REPLACE FUNCTION core.get_planets(token_ text)
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


  result_ = (with ps as (

    select p.id, p.star_system as "starSystem", p.orbit, p.type, p.size from core.planets p 
    join core.known_star_systems k on k.star_system=p.star_system
    join core.civilizations c on k.civilization=c.id
    join core.users u on c.usr=u.id and c.galaxy=u.galaxy
    where u.id=user_id_

  ) select array_to_json(array_agg(ps)) from ps);

  return coalesce(result_, '[]')::json;
end;$function$;




CREATE OR REPLACE FUNCTION core.get_current_civilization(token_ text)
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

    select c.id, c.name, 
    (select row_to_json(h) from (select p.id, p.star_system as "starSystem", p.orbit, p.type, p.size from core.planets p where p.id=c.homeworld) as h) as homeworld 
    from core.civilizations c 
    join core.users u on u.id=c.usr and u.galaxy=c.galaxy
    where u.id=user_id_

  ) select row_to_json(ss) from ss);

  return coalesce(result_, '{}')::json;
end;$function$;




CREATE OR REPLACE FUNCTION core.create_civilization(name_ text, home_star_name_ text, token_ text)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  user_id_ bigint;
  galaxy_id_ bigint;
  star_system_ bigint;
  homeworld_id_ bigint;
  civilization_id_ bigint;
begin

  user_id_ = usr from core.sessions where id=token_;
  
  if (user_id_ is null) then
    perform core.error(401, 'Invalid token');
  end if;

  galaxy_id_ = galaxy from core.users where id=user_id_;

  if (galaxy_id_ is null) then
    perform core.error(400, 'User does not have selected galaxy');
  end if;

  star_system_ = core.find_random_unexplored_system(galaxy_id_);

  update core.star_systems set name = home_star_name_, explored=true where id=star_system_;

  insert into core.planets(star_system, orbit, type, size) values(star_system_, 3, 10, 3) returning id into homeworld_id_;

  insert into core.civilizations(galaxy, name, homeworld, usr) values(galaxy_id_, name_, homeworld_id_, user_id_) returning id into civilization_id_;

  insert into core.known_star_systems(civilization, star_system) values(civilization_id_, star_system_);

  insert into core.known_civilizations(knows, known) values(civilization_id_, civilization_id_);

  return core.get_current_civilization(token_);
end;$function$;




CREATE OR REPLACE FUNCTION core.find_random_unexplored_system(galaxy_ bigint)
 RETURNS bigint
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ bigint;
  count_ bigint;
begin

  count_ = (select count(*) from core.star_systems where not explored and galaxy=galaxy_);

  result_ = (SELECT id FROM core.star_systems OFFSET floor(random()*count_) LIMIT 1);
  
  return result_;
end;$function$;




CREATE OR REPLACE FUNCTION core.get_civilizations(token_ text)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
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

  result_ = (with cs as (

    select c.id, c.name, c.homeworld from core.civilizations c join core.known_civilizations k on k.known=c.id where k.knows=civilization_id_

  ) select array_to_json(array_agg(cs)) from cs);

  return coalesce(result_, '[]')::json;
end;$function$;