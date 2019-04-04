import { ColonyBuildingType } from './colony-building-type';
import { ColonyDTO } from './../dtos/colony';
import { GameObject } from './game-object';
import { Planet } from './planet';
import { Civilization } from './civilization';
import { ColonyBuilding } from './colony-building';
import { ColonyResource } from './colony-resource';

export class Colony implements GameObject {

  id: number;
  planet: Planet;
  civilization: Civilization;
  buildings: ColonyBuilding[];
  availableBuildingTypes: ColonyBuildingType[];
  resources: ColonyResource[];
  buildingOrder: string;
  buildingOrderName: string;
  shipOrder: number;
  shipOrderName: string;
  planetId: number;
  civilizationId: number;
  canBuildShips: boolean;

  constructor(data: ColonyDTO) {
    this.id = data.id;
    this.planetId = data.planet;
    this.civilizationId = data.civilization;
    this.buildingOrder = data.buildingOrder;
    this.buildingOrderName = data.buildingOrderName;
    this.shipOrder = data.shipOrder;
    this.shipOrderName = data.shipOrderName;

  }

  get objectType() { return 'Colony'; }

  get x(): number {
    return this.planet.x;
  }

  get y(): number {
    return this.planet.y;
  }
}
