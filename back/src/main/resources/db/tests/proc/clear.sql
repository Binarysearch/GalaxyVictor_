CREATE OR REPLACE FUNCTION test.clear()
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin
  
  truncate table core.galaxies CASCADE;
  truncate table core.users CASCADE;
  
end;$function$;