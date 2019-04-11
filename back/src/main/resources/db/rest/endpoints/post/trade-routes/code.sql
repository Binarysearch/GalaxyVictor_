declare
  origin_ bigint := (params_->>'origin')::numeric::bigint;
  destination_ bigint := (params_->>'destination')::numeric::bigint;
  resource_type_ text := params_->>'resourceType';
  quantity_ bigint := (params_->>'quantity')::numeric::bigint;

  civilization_id_ bigint; 
  origin_star_system_ bigint; 
  destination_star_system_ bigint; 
  trade_route_id_ bigint;
  finish_time_ bigint;
  result_ json;
  message_orders json;
  asinc_tasks json;
begin

  civilization_id_ = core.require_civ(token_);

  -- Amount calculated at destination must be greater than zero.
  if (quantity_ < 2) then
    return core.format_error(400, 'Amount calculated at destination must be greater than zero.');
  end if;

  -- The resource must be merchantable.
  if (not exists(select 1 from core.resource_types where id=resource_type_ and merchantable)) then
    return core.format_error(400, 'The resource must be merchantable.');
  end if;

  -- Origin and destination can not be the same.
  if (origin_ = destination_) then
    return core.format_error(400, 'Origin and destination can not be the same');
  end if;

  -- Origin must be a colony of ordering civilization.
  if (not exists(select 1 from core.colonies where id=origin_ and civilization=civilization_id_)) then
    return core.format_error(400, 'Origin must be a colony of ordering civilization');
  end if;

  -- Destination must be a visible colony by ordering civilization.
  if (not exists(select 1 from core.colonies where id=destination_ and planet in(select id from core.planets where star_system in (select v.star_system from core.visible_star_systems v where civilization=civilization_id_)))) then
    return core.format_error(400, 'Destination must be a visible colony by ordering civilization.');
  end if;

  -- Origin must have sufficient resources.
  if (not exists(select 1 from core.colony_resources where resource_type=resource_type_ and colony=origin_ and quantity>=quantity_)) then
    return core.format_error(400, 'Origin must have sufficient resources.');
  end if;

  origin_star_system_ = star_system from core.planets where id=(select planet from core.colonies where id=origin_);
  destination_star_system_ = star_system from core.planets where id=(select planet from core.colonies where id=destination_);
  
  finish_time_ = time_ + 100000;

  if (origin_star_system_ = destination_star_system_) then
    --same star system, create route
    
    insert into core.trade_routes(origin, destination, resource_type, quantity) values(origin_, destination_, resource_type_, quantity_) returning id into trade_route_id_;
    result_ = (select row_to_json(r) from (select tr.id, tr.resource_type as "resourceType", tr.quantity, tr.received_quantity as "receivedQuantity", tr.origin_planet as origin, tr.destination_planet as destination from core.trade_routes tr where id=trade_route_id_) as r);

    if (exists(select 1 from core.colonies where id=destination_ and civilization=civilization_id_)) then
      message_orders = format('[
        {"type": "TradeRoute", "payload": %s, "civilizations": [%s]}
      ]', result_, civilization_id_);
    else
      -- Destination civilization is other then origin one, send message to destination civilization
      message_orders = format('[
        {"type": "TradeRoute", "payload": %s, "civilizations": [%s,%s]}
      ]', result_, civilization_id_, (select civilization from core.colonies where id=destination_));
    end if;

    return format('{"messageOrders": %s}', message_orders);

  else
    -- distinct star system, create asinc task and route_creation

    insert into core.trade_routes_creation(origin, destination, resource_type, quantity, started_time, finish_time) values(origin_, destination_, resource_type_, quantity_, time_, finish_time_) returning id into trade_route_id_;
    result_ = (select row_to_json(r) from (select tr.id, tr.resource_type as "resourceType", tr.quantity, tr.started_time as "startedTime", tr.finish_time as "finishTime", tr.origin_planet as origin, tr.destination_planet as destination from core.trade_routes_creation tr where id=trade_route_id_) as r);

    message_orders = format('[
      {"type": "TradeRouteCreation", "payload": %s, "civilizations": [%s]}
    ]', result_, civilization_id_);

    asinc_tasks = format('[{"id": %s, "endTime": %s, "procedureName": "core.finish_trade_route_creation"}]', trade_route_id_, finish_time_);

    return format('{"messageOrders": %s, "asincTasks": %s}', message_orders, asinc_tasks);
  end if;

end;