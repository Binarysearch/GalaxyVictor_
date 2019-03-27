CREATE OR REPLACE FUNCTION core.get_ship_models(token_ text)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  civilization_id_ bigint;
begin

  civilization_id_ = core.require_civ(token_);

  result_ = (with x as (
    
    select id, name, can_colonize as "canColonize", can_fight as "canFight" from core.civilization_ship_models where civilization=civilization_id_
    union
    select id, name, can_colonize as "canColonize", can_fight as "canFight" from core.ship_models

  ) select array_to_json(array_agg(x)) from x);

  return coalesce(result_, '[]')::json;
end;$function$;