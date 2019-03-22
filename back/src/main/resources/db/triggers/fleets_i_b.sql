CREATE OR REPLACE FUNCTION tg.fleets_i_b()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin

  insert into core.visible_star_systems(civilization, star_system) values(new.civilization, new.destination);

  return new;
end;$function$;

CREATE TRIGGER fleets_i_b BEFORE INSERT ON core.fleets FOR EACH ROW EXECUTE PROCEDURE tg.fleets_i_b();