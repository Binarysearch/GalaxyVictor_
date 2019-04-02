import { Technology } from './game-objects/technology';
import { ShipModel } from './game-objects/ship-model';
import { ResourceType } from './game-objects/resource-type';
import { ColonyBuildingType } from './game-objects/colony-building-type';
import { GalaxyDTO } from './dtos/galaxy';
import { GameObject } from './game-objects/game-object';
import { Injectable } from '@angular/core';
import { StarSystem } from './game-objects/star-system';
import { Planet } from './game-objects/planet';
import { Colony } from './game-objects/colony';
import { Civilization } from './game-objects/civilization';
import { Fleet } from './game-objects/fleet';
import { SessionDTO } from './dtos/session';
import { UserCivilizationDTO } from './dtos/user-civilization';
import { ColonyBuildingCapabilityType } from './game-objects/colony-building-capability-type';

@Injectable({
  providedIn: 'root'
})
export class Store {

  private objects: Map<number, GameObject> = new Map();
  private colonyBuildingTypesMap: Map<string, ColonyBuildingType> = new Map();
  private shipModelsMap: Map<number, ShipModel> = new Map();
  private resourceTypes: Map<string, ResourceType> = new Map();
  private technologies: Map<string, Technology> = new Map();
  private colonyBuildingCapabilityTypes: Map<string, ColonyBuildingCapabilityType> = new Map();

  private _starSystems: StarSystem[] = [];
  private _colonyBuildingTypes: ColonyBuildingType[] = [];
  private _shipModels: ShipModel[] = [];
  private _planets: Planet[] = [];
  private _civilizations: Civilization[] = [];
  private _colonies: Colony[] = [];
  private _fleets: Fleet[] = [];
  private _session: SessionDTO;
  private _galaxy: GalaxyDTO;
  private _userCivilization: UserCivilizationDTO;
  private lag = 0;

  public get session(): SessionDTO {
    return this._session;
  }

  public get starSystems(): StarSystem[] {
    return this._starSystems;
  }

  public get planets(): Planet[] {
    return this._planets;
  }

  public get colonies(): Colony[] {
    return this._colonies;
  }

  public get fleets(): Fleet[] {
    return this._fleets;
  }

  public get civilizations(): Civilization[] {
    return this._civilizations;
  }

  public get galaxy(): GalaxyDTO {
    return this._galaxy;
  }

  public setSession(session: SessionDTO): void {
    this._session = session;
    if (session.user.currentGalaxy) {
      this.setGalaxy(session.user.currentGalaxy);
    }
  }

  public setGalaxy(galaxy: GalaxyDTO): void {
    this._galaxy = galaxy;
  }

  public addStarSystem(starSystem: StarSystem): void {
    this.objects.set(starSystem.id, starSystem);
    this._starSystems.push(starSystem);
  }

  public addPlanet(planet: Planet): void {
    planet.starSystem = this.objects.get(planet.starSystemId) as StarSystem;
    planet.starSystem.planets.push(planet);
    this.objects.set(planet.id, planet);
    this._planets.push(planet);
  }

  addCivilization(civilization: Civilization): any {
    civilization.homeworld = this.objects.get(civilization.homeworldId) as Planet;
    this.objects.set(civilization.id, civilization);
    this._civilizations.push(civilization);
  }

  addColony(colony: Colony): any {
    colony.planet = this.objects.get(colony.planetId) as Planet;
    colony.planet.colony = colony;
    colony.civilization = this.objects.get(colony.civilizationId) as Civilization;
    colony.civilization.colonies.push(colony);
    this.objects.set(colony.id, colony);
    this._colonies.push(colony);
  }

  removeColony(colony: Colony): any {
    colony.planet.colony = null;
    if (colony.civilization.colonies.indexOf(colony) > -1) {
      colony.civilization.colonies.splice(colony.civilization.colonies.indexOf(colony), 1);
    }
    if (this._colonies.indexOf(colony) > -1) {
      this._colonies.splice(this._colonies.indexOf(colony), 1);
    }
    this.objects.delete(colony.id);

  }

  addFleet(fleet: Fleet): any {
    fleet.destination = this.objects.get(fleet.destinationId) as StarSystem;
    fleet.origin = this.objects.get(fleet.originId) as StarSystem;
    fleet.civilization = this.objects.get(fleet.civilizationId) as Civilization;
    fleet.civilization.fleets.push(fleet);
    fleet.destination.fleets.push(fleet);
    fleet.store = this;
    this.objects.set(fleet.id, fleet);
    this._fleets.push(fleet);
  }

  removeFleet(fleet: Fleet): any {
    if (fleet.civilization.fleets.indexOf(fleet) > -1) {
      fleet.civilization.fleets.splice(fleet.civilization.fleets.indexOf(fleet), 1);
    }
    if (fleet.destination.fleets.indexOf(fleet) > -1) {
      fleet.destination.fleets.splice(fleet.destination.fleets.indexOf(fleet), 1);
    }
    if (this._fleets.indexOf(fleet) > -1) {
      this._fleets.splice(this._fleets.indexOf(fleet), 1);
    }
    this.objects.delete(fleet.id);
  }

  public clear(): void {
    this.objects.clear();
    this._fleets = [];
    this._planets = [];
    this._civilizations = [];
    this._colonies = [];
    this._starSystems = [];
  }

  getObjectById(id: number) {
    return this.objects.get(id);
  }

  public get gameTime() {
    const localTime = new Date().getTime();
    return localTime - this.lag;
  }

  public set serverTime(serverTime: number) {
    this.lag = new Date().getTime() - serverTime;
  }

  public set userCivilization(civ: UserCivilizationDTO) {
    this._userCivilization = civ;
  }

  public get userCivilization(): UserCivilizationDTO {
    return this._userCivilization;
  }

  public addColonyBuildingType(type: ColonyBuildingType) {
    this.colonyBuildingTypesMap.set(type.id, type);
    this._colonyBuildingTypes.push(type);
  }

  public getColonyBuildingType(typeId: string): ColonyBuildingType {
    return this.colonyBuildingTypesMap.get(typeId);
  }

  public addShipModel(model: ShipModel) {
    this.shipModelsMap.set(model.id, model);
    this._shipModels.push(model);
  }

  public getShipModel(modelId: number): ShipModel {
    return this.shipModelsMap.get(modelId);
  }

  public addResourceType(type: ResourceType) {
    this.resourceTypes.set(type.id, type);
  }

  public getResourceType(typeId: string): ResourceType {
    return this.resourceTypes.get(typeId);
  }

  public addTechnology(technology: Technology) {
    this.technologies.set(technology.id, technology);
  }

  public getTechnology(id: string): Technology {
    return this.technologies.get(id);
  }

  public addColonyBuildingCapabilityType(type: ColonyBuildingCapabilityType) {
    this.colonyBuildingCapabilityTypes.set(type.id, type);
  }

  public getColonyBuildingCapabilityType(typeId: string): ColonyBuildingCapabilityType {
    return this.colonyBuildingCapabilityTypes.get(typeId);
  }

  public get colonyBuildingTypes(): ColonyBuildingType[] {
    return this._colonyBuildingTypes;
  }

  public get shipModels(): ShipModel[] {
    return this._shipModels;
  }

}
