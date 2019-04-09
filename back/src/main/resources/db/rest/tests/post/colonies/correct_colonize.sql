declare
    planet_ bigint;
    result_ json;
begin
    perform test.civ_test_bed();

    planet_ = id from core.planets where star_system = 1 and orbit <> 3 limit 1;

    result_ = rest.execute_api('/colonies', 'post', 'token_1', format('{"planet":%s}', planet_)::jsonb);

    -- Test response format
    perform test.message_order_dto(result_ #> '{messageOrders, 0}');
    perform test.message_order_dto(result_ #> '{messageOrders, 1}');
    perform test.colony_dto(result_ #> '{messageOrders, 0, payload}');
    perform test.fleet_dto(result_ #> '{messageOrders, 1, payload}');

    -- Test correct message order created
    assert (result_ #>> '{messageOrders, 0, civilizations, 0}')::numeric = 1, 'Message sent to other civilization';
    assert (result_ #>> '{messageOrders, 0, civilizations, 1}') is null, 'Message sent to other civilization';
    assert (result_ #>> '{messageOrders, 1, civilizations, 0}')::numeric = 1, 'Message sent to other civilization';
    assert (result_ #>> '{messageOrders, 1, civilizations, 1}') is null, 'Message sent to other civilization';
    assert (result_ #>> '{messageOrders, 0, type}') = 'Colony', 'Message 0 is not Colony';
    assert (result_ #>> '{messageOrders, 1, type}') = 'Fleet', 'Message 1 is not Fleet';
    assert (result_ #>> '{messageOrders, 2}') is null, 'Sent too many messages';

    -- Test correct colony created
    assert (exists(select 1 from core.colonies where planet=planet_ and civilization=1)), 'Colony not saved correctly';
    assert (exists(select 1 from core.colony_buildings where building_type='colony base' and colony=(select id from core.colonies where planet=planet_ and civilization=1))), 'Colony base not saved correctly';

end;