CREATE OR REPLACE FUNCTION tg.star_systems_i_b()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  name_ text := '';
begin

  while (exists(select 1 from core.star_systems where galaxy=new.galaxy and name=name_)) loop
    name_ = core.random_star_name();
  end loop;
  
  new.name = name_;

  return new;
end;$function$;

CREATE TRIGGER star_systems_i_b BEFORE INSERT ON core.star_systems FOR EACH ROW EXECUTE PROCEDURE tg.star_systems_i_b();

