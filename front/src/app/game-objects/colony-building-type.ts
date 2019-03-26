import { ColonyBuildingTypeDTO } from './../dtos/colony-building-type';
import { ResourceType } from './resource-type';

export class ColonyBuildingType {
  id: string;
  name: string;
  buildable: boolean;
  resources: {resourceType: ResourceType, quantity: number}[] = [];

  constructor(data: ColonyBuildingTypeDTO) {
    this.id = data.id;
    this.name = data.name;
    this.buildable = data.buildable;
  }
}
