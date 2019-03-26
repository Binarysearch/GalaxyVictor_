import { ColonyResourceDTO } from '../dtos/colony-resource';
import { ResourceType } from './resource-type';

export class ColonyResource {
  type: ResourceType;
  quantity: number;

  constructor(data: ColonyResourceDTO) {
    this.quantity = data.quantity;
  }
}
