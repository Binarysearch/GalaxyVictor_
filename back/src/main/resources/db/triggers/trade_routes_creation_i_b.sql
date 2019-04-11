CREATE OR REPLACE FUNCTION tg.trade_routes_creation_i_b()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin

  new.origin_planet = planet from core.colonies where id=new.origin;
  new.destination_planet = planet from core.colonies where id=new.destination;

  insert into core.colony_resources(colony, resource_type, quantity) values
    (new.origin, new.resource_type, -new.quantity);
  
  return new;
end;$function$;

CREATE TRIGGER trade_routes_creation_i_b BEFORE INSERT ON core.trade_routes_creation FOR EACH ROW EXECUTE PROCEDURE tg.trade_routes_creation_i_b();

