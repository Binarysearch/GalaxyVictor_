CREATE OR REPLACE FUNCTION core.finish_travel(fleet_ bigint)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  destination_ bigint;
  civilization_ bigint;
  ss_was_known_ boolean;
  ss_was_visible_ boolean;
  existing_fleet_ bigint;
begin

  destination_ = destination from core.fleets where id=fleet_;
  civilization_ = civilization from core.fleets where id=fleet_;
  delete from core.travels where fleet=fleet_;

  --if destination already has a fleet, merge the incoming fleet into the orbiting one
  existing_fleet_ = id from core.fleets where civilization=civilization_ and destination=destination_ and origin=destination_;
  update core.fleets set travel_start_time=0, origin=destination where id=fleet_;

  ss_was_known_ = (exists(select 1 from core.known_star_systems where star_system=destination_ and civilization=civilization_));
  ss_was_visible_ = (exists(select 1 from core.visible_star_systems where star_system=destination_ and civilization=civilization_));

  insert into core.visible_star_systems(star_system, civilization) values(destination_, civilization_);

  if (existing_fleet_ is not null) then
    update core.ships set fleet=existing_fleet_ where fleet=fleet_;
    delete from core.fleets where id=fleet_;
    delete from core.visible_star_systems where star_system=destination_ and civilization=civilization_;
  end if;
  
  if (not ss_was_known_) then
    insert into core.known_star_systems(star_system, civilization) values(destination_, civilization_);
  end if;

  if (not (select explored from core.star_systems where id=destination_)) then
    update core.star_systems set explored = true where id=destination_;
    perform core.generate_planets(destination_);
  end if;

  result_ = (
    with x as (
      select 
      (select row_to_json(f) from (select id, civilization, destination, origin, travel_start_time as "travelStartTime" from core.fleets where id=existing_fleet_) as f) as "resultingFleet",
      fleet_ as "incomingFleetId",
      (select array_to_json(array_agg(p)) from (select id, star_system as "starSystem", orbit, type, size from core.planets where star_system=destination_ and not ss_was_known_) as p) as planets,
      (select array_to_json(array_agg(flee)) from (
        select f.id, f.civilization, f.destination, f.origin, f.travel_start_time as "travelStartTime" from core.fleets f where civilization <> civilization_ and destination=destination_ and not ss_was_visible_
      ) as flee) as fleets,
      (select array_to_json(array_agg(cols)) from (
        select c.id, c.planet, c.civilization from core.colonies c join core.planets p on p.id=c.planet where c.civilization <> civilization_ and p.star_system=destination_ and not ss_was_visible_
      ) as cols) as colonies,
      (select array_to_json(array_agg(civs)) from (
        select c.id, c.name, c.homeworld from core.civilizations c where id <> civilization_ and id in(select co.civilization from core.colonies co join core.planets p on p.id=co.planet where p.star_system=destination_ and not ss_was_visible_ union select f.civilization from core.fleets f where destination=destination_ and not ss_was_visible_)
      ) as civs) as civilizations,
      (select array_to_json(array_agg(civilization)) from core.visible_star_systems where star_system=destination_) as "destinationCivilizations"
    ) select row_to_json(x) from x
  );

  return coalesce(result_, '{}')::json;
end;$function$;