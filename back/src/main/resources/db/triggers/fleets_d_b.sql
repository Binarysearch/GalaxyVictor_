CREATE OR REPLACE FUNCTION tg.fleets_d_b()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin

  if (old.origin=old.destination) then
    delete from core.visible_star_systems where star_system=old.destination and civilization=old.civilization;
  end if;

  return old;
end;$function$;

CREATE TRIGGER fleets_d_b BEFORE DELETE ON core.fleets FOR EACH ROW EXECUTE PROCEDURE tg.fleets_d_b();