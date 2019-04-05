CREATE OR REPLACE FUNCTION test.get_civilization_research_orders()
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  token_ text;
begin
  
  token_ = test.civ_test_bed('token');
  
  insert into core.stellar_governments(star_system,civilization) select ss.id,c.id from core.civilizations c cross join core.star_systems ss;
  insert into core.research_orders(stellar_government, technology, started_time) select id,'fusion',123456 from core.stellar_governments;

  result_ = core.get_civilization_research_orders('token');
  perform test.research_order_dto(result_->0);
  perform test.research_order_dto(result_->1);

end;$function$;