CREATE OR REPLACE FUNCTION tg.research_orders_i_b()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
begin

  update core.civilizations set research_orders_cache = (random()*1000000)::integer where id=(select civilization from core.stellar_governments where id=new.stellar_government);

  return new;
end;$function$;

CREATE TRIGGER research_orders_i_b BEFORE INSERT ON core.research_orders FOR EACH ROW EXECUTE PROCEDURE tg.research_orders_i_b();