CREATE OR REPLACE FUNCTION test.finish_colony_ship_order()
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  colony_ bigint;
  ship_model_ bigint;
begin

  perform test.civ_test_bed();

  ship_model_ = id from core.civilization_ship_models where civilization=1 limit 1;
  colony_ = id from core.colonies where civilization=1 limit 1;
  insert into core.colony_building_orders(colony, ship_model, started_time) values(colony_, ship_model_, 123456);
  delete from core.ships;
  delete from core.fleets;

  result_ = core.finish_colony_ship_order(colony_);

  -- Test response format
  perform test.message_order_dto(result_ #> '{messageOrders, 0}');
  perform test.message_order_dto(result_ #> '{messageOrders, 1}');
  perform test.finish_building_ship_dto(result_ #> '{messageOrders, 0, payload}');
  perform test.fleet_dto(result_ #> '{messageOrders, 1, payload}');

  -- Test correct message order created
  assert (result_ #>> '{messageOrders, 0, civilizations, 0}')::numeric = 1, 'Message sent to other civilization';
  assert (result_ #>> '{messageOrders, 0, civilizations, 1}') is null, 'Message sent to other civilization';
  assert (result_ #>> '{messageOrders, 0, type}') = 'FinishBuildingShip', 'Message 0 is not FinishBuildingShip';

  assert (result_ #>> '{messageOrders, 1, civilizations, 0}')::numeric = 1, 'Message sent to other civilization';
  assert (result_ #>> '{messageOrders, 1, civilizations, 1}') is null, 'Message sent to other civilization';
  assert (result_ #>> '{messageOrders, 1, type}') = 'Fleet', 'Message 1 is not Fleet';
  assert (result_ #>> '{messageOrders, 2}') is null, 'Sent too many messages';

  -- Test correct ship created
  assert (exists(select 1 from core.ships where model_name=(select name from core.civilization_ship_models where id=ship_model_) and fleet=(select id from core.fleets where civilization=1 and destination=1 and origin=1))), 'Ship not saved correctly';
  assert (exists(select 1 from core.fleets where civilization=1 and destination=1 and origin=1)), 'Fleet not saved correctly';

  assert (not exists (select 1 from core.colony_building_orders where colony=colony_)), 'Colony building order not deleted correctly';
end;$function$;