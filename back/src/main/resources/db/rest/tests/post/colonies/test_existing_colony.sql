declare
    expected_error_code_ text := 'GV400';
    expected_error_message_ text := 'Planet must be uninhabited';
    planet_ bigint;
begin
    perform test.civ_test_bed();

    planet_ = planet from core.colonies where civilization=1;

    perform rest.execute_api('/colonies', 'post', 'token_1', format('{"planet":%s}', planet_)::jsonb);

    RAISE EXCEPTION using errcode='NOERR';

    exception when others then
        if (sqlstate=expected_error_code_ and SQLERRM::jsonb->>'message' = expected_error_message_) then
            return;
        end if;
        if (sqlstate = 'NOERR') then
            perform core.error(500, format('Test not passed. Expecting errorcode: %L, errormessage: %L. No error was thrown', expected_error_code_, expected_error_message_));
        end if;
        perform core.error(500, format('Test not passed. Expecting errorcode: %L, errormessage: %L. Actual errorcode: %L, errormessage: %L.', expected_error_code_, expected_error_message_, sqlstate, SQLERRM));

    
end;