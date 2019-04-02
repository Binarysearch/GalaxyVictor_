CREATE OR REPLACE FUNCTION tg.ships_i_b()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin

  if (new.can_colonize) then
    update core.fleets set colony_ships = colony_ships + 1 where id=new.fleet;
  end if;

  return new;
end;$function$;

CREATE TRIGGER ships_i_b BEFORE INSERT ON core.ships FOR EACH ROW EXECUTE PROCEDURE tg.ships_i_b();