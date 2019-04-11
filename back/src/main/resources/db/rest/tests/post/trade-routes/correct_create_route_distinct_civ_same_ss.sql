declare
    result_ json;
begin
    perform test.civ_test_bed();

    update core.colonies set id=civilization;
    insert into core.colonies(id,civilization,planet) values(4,2,(select id from core.planets where star_system=1 and orbit<>3 limit 1));
    insert into core.colony_resources(colony, resource_type, quantity) values(1, 'wood', 10);
    insert into core.visible_star_systems(star_system, civilization) values(2, 1);

    result_ = rest.execute_api('/trade-routes', 'post', 'token_1', '{"origin":1, "destination":4, "resourceType":"wood", "quantity":10}'::jsonb, 0);

    -- Test response format
    perform test.message_order_dto(result_ #> '{messageOrders, 0}');
    perform test.trade_route_dto(result_ #> '{messageOrders, 0, payload}');

    -- Test correct message order created
    assert (result_ #>> '{messageOrders, 0, civilizations, 0}')::numeric = 1, 'Message sent to other civilization';
    assert (result_ #>> '{messageOrders, 0, civilizations, 1}')::numeric = 2, 'Message sent to other civilization';
    assert (result_ #>> '{messageOrders, 0, civilizations, 2}') is null, 'Message sent to other civilization';
    assert (result_ #>> '{messageOrders, 0, type}') = 'TradeRoute', 'Message 0 is not TradeRoute';
    assert (result_ #>> '{messageOrders, 1}') is null, 'Sent too many messages';
    assert (result_ #>> '{asincTasks, 0}') is null, 'Created too many asinc tasks';

    -- Test correct trade route created
    assert (exists(select 1 from core.trade_routes where origin=1 and destination=4 and resource_type='wood' and quantity=10 and received_quantity=9)), 'Trade route not saved correctly';
    assert (select quantity from core.colony_resources where colony=1 and resource_type='wood')=0, 'Trade route should send all the wood';
    assert (select quantity from core.colony_resources where colony=4 and resource_type='wood')=9, 'Destination should receive 9 wood';
end;