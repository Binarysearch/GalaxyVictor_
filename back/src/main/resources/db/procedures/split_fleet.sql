CREATE OR REPLACE FUNCTION core.split_fleet(fleet_ bigint, ships_ bigint[], destination_ bigint, time_ bigint, token_ text)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  start_travel_result_ json;
  staying_fleet_ json;
  civilization_id_ bigint;
  new_fleet_ bigint;
begin

  civilization_id_ = core.require_civ(token_);

  if (ships_ = '{}') then
    perform core.error(400, 'No ships selected');
  end if;

  --check if fleet exists
  if (not exists(select 1 from core.fleets where id=fleet_)) then
    perform core.error(404, 'Fleet not found');
  end if;

  --check if civilization owns fleet
  if (not exists(select 1 from core.fleets where id=fleet_ and civilization=civilization_id_)) then
    perform core.error(401, 'Not your fleet');
  end if;

  --check that is not travelling
  if (exists(select 1 from core.travels where fleet=fleet_)) then
    perform core.error(400, 'Fleet is already travelling');
  end if;
  
  --check if destination exists
  if (not exists(select 1 from core.star_systems where id=destination_)) then
    perform core.error(404, 'Destination not found');
  end if;

  --check that destination and origin(previous dest) are not the same
  if (exists(select 1 from core.fleets where id=fleet_ and destination=destination_)) then
    perform core.error(400, 'Destination and origin are the same');
  end if;

  --check that all ships are from the fleet
  if (exists(select 1 from core.ships where fleet <> fleet_ and id = ANY (ships_))) then
    perform core.error(400, 'Those ships are not from the same fleet');
  end if;

  insert into core.fleets(civilization, destination, origin) select civilization, destination, origin from core.fleets where id=fleet_ returning id into new_fleet_;

  update core.ships set fleet=new_fleet_ where id = ANY (ships_);

  start_travel_result_ = core.start_travel(new_fleet_, destination_, time_, token_);

  staying_fleet_ = (select row_to_json(f) from (select id, civilization, destination, origin, travel_start_time as "travelStartTime", colony_ships>0 as "canColonize" from core.fleets where id=fleet_) as f);

  return format('{"stayingFleet": %s, "startTravelDbResponse": %s}', staying_fleet_, start_travel_result_)::json;
end;$function$;