CREATE OR REPLACE FUNCTION tg.colonies_d_b()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  star_system_ bigint;
begin

  star_system_ = star_system from core.planets where id=old.planet;

  --eliminar cache de colonias de todas las civilizaciones que tengan visibilidad en el sistema
  update core.civilizations set colonies_cache=(random() * 1000000)::integer 
  where id in(select civilization from core.visible_star_systems where star_system=(select star_system from core.planets where id=old.planet));

  delete from core.visible_star_systems where star_system=star_system_ and civilization=old.civilization;
  delete from core.stellar_governments where star_system=star_system_ and civilization=old.civilization;


  return old;
end;$function$;

CREATE TRIGGER colonies_d_b BEFORE DELETE ON core.colonies FOR EACH ROW EXECUTE PROCEDURE tg.colonies_d_b();

