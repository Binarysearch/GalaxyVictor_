CREATE OR REPLACE FUNCTION core.login(email_ text, password_ text, time_ bigint)
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

    select u.id, u.email,
    (select row_to_json(cg) from (select id,name from core.galaxies where id=u.galaxy) as cg) as "currentGalaxy",
    (select row_to_json(cc) from (

    select c.id, c.name, 
    (select row_to_json(h) from (select p.id, p.star_system as "starSystem", p.orbit, p.type, p.size from core.planets p where p.id=c.homeworld) as h) as homeworld 
    from core.civilizations c 
    where c.usr=user_id_ and c.galaxy=u.galaxy

    ) as cc) as "currentCivilization" 
    from core.users u where u.id = user_id_

  ) select row_to_json(x) from x);

  return format('{"token": "%s", "user": %s, "serverTime": %s}', token_, user_, time_)::json;
end;$function$;