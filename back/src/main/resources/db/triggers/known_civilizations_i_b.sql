CREATE OR REPLACE FUNCTION tg.known_civilizations_i_b()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
begin

  update core.civilizations set civilizations_cache = (random()*1000000)::integer where id=new.knows;

  return new;
end;$function$;

CREATE TRIGGER known_civilizations_i_b BEFORE INSERT ON core.known_civilizations FOR EACH ROW EXECUTE PROCEDURE tg.known_civilizations_i_b();