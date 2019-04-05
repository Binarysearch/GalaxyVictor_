

-- test without galaxy
select test.register();
select test.set_current_galaxy();

-- test with galaxy
select test.login();
select test.auth();



--clear
select test.clear();