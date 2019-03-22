CREATE OR REPLACE FUNCTION tg.colony_resources_i_b()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin

  if (exists(select 1 from core.colony_resources where colony=new.colony and resource_type=new.resource_type)) then
    update core.colony_resources set quantity=quantity+new.quantity where colony=new.colony and resource_type=new.resource_type;
    return null;
  end if;

  return new;
end;$function$;

CREATE TRIGGER colony_resources_i_b BEFORE INSERT ON core.colony_resources FOR EACH ROW EXECUTE PROCEDURE tg.colony_resources_i_b();