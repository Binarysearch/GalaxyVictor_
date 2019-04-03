CREATE OR REPLACE FUNCTION tg.visible_star_systems_i_b()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
begin

  if (exists(select 1 from core.visible_star_systems where civilization=new.civilization and star_system=new.star_system)) then
    update core.visible_star_systems set quantity=quantity+1 where civilization=new.civilization and star_system=new.star_system;
    return null;
  end if;

  return new;
end;$function$;

CREATE TRIGGER visible_star_systems_i_b BEFORE INSERT ON core.visible_star_systems FOR EACH ROW EXECUTE PROCEDURE tg.visible_star_systems_i_b();