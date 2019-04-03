CREATE OR REPLACE FUNCTION core.finish_research_order(stellar_government_ bigint)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  message_orders json;
  star_system_ bigint;
  civilization_ bigint;
  technology_ text;
begin

  technology_ = technology from core.research_orders where stellar_government=stellar_government_;

  star_system_ = star_system from core.stellar_governments where id=stellar_government_;
  civilization_ = civilization from core.stellar_governments where id=stellar_government_;

  
  if (technology_ is null) then
    return '{}'::json;
  end if;

  insert into core.stellar_governments_technologies(stellar_government, technology)
  select stellar_government_, technology_
  where not exists(select 1 from core.stellar_governments_technologies where stellar_government=stellar_government_ and technology=technology_);

  delete from core.research_orders where stellar_government=stellar_government_;

  message_orders = format('[
    {"type": "FinishResearchOrder", "payload": {"starSystem": %s, "technology": %s}, "civilizations": [%s]}
  ]', star_system_, technology_, civilization_);

  return format('{"messageOrders": %s}', message_orders);

end;$function$;