CREATE OR REPLACE FUNCTION core.get_colony_buildings(colony_ bigint, token_ text)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  civilization_id_ bigint;
begin

  civilization_id_ = core.require_civ(token_);

  -- if civilization does not own the colony throw error
  if (not exists(select 1 from core.colonies where id=colony_ and civilization=civilization_id_)) then
    perform core.error(401, format('Colony %L not yours', colony_));
  end if;

  result_ = (with x as (
    
    select id, building_type from core.colony_buildings where colony=colony_

  ) select array_to_json(array_agg(x)) from x);

  return coalesce(result_, '[]')::json;
end;$function$;