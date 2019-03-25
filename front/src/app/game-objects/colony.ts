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
  resources: ColonyResource[];
  buildingOrder: string;
  buildingOrderName: string;
  planetId: number;
  civilizationId: number;

  constructor(data: ColonyDTO) {
    this.id = data.id;
    this.planetId = data.planet;
    this.civilizationId = data.civilization;
    this.buildingOrder = data.buildingOrder;
    this.buildingOrderName = data.buildingOrderName;
  }

  get objectType() { return 'Colony'; }

  get x(): number {
    return this.planet.x;
  }

  get y(): number {
    return this.planet.y;
  }
}
