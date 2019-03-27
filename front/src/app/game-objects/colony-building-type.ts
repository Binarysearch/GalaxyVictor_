import { ColonyBuildingTypeDTO } from './../dtos/colony-building-type';
import { ResourceType } from './resource-type';
import { ColonyBuildingCapabilityType } from './colony-building-capability-type';

export class ColonyBuildingType {
  id: string;
  name: string;
  buildable: boolean;
  resources: {resourceType: ResourceType, quantity: number}[] = [];
  costs: {resourceType: ResourceType, quantity: number}[] = [];
  capabilities: {type: ColonyBuildingCapabilityType}[] = [];

  constructor(data: ColonyBuildingTypeDTO) {
    this.id = data.id;
    this.name = data.name;
    this.buildable = data.buildable;
  }
}
