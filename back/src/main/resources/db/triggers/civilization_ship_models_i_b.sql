CREATE OR REPLACE FUNCTION tg.civilization_ship_models_i_b()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
begin

  update core.civilizations set ship_models_cache = (random()*1000000)::integer where id=new.civilization;

  return new;
end;$function$;

CREATE TRIGGER civilization_ship_models_i_b BEFORE INSERT ON core.civilization_ship_models FOR EACH ROW EXECUTE PROCEDURE tg.civilization_ship_models_i_b();