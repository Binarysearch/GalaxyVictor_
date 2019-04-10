CREATE OR REPLACE FUNCTION tg.trade_routes_destruction_d_b()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin

  insert into core.colony_resources(colony, resource_type, quantity) values
    (old.destination, old.resource_type, -old.received_quantity);
  
  return old;
end;$function$;

CREATE TRIGGER trade_routes_destruction_d_b BEFORE DELETE ON core.trade_routes_destruction FOR EACH ROW EXECUTE PROCEDURE tg.trade_routes_destruction_d_b();

