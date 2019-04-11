declare
    civ_id_ bigint;
begin

    civ_id_ = core.require_civ(token_);

    return coalesce((
        with x as (
            select 
            (select array_to_json(array_agg(r)) from (select tr.id, tr.resource_type as "resourceType", tr.quantity, tr.received_quantity as "receivedQuantity", tr.origin_planet as origin, tr.destination_planet as destination from core.trade_routes tr where civ_id_ in (select civilization from core.colonies c where c.id=tr.origin or c.id=tr.destination)) as r) as "tradeRoutes",
            (select array_to_json(array_agg(r)) from (select tr.id, tr.resource_type as "resourceType", tr.quantity, tr.started_time as "startedTime", tr.finish_time as "finishTime", tr.origin_planet as origin, tr.destination_planet as destination from core.trade_routes_creation tr where civ_id_ in (select civilization from core.colonies c where c.id=tr.origin)) as r) as "creationTradeRoutes",
            (select array_to_json(array_agg(r)) from (select tr.id, tr.resource_type as "resourceType", tr.received_quantity as "receivedQuantity", tr.finish_time as "finishTime", tr.origin_planet as origin, tr.destination_planet as destination from core.trade_routes_destruction tr where civ_id_ in (select civilization from core.colonies c where c.id=tr.origin or c.id=tr.destination)) as r) as "destructionTradeRoutes"

        ) select row_to_json(x) from x
    ), '{}');

end;