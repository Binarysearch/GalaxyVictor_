CREATE OR REPLACE FUNCTION tg.colonies_u_b()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  star_system_ bigint; 
begin

  if (old.civilization <> new.civilization) then
    star_system_ = star_system from core.planets where id=old.planet;

    insert into core.visible_star_systems(civilization, star_system) values(new.civilization, star_system_);
    insert into core.stellar_governments(civilization, star_system) values(new.civilization, star_system_);

    delete from core.visible_star_systems where star_system=star_system_ and civilization=old.civilization;
    delete from core.stellar_governments where star_system=star_system_ and civilization=old.civilization;
  end if;

  return new;
end;$function$;

CREATE TRIGGER colonies_u_b BEFORE UPDATE ON core.colonies FOR EACH ROW EXECUTE PROCEDURE tg.colonies_u_b();

