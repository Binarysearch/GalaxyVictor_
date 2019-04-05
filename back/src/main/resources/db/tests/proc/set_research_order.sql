CREATE OR REPLACE FUNCTION test.set_research_order()
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  token_ text;
  stellar_government_ bigint;
begin
  
  token_ = test.civ_test_bed('token');

  stellar_government_ = id from core.stellar_governments where star_system=2 and civilization=1;

  result_ = core.set_research_order(2, 'fusion', 12345, 'token');
  perform test.research_order_dto(result_ #> '{messageOrders, 0, payload}');

  assert (result_ #>> '{messageOrders, 0, civilizations, 0}')::numeric = 1, 'Message sent to other civilization';
  assert (result_ #>> '{messageOrders, 0, civilizations, 1}') is null, 'Message sent to other civilization';
  
  assert (result_ #>> '{messageOrders, 0, type}') = 'ResearchOrder', 'Message is not ResearchOrder';
  
  assert (result_ #>> '{messageOrders, 1}') is null, 'Sent too many messages';

  assert (result_ #>> '{asincTasks, 0, id}') = (result_ #>> '{asincTaskCancelOrders, 0}'), 'Cancel order id and new order id does not match';

  assert (result_ #>> '{asincTasks, 0, id}')::numeric = stellar_government_, 'Incorrect stellar_government';

  assert (result_ #>> '{asincTasks, 0, id}')::numeric = stellar_government_, 'Incorrect stellar_government';
  assert (result_ #>> '{asincTasks, 0, procedureName}') = 'core.finish_research_order', 'Incorrect callback name';
  
  assert (result_ #>> '{asincTasks, 1}') is null, 'Created too many asinc tasks';


  assert (exists(select 1 from core.research_orders where stellar_government=stellar_government_ and technology='fusion')), 'Research Order not saved correctly';

end;$function$;