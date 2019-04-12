import { GameObject } from './game-object';
import { Planet } from './planet';
import { DestructionTradeRouteDTO } from '../dtos/destruction-trade-route';
import { ResourceType } from './resource-type';

export class DestructionTradeRoute implements GameObject {

  x: number;
  y: number;
  objectType = 'CreationTradeRoute';

  id: number;
  resourceType: ResourceType;
  receivedQuantity: number;
  finishTime: number;
  origin: Planet;
  destination: Planet;

  constructor(data: DestructionTradeRouteDTO) {
    this.id = data.id;
    this.receivedQuantity = data.receivedQuantity;
    this.finishTime = data.finishTime;
  }
}
