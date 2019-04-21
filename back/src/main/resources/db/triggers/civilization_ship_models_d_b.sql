CREATE OR REPLACE FUNCTION tg.civilization_ship_models_d_b()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
begin

  update core.civilizations set ship_models_cache = (random()*1000000)::integer where id=old.civilization;

  return old;
end;$function$;

CREATE TRIGGER civilization_ship_models_d_b BEFORE DELETE ON core.civilization_ship_models FOR EACH ROW EXECUTE PROCEDURE tg.civilization_ship_models_d_b();