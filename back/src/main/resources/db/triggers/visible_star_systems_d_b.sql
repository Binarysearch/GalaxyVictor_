CREATE OR REPLACE FUNCTION tg.visible_star_systems_d_b()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
begin

  if (old.quantity > 1) then
    update core.visible_star_systems set quantity=quantity-1 where civilization=old.civilization and star_system=old.star_system;
    return null;
  end if;

  --si hay colonias eliminar cache de colonias para la civilizacion que elimina visibilidad
  if (exists(select 1 from core.colonies where planet in(select id from core.planets where star_system=old.star_system))) then

    update core.civilizations set colonies_cache=(random() * 1000000)::integer where id = old.civilization;

  end if;

  return old;
end;$function$;

CREATE TRIGGER visible_star_systems_d_b BEFORE DELETE ON core.visible_star_systems FOR EACH ROW EXECUTE PROCEDURE tg.visible_star_systems_d_b();