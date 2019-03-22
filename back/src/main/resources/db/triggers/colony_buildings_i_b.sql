CREATE OR REPLACE FUNCTION tg.colony_buildings_i_b()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin

  insert into core.colony_resources(colony, resource_type, quantity) 
  select new.colony, resource_type, quantity from core.colony_building_types_resources where building_type=new.building_type;

  return new;
end;$function$;

CREATE TRIGGER colony_buildings_i_b BEFORE INSERT ON core.colony_buildings FOR EACH ROW EXECUTE PROCEDURE tg.colony_buildings_i_b();