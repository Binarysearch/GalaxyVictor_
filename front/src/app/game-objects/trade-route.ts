import { GameObject } from './game-object';
import { Planet } from './planet';
import { TradeRouteDTO } from '../dtos/trade-route';
import { ResourceType } from './resource-type';

export class TradeRoute implements GameObject {

  x: number;
  y: number;
  objectType = 'CreationTradeRoute';

  id: number;
  resourceType: ResourceType;
  quantity: number;
  receivedQuantity: number;
  origin: Planet;
  destination: Planet;

  constructor(data: TradeRouteDTO) {
    this.id = data.id;
    this.quantity = data.quantity;
    this.receivedQuantity = data.receivedQuantity;
  }
}
