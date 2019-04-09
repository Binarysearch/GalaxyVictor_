DROP SCHEMA IF EXISTS rest CASCADE;

CREATE SCHEMA rest;

SET search_path TO rest;

CREATE TABLE endpoints(
    id SERIAL PRIMARY KEY,
    path text NOT NULL,
    method text NOT NULL,
    code text NOT NULL,
    schema jsonb NOT NULL,
    UNIQUE(path, method)
);

CREATE TABLE tests(
    id SERIAL PRIMARY KEY,
    name text NOT NULL,
    path text NOT NULL,
    method text NOT NULL,
    code text NOT NULL,
    passed boolean NOT NULL DEFAULT false,
    error_message text,
    error_code text,
    FOREIGN KEY (path, method) REFERENCES rest.endpoints (path, method)
);


CREATE OR REPLACE FUNCTION rest.execute_api(path_ text, method_ text, token_ text, params_ jsonb)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  result_ json;
  query_ text;
begin

  if (not exists(select 1 from rest.endpoints where path=path_ and method=method_)) then
    perform core.error(401, format('Endpoint: %L, method: %L not found', path_, method_));
  end if;


  query_ = (

    select 
      format('CREATE OR REPLACE FUNCTION pg_temp."api_%s"(path_ text, method_ text, token_ text, params_ jsonb)
      RETURNS json
      LANGUAGE plpgsql
      VOLATILE SECURITY DEFINER
      AS $bxbxb$%s$bxbxb$;select pg_temp."api_%s"(%L,%L,%L,%L::jsonb)', id, code, id, path_, method_, token_, params_) 
    from rest.endpoints 
    where path=path_ and method=method_ and rest.validate_json_schema(schema, params_)

  );

  if (query_ is null) then
    perform core.error(400, format('Endpoint: %L, method: %L does not validate request schema.', path_, method_));
  end if;

  execute query_ into result_;

  return result_;
end;$function$;


CREATE OR REPLACE FUNCTION rest.execute_tests()
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  temprow_ record;
begin

  for temprow_ in
    select id from rest.tests
  LOOP
    perform rest.execute_test(temprow_.id);
  END LOOP;

end;$function$;


CREATE OR REPLACE FUNCTION rest.execute_test(id_ bigint)
 RETURNS void
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$declare
  temprow_ record;
begin

  execute(
    select
      format('CREATE OR REPLACE FUNCTION pg_temp."test_api_%s"()
      RETURNS void
      LANGUAGE plpgsql
      VOLATILE SECURITY DEFINER
      AS $bxbxb$%s$bxbxb$;select pg_temp."test_api_%s"();', id, code, id) 
    from 
      rest.tests
    where 
      id=id_
  );

  perform test.clear();
  update rest.tests set passed=true where id=id_;

  EXCEPTION WHEN others then
    update rest.tests set passed=false, error_message=SQLERRM, error_code=SQLSTATE where id=id_;

end;$function$;