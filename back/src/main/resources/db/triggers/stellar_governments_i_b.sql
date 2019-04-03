CREATE OR REPLACE FUNCTION tg.stellar_governments_i_b()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
begin

  if (exists(select 1 from core.stellar_governments where civilization=new.civilization and star_system=new.star_system)) then
    update core.stellar_governments set quantity=quantity+1 where civilization=new.civilization and star_system=new.star_system;
    return null;
  end if;

  return new;
end;$function$;

CREATE TRIGGER stellar_governments_i_b BEFORE INSERT ON core.stellar_governments FOR EACH ROW EXECUTE PROCEDURE tg.stellar_governments_i_b();