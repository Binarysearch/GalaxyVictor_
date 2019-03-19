
DROP SCHEMA IF EXISTS tg CASCADE;

CREATE SCHEMA tg;

SET search_path TO tg;





CREATE OR REPLACE FUNCTION tg.colonies_i_b()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin

  insert into core.visible_star_systems(civilization, star_system) select new.civilization, p.star_system from core.planets p where p.id=new.planet;

  return new;
end;$function$;

CREATE TRIGGER colonies_i_b BEFORE INSERT ON core.colonies FOR EACH ROW EXECUTE PROCEDURE tg.colonies_i_b();






CREATE OR REPLACE FUNCTION tg.fleets_i_b()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin

  insert into core.visible_star_systems(civilization, star_system) values(new.civilization, new.destination);

  return new;
end;$function$;

CREATE TRIGGER fleets_i_b BEFORE INSERT ON core.fleets FOR EACH ROW EXECUTE PROCEDURE tg.fleets_i_b();








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

