CREATE OR REPLACE FUNCTION tg.ships_u_b()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin

  if (new.can_colonize and (new.fleet <> old.fleet)) then
    update core.fleets set colony_ships = colony_ships - 1 where id=old.fleet;
    update core.fleets set colony_ships = colony_ships + 1 where id=new.fleet;
  end if;

  return new;
end;$function$;

CREATE TRIGGER ships_u_b BEFORE UPDATE ON core.ships FOR EACH ROW EXECUTE PROCEDURE tg.ships_u_b();