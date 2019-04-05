

-- test without galaxy
select test.register();
select test.set_current_galaxy();

-- test with galaxy
select test.login();
select test.auth();
select test.get_galaxies();
select test.get_star_systems();
select test.create_civilization();
select test.get_current_civilization();
select test.get_civilization_research_orders();
select test.set_research_order();
select test.colonize_planet();



--clear
select test.clear();