declare
    result_ json;
begin

    perform test.civ_test_bed();

    update core.colonies set id=civilization;

    insert into core.trade_routes(origin, destination, resource_type, quantity) values
    (3,2,'energy',5),
    (1,2,'energy',5),
    (2,1,'work',5);

    insert into core.trade_routes_creation(origin, destination, resource_type, quantity, started_time, finish_time) values
    (1,2,'energy',5,0,1),
    (2,1,'work',5,0,1);

    insert into core.trade_routes_destruction(origin, destination, resource_type, received_quantity, finish_time) values
    (1,2,'energy',5,1),
    (2,1,'work',5,1);

    result_ = rest.execute_api('/trade-routes', 'get', 'token_1', '{}'::jsonb, 0);

    -- Test response format
    perform test.trade_route_dto(result_ #> '{tradeRoutes, 0}');
    perform test.trade_route_dto(result_ #> '{tradeRoutes, 1}');
    perform test.creation_trade_route_dto(result_ #> '{creationTradeRoutes, 0}');
    perform test.destruction_trade_route_dto(result_ #> '{destructionTradeRoutes, 0}');
    perform test.destruction_trade_route_dto(result_ #> '{destructionTradeRoutes, 1}');

    --Test creation trade route not seen by destination civ
    assert (result_ #>> '{creationTradeRoutes, 1}') is null, 'Creation trade route is seen by destination civ';
    assert (result_ #>> '{tradeRoutes, 2}') is null, 'Too many trade routes seen by civilization 1';
    

    --Test get by civilization 2
    result_ = rest.execute_api('/trade-routes', 'get', 'token_2', '{}'::jsonb, 0);
    
    perform test.trade_route_dto(result_ #> '{tradeRoutes, 0}');
    perform test.trade_route_dto(result_ #> '{tradeRoutes, 1}');
    perform test.trade_route_dto(result_ #> '{tradeRoutes, 2}');
    perform test.creation_trade_route_dto(result_ #> '{creationTradeRoutes, 0}');
    perform test.destruction_trade_route_dto(result_ #> '{destructionTradeRoutes, 0}');
    perform test.destruction_trade_route_dto(result_ #> '{destructionTradeRoutes, 1}');

    assert (result_ #>> '{creationTradeRoutes, 1}') is null, 'Creation trade route is seen by destination civ';
    assert (result_ #>> '{tradeRoutes, 3}') is null, 'Too many trade routes seen by civilization 2';

    --Test get by civilization 3
    result_ = rest.execute_api('/trade-routes', 'get', 'token_3', '{}'::jsonb, 0);
    
    perform test.trade_route_dto(result_ #> '{tradeRoutes, 0}');

    assert (result_ #>> '{tradeRoutes, 1}') is null, 'Too many trade routes seen by civilization 3';
    assert (result_ #>> '{creationTradeRoutes, 1}') is null, 'Too many creation trade routes seen by civilization 3';
    assert (result_ #>> '{destructionTradeRoutes, 1}') is null, 'Too many destruction trade routes seen by civilization 3';
end;