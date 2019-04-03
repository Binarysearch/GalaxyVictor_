CREATE OR REPLACE FUNCTION tg.stellar_governments_d_b()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
begin

  if (old.quantity > 1) then
    update core.stellar_governments set quantity=quantity-1 where civilization=old.civilization and star_system=old.star_system;
    return null;
  end if;

  return old;
end;$function$;

CREATE TRIGGER stellar_governments_d_b BEFORE DELETE ON core.stellar_governments FOR EACH ROW EXECUTE PROCEDURE tg.stellar_governments_d_b();