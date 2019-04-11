declare
    expected_error_code_ integer := 400;
    expected_error_message_ text := 'Planet must be uninhabited';
    planet_ bigint;
    result_ json;
begin
    perform test.civ_test_bed();

    planet_ = planet from core.colonies where civilization=1;

    result_ = rest.execute_api('/colonies', 'post', 'token_1', format('{"planet":%s}', planet_)::jsonb, 0);

    perform rest.check_error(result_, expected_error_code_, expected_error_message_);

end;