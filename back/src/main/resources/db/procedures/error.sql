CREATE OR REPLACE FUNCTION core.error(code_ integer, message_ text, data_ json)
 RETURNS boolean
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin
  RAISE exception using errcode=format('GV%s', code_), message=format('{"data":%s, "message":"%s"}', data_::text, message_);
end;$function$;

CREATE OR REPLACE FUNCTION core.error(code_ integer, message_ text)
 RETURNS boolean
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin
  RAISE exception using errcode=format('GV%s', code_), message=format('{"data":{}, "message":"%s"}', message_);
end;$function$;