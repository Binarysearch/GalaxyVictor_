import { ColonyBuildingType } from './colony-building-type';
import { ColonyDTO } from './../dtos/colony';
import { GameObject } from './game-object';
import { Planet } from './planet';
import { Civilization } from './civilization';
import { ColonyBuilding } from './colony-building';
import { ColonyResource } from './colony-resource';
import { ColoniesService } from '../services/colonies.service';

export class Colony implements GameObject {

  id: number;
  planet: Planet;
  civilization: Civilization;

  private _buildings: ColonyBuilding[] = [];
  private invalidatedBuildings = true;

  private _availableBuildingTypes: ColonyBuildingType[] = [];
  private invalidatedAvailableBuildings = true;

  private _resources: ColonyResource[] = [];
  private invaliadatedResources = true;

  private _resourceQuantityMap: Map<string, number> = new Map();

  buildingOrder: string;
  buildingOrderName: string;
  shipOrder: number;
  shipOrderName: string;
  planetId: number;
  civilizationId: number;
  canBuildShips: boolean;

  constructor(data: ColonyDTO, private coloniesService: ColoniesService) {
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

  get buildings(): ColonyBuilding[] {
    if (this.invalidatedBuildings) {
      this.coloniesService.loadColonyBuildings(this);
      this.invalidatedBuildings = false;
    }
    return this._buildings;
  }

  set buildings(buildings: ColonyBuilding[]) {
    this._buildings = buildings;
  }

  get resources(): ColonyResource[] {
    if (this.invaliadatedResources) {
      this.coloniesService.loadColonyResources(this);
      this.invaliadatedResources = false;
    }
    return this._resources;
  }

  set resources(resources: ColonyResource[]) {
    this._resources = resources;
    this._resourceQuantityMap = new Map();
    this._resources.forEach(r => {
      this._resourceQuantityMap.set(r.type.id, r.quantity);
    });
  }

  getResourceQuantity(resourceTypeId: string): number {
    const quantity = this._resourceQuantityMap.get(resourceTypeId);
    if (!quantity) {
      return 0;
    }
    return quantity;
  }

  get availableBuildings(): ColonyBuildingType[] {
    if (this.invalidatedAvailableBuildings) {
      this.coloniesService.loadColonyAvailableBuildingTypes(this);
      this.invalidatedAvailableBuildings = false;
    }
    return this._availableBuildingTypes;
  }

  set availableBuildings(availableBuildings: ColonyBuildingType[]) {
    this._availableBuildingTypes = availableBuildings;
  }

  invalidateBuildings() {
    this.invalidatedBuildings = true;
  }

  invalidateResources() {
    this.invaliadatedResources = true;
  }

  invalidateAvailableBuildings() {
    this.invalidatedAvailableBuildings = true;
  }

}
