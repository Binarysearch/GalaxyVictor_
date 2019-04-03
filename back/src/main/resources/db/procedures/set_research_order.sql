CREATE OR REPLACE FUNCTION core.set_research_order(star_system_ bigint, technology_ text, time_ bigint, token_ text)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  civilization_id_ bigint;
  stellar_government_ bigint;
  message_orders json;
  asinc_tasks json;
begin

  civilization_id_ = core.require_civ(token_);

  -- if civilization does not have stellar government
  stellar_government_ = id from core.stellar_governments where star_system=star_system_ and civilization=civilization_id_;
  if (stellar_government_ is null) then
    perform core.error(401, format('Star system %L not yours', star_system_));
  end if;

  --check prerequisites
  if (exists(select 1 from core.technologies_prerequisites where technology=technology_ and prerequisite not in(select technology from core.stellar_governments_technologies where stellar_government=stellar_government_))) then
    perform core.error(400, 'Technological prerequisites not met');
  end if;

  update core.research_orders set technology = technology_, started_time=time_ where stellar_government=stellar_government_;
  insert into core.research_orders(stellar_government, technology, started_time) 
    select stellar_government_, technology_, time_ 
    where not exists(select 1 from core.research_orders where stellar_government=stellar_government_);
  

  result_ = (with x as (

    select star_system_ as "starSystem", technology_ as "technology", time_ as "startedTime"

  ) select row_to_json(x) from x);

  message_orders = format('[{"type": "ResearchOrder", "payload": %s, "civilizations": [%s]}]', result_, civilization_id_);
  asinc_tasks = format('[{"id": %s, "endTime": %s, "procedureName": "core.finish_research_order"}]', stellar_government_, time_ + 100000);

  return format('{"messageOrders": %s, "asincTaskCancelOrders": [%s], "asincTasks": %s}', message_orders, stellar_government_, asinc_tasks);
end;$function$;