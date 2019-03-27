import { ColonyBuildingCapabilityTypeDTO } from '../dtos/colony-building-capability-type';

export class ColonyBuildingCapabilityType {
  id: string;
  name: string;

  constructor(data: ColonyBuildingCapabilityTypeDTO) {
    this.id = data.id;
    this.name = data.name;
  }
}
