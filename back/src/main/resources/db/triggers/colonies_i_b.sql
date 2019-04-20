CREATE OR REPLACE FUNCTION tg.colonies_i_b()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin

  insert into core.visible_star_systems(civilization, star_system) select new.civilization, p.star_system from core.planets p where p.id=new.planet;
  insert into core.stellar_governments(civilization, star_system) select new.civilization, p.star_system from core.planets p where p.id=new.planet;

  --eliminar cache de colonias de todas las civilizaciones que tengan visibilidad en el sistema
  update core.civilizations set colonies_cache=(random() * 1000000)::integer 
  where id in(select civilization from core.visible_star_systems where star_system=(select star_system from core.planets where id=new.planet));

  return new;
end;$function$;

CREATE TRIGGER colonies_i_b BEFORE INSERT ON core.colonies FOR EACH ROW EXECUTE PROCEDURE tg.colonies_i_b();

