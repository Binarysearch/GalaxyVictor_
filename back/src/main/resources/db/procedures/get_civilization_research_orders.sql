CREATE OR REPLACE FUNCTION core.get_civilization_research_orders(token_ text)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  civilization_id_ bigint;
begin

  civilization_id_ = core.require_civ(token_);

  result_ = (with cs as (

    select
      sg.star_system as "starSystem", ro.technology, ro.started_time as "startedTime", ro.finish_time as "finishTime"
    from
    core.stellar_governments sg join
    core.research_orders ro on ro.stellar_government=sg.id
    where sg.civilization=civilization_id_

  ) select array_to_json(array_agg(cs)) from cs);

  return coalesce(result_, '[]')::json;
end;$function$;