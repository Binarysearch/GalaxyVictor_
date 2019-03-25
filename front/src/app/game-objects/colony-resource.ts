import { ColonyResourceDTO } from '../dtos/colony-resource';
import { ResourceType } from './resource-type';

export class ColonyResource {
  id: number;
  type: ResourceType;
  quantity: number;

  constructor(data: ColonyResourceDTO) {
    this.id = data.id;
    this.quantity = data.quantity;
  }
}
