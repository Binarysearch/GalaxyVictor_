CREATE OR REPLACE FUNCTION core.start_travel(fleet_ bigint, destination_ bigint, time_ bigint, token_ text)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  civilization_id_ bigint;
  origin_ bigint;
begin

  civilization_id_ = core.require_civ(token_);

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

  --update fleet
  update core.fleets set destination=destination_, travel_start_time=time_ where id=fleet_;

  --delete visibility from origin star_system
  origin_ = (select origin from core.fleets where id=fleet_);
  delete from core.visible_star_systems where civilization=civilization_id_ and star_system=origin_;

  --insert travel
  insert into core.travels(fleet, origin, destination, start_time) select id, origin, destination, travel_start_time from core.fleets where id=fleet_;
  
  --insert new known civilizations
  --the civilizations with visibility in the target system go to meet the civilization owner of the fleet
  insert into core.known_civilizations(knows, known) select civilization, civilization_id_ from core.visible_star_systems where star_system=destination_  
  and not exists(select 1 from core.known_civilizations where knows=civilization and known=civilization_id_);


  --enviar por ws 'remove fleet' para civilizaciones en sistema origen
  --enviar por ws 'nueva civilizacion' para civilizaciones en sistema destino
  --enviar por ws 'update fleet' para civilizaciones en sistema destino
  --enviar visibility lost si procede

  result_ = (
    with ws_data as (select
      (select array_to_json(array_agg(civilization)) from core.visible_star_systems where star_system=destination_) as "destinationCivilizations",
      (select array_to_json(array_agg(civilization)) from core.visible_star_systems where star_system=origin_) as "originCivilizations",
      (select row_to_json(f) from (select id, civilization, destination, origin, travel_start_time as "travelStartTime" from core.fleets where id=fleet_) as f) as fleet,
      (select row_to_json(civ) from (select id, name from core.civilizations where id=civilization_id_) as civ) as civilization,
      (select row_to_json(tr) from (select t.fleet, civilization_id_ as civilization, ss0.x as x0, ss1.x as x1, ss0.y as y0, ss1.y as y1, 1 as speed, t.start_time as "startTime" from core.travels t join core.star_systems ss0 on ss0.id=t.origin join core.star_systems ss1 on ss1.id=t.destination where t.fleet=fleet_) as tr) as travel
    ) select row_to_json(ws_data) from ws_data
  );

  return coalesce(result_, '{}')::json;
end;$function$;