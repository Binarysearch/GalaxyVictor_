CREATE OR REPLACE FUNCTION tg.known_star_systems_i_b()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
begin

  update core.civilizations set planets_cache = (random()*1000000)::integer where id=new.civilization;

  return new;
end;$function$;

CREATE TRIGGER known_star_systems_i_b BEFORE INSERT ON core.known_star_systems FOR EACH ROW EXECUTE PROCEDURE tg.known_star_systems_i_b();