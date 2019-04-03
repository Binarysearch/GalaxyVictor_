CREATE OR REPLACE FUNCTION core.get_star_system_technologies(star_system_ bigint, token_ text)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  civilization_id_ bigint;
begin

  civilization_id_ = core.require_civ(token_);

  result_ = (select array_to_json(array_agg(technology)) from core.stellar_governments_technologies where stellar_government=(select id from core.stellar_governments where star_system=star_system_ and civilization=civilization_id_));

  return coalesce(result_, '[]')::json;
end;$function$;