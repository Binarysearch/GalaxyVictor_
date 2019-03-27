CREATE OR REPLACE FUNCTION core.finish_colony_ship_order(colony_ bigint)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  fleet_ bigint;
  star_system_ bigint;
  civilization_ bigint;
  ship_model_id_ bigint;
begin

  ship_model_id_ = ship_model from core.colony_building_orders where colony=colony_;

  star_system_ = star_system from core.planets where id=(select planet from core.colonies where id=colony_);
  civilization_ = civilization from core.colonies where id=colony_;

  fleet_ = id from core.fleets where origin=star_system_ and destination=star_system_ and civilization=civilization_;
  
  if (fleet_ is null) then
    insert into core.fleets(civilization, destination, origin, travel_start_time) values(civilization_, star_system_, star_system_, 0) returning id into fleet_;
  end if;

  insert into core.ships(fleet, model_name, can_colonize, can_fight) select fleet_, name, can_colonize, can_fight from core.civilization_ship_models where id=ship_model_id_;

  delete from core.colony_building_orders where colony=colony_;

  result_ = (
    with x as (select colony_ as colony,

      (select row_to_json(f) from (select id, civilization, destination, origin, travel_start_time as "travelStartTime" from core.fleets where id=fleet_) as f) as fleet,
      (select array_to_json(array_agg(civilization)) from core.visible_star_systems where star_system=star_system_) as civilizations

    ) select row_to_json(x) from x
  );

  return result_;
end;$function$;