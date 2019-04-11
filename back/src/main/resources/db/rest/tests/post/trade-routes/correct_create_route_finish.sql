declare
    result_ json;
begin
    perform test.civ_test_bed();

    update core.colonies set id=civilization;
    insert into core.colony_resources(colony, resource_type, quantity) values(1, 'wood', 10);
    insert into core.visible_star_systems(star_system, civilization) values(2, 1);

    result_ = rest.execute_api('/trade-routes', 'post', 'token_1', '{"origin":1, "destination":2, "resourceType":"wood", "quantity":10}'::jsonb, 0);

    -- Test response format
    perform test.message_order_dto(result_ #> '{messageOrders, 0}');
    perform test.creation_trade_route_dto(result_ #> '{messageOrders, 0, payload}');
    perform test.asinc_task_order_dto(result_ #> '{asincTasks, 0}');

    -- Test correct message order created
    assert (result_ #>> '{messageOrders, 0, civilizations, 0}')::numeric = 1, 'Message sent to other civilization';
    assert (result_ #>> '{messageOrders, 0, civilizations, 1}') is null, 'Message sent to other civilization';
    assert (result_ #>> '{messageOrders, 0, type}') = 'TradeRouteCreation', 'Message 0 is not TradeRouteCreation';
    assert (result_ #>> '{messageOrders, 1}') is null, 'Sent too many messages';

    --Test correct asinc task
    assert (result_ #>> '{asincTasks, 0, procedureName}')='core.finish_trade_route_creation', 'Incorrect callback procedure name';
    assert (result_ #>> '{asincTasks, 1}') is null, 'Created too many asinc tasks';

    -- Test correct trade route created
    assert (exists(select 1 from core.trade_routes_creation where origin=1 and destination=2 and resource_type='wood' and quantity=10)), 'Trade route creation not saved correctly';
    assert (select quantity from core.colony_resources where colony=1 and resource_type='wood')=0, 'Trade route should send all the wood';

    -- Finish trade route
    result_ = core.finish_trade_route_creation((result_ #>> '{asincTasks, 0, id}')::numeric::bigint);

    -- Test correct finish response format
    perform test.message_order_dto(result_ #> '{messageOrders, 0}');
    perform test.finish_creation_trade_route_dto(result_ #> '{messageOrders, 0, payload}');

    -- Test correct finish message orders
    assert (result_ #>> '{messageOrders, 0, civilizations, 0}')::numeric = 1, 'Message sent to other civilization';
    assert (result_ #>> '{messageOrders, 0, civilizations, 1}')::numeric = 2, 'Message sent to other civilization';
    assert (result_ #>> '{messageOrders, 0, civilizations, 2}') is null, 'Message sent to other civilization';
    assert (result_ #>> '{messageOrders, 0, type}') = 'FinishTradeRouteCreation', 'Message 0 is not FinishTradeRouteCreation';
    assert (result_ #>> '{messageOrders, 1}') is null, 'Sent too many messages';

    -- Test correct trade route created
    assert (not exists(select 1 from core.trade_routes_creation where origin=1 and destination=2 and resource_type='wood' and quantity=10)), 'Trade route creation not deleted correctly';
    assert (exists(select 1 from core.trade_routes where origin=1 and destination=2 and resource_type='wood' and quantity=10 and received_quantity=8)), 'Trade route not saved correctly';
    assert (select quantity from core.colony_resources where colony=1 and resource_type='wood')=0, 'Trade route should send all the wood';
    assert (select quantity from core.colony_resources where colony=2 and resource_type='wood')=8, 'Destination should receive 8 wood';
end;