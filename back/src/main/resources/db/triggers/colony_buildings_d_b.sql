CREATE OR REPLACE FUNCTION tg.colony_buildings_d_b()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin

  insert into core.colony_resources(colony, resource_type, quantity) 
  select old.colony, resource_type, -quantity from core.colony_building_types_resources where building_type=old.building_type;

  return old;
end;$function$;

CREATE TRIGGER colony_buildings_d_b BEFORE DELETE ON core.colony_buildings FOR EACH ROW EXECUTE PROCEDURE tg.colony_buildings_d_b();