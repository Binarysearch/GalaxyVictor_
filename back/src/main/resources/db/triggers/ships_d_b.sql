CREATE OR REPLACE FUNCTION tg.ships_d_b()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin

  if (old.can_colonize) then
    update core.fleets set colony_ships = colony_ships - 1 where id=old.fleet;
  end if;

  return new;
end;$function$;

CREATE TRIGGER ships_d_b BEFORE DELETE ON core.ships FOR EACH ROW EXECUTE PROCEDURE tg.ships_d_b();