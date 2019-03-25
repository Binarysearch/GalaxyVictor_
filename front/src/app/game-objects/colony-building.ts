import { ColonyBuildingDTO } from './../dtos/colony-building';
import { ColonyBuildingType } from './colony-building-type';

export class ColonyBuilding {
  id: number;
  type: ColonyBuildingType;

  constructor(data: ColonyBuildingDTO) {
    this.id = data.id;
  }
}
