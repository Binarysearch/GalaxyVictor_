declare
  planet_ bigint := (params_->>'planet')::numeric::bigint;

  result_ json;
  civilization_id_ bigint;
  fleet_ bigint;
  ship_ bigint;
  star_system_ bigint;
  colony_id_ bigint;
  message_orders json;
  delete_fleet_ boolean;
begin

  civilization_id_ = core.require_civ(token_);

  -- planet must be uninhabited
  if (exists(select 1 from core.colonies where planet=planet_)) then
    return core.format_error(400, 'Planet must be uninhabited');
  end if;

  star_system_ = star_system from core.planets where id=planet_;

  -- there must be a colonizing ship of civilization orbiting the system
  ship_ = id from core.ships where can_colonize and fleet=(select id from core.fleets where civilization=civilization_id_ and origin=star_system_ and destination=star_system_) limit 1;
  if (ship_ is null) then
    return core.format_error(400, 'There must be a colonizing ship of civilization orbiting the system');
  end if;

  fleet_ = fleet from core.ships where id=ship_;

  delete from core.ships where id=ship_;

  -- If it is the last ship in the fleet eliminate the fleet
  delete_fleet_ = false;
  if (not exists(select 1 from core.ships where fleet=fleet_)) then
    delete from core.fleets where id = fleet_;
    delete_fleet_ = true;
  end if;

  insert into core.colonies(civilization, planet) values(civilization_id_, planet_) returning id into colony_id_;
  insert into core.colony_buildings(colony, building_type) values(colony_id_, 'colony base');

  result_ = (
    with x as (select

      (select row_to_json(c) from (select id, planet, civilization from core.colonies where id=colony_id_) as c) as colony,
      (select row_to_json(f) from (select id, civilization, destination, origin, travel_start_time as "travelStartTime", colony_ships>0 as "canColonize" from core.fleets where id=fleet_) as f) as fleet,
      (select array_to_json(array_agg(civilization)) from core.visible_star_systems where star_system=star_system_) as civilizations

    ) select row_to_json(x) from x
  );

  if (delete_fleet_) then
    message_orders = format('[
      {"type": "Colony", "payload": %s, "civilizations": %s},
      {"type": "RemoveFleet", "payload": {"id": %s}, "civilizations": %s}
    ]', result_->'colony', result_->'civilizations', fleet_, result_->'civilizations');
  else
    message_orders = format('[
      {"type": "Colony", "payload": %s, "civilizations": %s},
      {"type": "Fleet", "payload": %s, "civilizations": %s}
    ]', result_->'colony', result_->'civilizations', result_->'fleet', result_->'civilizations');
  end if;


  return format('{"messageOrders": %s}', message_orders);

end;