CREATE OR REPLACE FUNCTION core.create_civilization(name_ text, home_star_name_ text, token_ text)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  user_id_ bigint;
  galaxy_id_ bigint;
  star_system_ bigint;
  homeworld_id_ bigint;
  civilization_id_ bigint;
  fleet_id_ bigint;
  colony_id_ bigint;
begin

  user_id_ = usr from core.sessions where id=token_;
  
  if (user_id_ is null) then
    perform core.error(401, 'Invalid token');
  end if;

  galaxy_id_ = galaxy from core.users where id=user_id_;

  if (galaxy_id_ is null) then
    perform core.error(400, 'User does not have selected galaxy');
  end if;

  star_system_ = core.find_random_unexplored_system(galaxy_id_);
  perform core.generate_planets(star_system_);

  update core.star_systems set name = home_star_name_, explored=true where id=star_system_;

  insert into core.planets(star_system, orbit, type, size) select star_system_, 3, 10, 3 
  where not exists(select 1 from core.planets where star_system=star_system_ and orbit=3);
  homeworld_id_ = id from core.planets where star_system = star_system_ and orbit=3;

  update core.planets set type=10, size=3 where star_system=star_system_ and orbit=3;

  insert into core.civilizations(galaxy, name, homeworld, usr) values(galaxy_id_, name_, homeworld_id_, user_id_) returning id into civilization_id_;

  insert into core.known_star_systems(civilization, star_system) values(civilization_id_, star_system_);

  insert into core.known_civilizations(knows, known) values(civilization_id_, civilization_id_);

  insert into core.colonies(civilization, planet) values(civilization_id_, homeworld_id_) returning id into colony_id_;

  insert into core.colony_buildings(colony, building_type) values(colony_id_, 'imperial capital');

  insert into core.fleets(civilization, destination, origin, travel_start_time) values(civilization_id_, star_system_, star_system_, 0) returning id into fleet_id_;

  insert into core.civilization_ship_models(civilization, name, can_colonize, can_fight) values (civilization_id_, 'Colonizador', true, false),(civilization_id_, 'Explorador', false, false);
  
  insert into core.ships(fleet, model_name, can_colonize, can_fight) values (fleet_id_, 'Colonizador', true, false),(fleet_id_, 'Explorador', false, false);

  return core.get_current_civilization(token_);
end;$function$;