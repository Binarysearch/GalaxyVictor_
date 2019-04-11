CREATE OR REPLACE FUNCTION tg.trade_routes_i_b()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  origin_ss_ bigint;
  destination_ss_ bigint;
begin

  new.origin_planet = planet from core.colonies where id=new.origin;
  new.destination_planet = planet from core.colonies where id=new.destination;

  origin_ss_ = star_system from core.planets where id=new.origin_planet;
  destination_ss_ = star_system from core.planets where id=new.destination_planet;
  
  --if destination star system = origin star system received quantity = 0.9 * quantity
  if (origin_ss_ = destination_ss_) then
    new.received_quantity = floor(new.quantity * 0.9);
  else
    new.received_quantity = floor(new.quantity * 0.8);
  end if;

  insert into core.colony_resources(colony, resource_type, quantity) values
    (new.origin, new.resource_type, -new.quantity),
    (new.destination, new.resource_type, new.received_quantity);
  
  return new;
end;$function$;

CREATE TRIGGER trade_routes_i_b BEFORE INSERT ON core.trade_routes FOR EACH ROW EXECUTE PROCEDURE tg.trade_routes_i_b();

