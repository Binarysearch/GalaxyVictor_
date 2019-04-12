import { GameObject } from './game-object';
import { CreationTradeRouteDTO } from '../dtos/creation-trade-route';
import { ResourceType } from './resource-type';
import { Planet } from './planet';

export class CreationTradeRoute implements GameObject {

  x: number;
  y: number;
  objectType = 'CreationTradeRoute';

  id: number;
  resourceType: ResourceType;
  quantity: number;
  startedTime: number;
  finishTime: number;
  origin: Planet;
  destination: Planet;

  constructor(data: CreationTradeRouteDTO) {
    this.id = data.id;
    this.quantity = data.quantity;
    this.startedTime = data.startedTime;
    this.finishTime = data.finishTime;
  }
}
