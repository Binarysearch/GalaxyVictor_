declare
    expected_error_code_ integer := 400;
    expected_error_message_ text := 'The resource must be merchantable.';
    planet_ bigint;
    result_ json;
begin
    perform test.civ_test_bed();
    
    update core.colonies set id=civilization;
    insert into core.visible_star_systems(star_system, civilization) values(2, 1);

    planet_ = planet from core.colonies where civilization=1;

    result_ = rest.execute_api('/trade-routes', 'post', 'token_1', '{"origin":1, "destination":2, "resourceType":"energy", "quantity":10}'::jsonb, 0);

    perform rest.check_error(result_, expected_error_code_, expected_error_message_);

end;