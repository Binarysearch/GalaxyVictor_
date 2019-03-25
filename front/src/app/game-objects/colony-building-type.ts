import { ColonyBuildingTypeDTO } from './../dtos/colony-building-type';
import { ResourceType } from './resource-type';

export class ColonyBuildingType {
  id: string;
  name: string;
  resources: {resourceType: ResourceType, quantity: number}[] = [];

  constructor(data: ColonyBuildingTypeDTO) {
    this.id = data.id;
    this.name = data.name;
  }
}
