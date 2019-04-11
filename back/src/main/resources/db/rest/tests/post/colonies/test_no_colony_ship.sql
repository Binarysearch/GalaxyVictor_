declare
    expected_error_code_ integer := 400;
    expected_error_message_ text := 'There must be a colonizing ship of civilization orbiting the system';
    planet_ bigint;
    result_ json;
begin
    perform test.civ_test_bed();

    planet_ = id from core.planets where star_system <> 1 and orbit <> 3 limit 1;

    result_ = rest.execute_api('/colonies', 'post', 'token_1', format('{"planet":%s}', planet_)::jsonb, 0);

    perform rest.check_error(result_, expected_error_code_, expected_error_message_);
    
end;