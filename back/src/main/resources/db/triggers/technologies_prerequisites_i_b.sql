CREATE OR REPLACE FUNCTION tg.technologies_prerequisites_i_b()
 RETURNS trigger
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin

  if ((select level from core.technologies where id=new.technology) <= (select level from core.technologies where id=new.prerequisite)) then
    perform core.error(500, 'One technology can not require an upper-level technology');
  end if;

  return new;
end;$function$;

CREATE TRIGGER technologies_prerequisites_i_b BEFORE INSERT ON core.technologies_prerequisites FOR EACH ROW EXECUTE PROCEDURE tg.technologies_prerequisites_i_b();

