CREATE OR REPLACE FUNCTION tg.trade_routes_d_b()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin

  insert into core.colony_resources(colony, resource_type, quantity) values
    (old.origin, old.resource_type, old.quantity),
    (old.destination, old.resource_type, -old.received_quantity);
  
  return old;
end;$function$;

CREATE TRIGGER trade_routes_d_b BEFORE DELETE ON core.trade_routes FOR EACH ROW EXECUTE PROCEDURE tg.trade_routes_d_b();

