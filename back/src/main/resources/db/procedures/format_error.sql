CREATE OR REPLACE FUNCTION core.format_error(code_ integer, message_ text, description_ text, data_ json)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin
  return format('{"error": {"code": %s, "message": "%s", "description": "%s", "data": %s}}', code_, message_, description_, data_);
end;$function$;

CREATE OR REPLACE FUNCTION core.format_error(code_ integer, message_ text, data_ json)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin
  return format('{"error": {"code": %s, "message": "%s", "data": %s}}', code_, message_, data_);
end;$function$;

CREATE OR REPLACE FUNCTION core.format_error(code_ integer, message_ text)
 RETURNS json
 LANGUAGE plpgsql
 SECURITY DEFINER
AS $function$begin
  return format('{"error": {"code": %s, "message": "%s"}}', code_, message_);
end;$function$;

