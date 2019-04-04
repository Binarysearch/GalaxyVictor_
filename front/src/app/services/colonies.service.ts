import { ColonyBuilding } from './../game-objects/colony-building';
import { ColonyResource } from './../game-objects/colony-resource';
import { ColonyResourceDTO } from './../dtos/colony-resource';
import { HttpClient } from '@angular/common/http';
import { Injectable, isDevMode } from '@angular/core';
import { ColonyDTO } from '../dtos/colony';
import { Store } from '../store';
import { Colony } from '../game-objects/colony';
import { ColonyBuildingDTO } from '../dtos/colony-building';
import { Observable } from 'rxjs';
import { ColonyBuildingTypeDTO } from '../dtos/colony-building-type';

@Injectable({
  providedIn: 'root'
})
export class ColoniesService  {

  private host = (isDevMode()) ? 'http://localhost:8080' : 'https://galaxyvictor.com';

  private coloniesUrl = this.host + '/api/colonies';
  private buildingsUrl = this.host + '/api/colony-buildings';
  private resourcesUrl = this.host + '/api/colony-resources';
  private createShipUrl = this.host + '/api/ship-orders';


  constructor(private http: HttpClient, private store: Store) {

  }

  public loadColonies() {
    this.http.get<ColonyDTO[]>(this.coloniesUrl)
    .subscribe((data: ColonyDTO[]) => {
      data.forEach(c => {
        this.store.addColony(new Colony(c));
      });
    }, (error: any) => {
      console.log(error);
    });
  }

  getColonyBuildings(colonyId: number): Observable<ColonyBuildingDTO[]> {
    return this.http.get<ColonyBuildingDTO[]>(this.buildingsUrl + `?colony=${colonyId}`);
  }

  getColonyResources(colonyId: number): Observable<ColonyResourceDTO[]> {
    return this.http.get<ColonyResourceDTO[]>(this.resourcesUrl + `?colony=${colonyId}`);
  }

  changeColonyBuildingOrder(colonyId: number, buildingTypeId: string): void {
    this.http.post<ColonyBuildingTypeDTO>(this.buildingsUrl, {colony: colonyId, buildingType: buildingTypeId})
      .subscribe((type: ColonyBuildingTypeDTO) => {

      });
  }

  changeShipOrder(colonyId: number, shipModelId: number): void {
    this.http.post<any>(this.createShipUrl, {colony: colonyId, shipModel: shipModelId})
      .subscribe();
  }

  createColony(planetId: number) {
    this.http.post<any>(this.coloniesUrl, {planet: planetId})
      .subscribe();
  }

  loadColonyResources(id: number) {
    const colony = this.store.getObjectById(id) as Colony;
    colony.resources = [];
    this.getColonyResources(id).subscribe((resources: ColonyResourceDTO[]) => {
      resources.forEach(r => {
        const resource = new ColonyResource(r);
        resource.type = this.store.getResourceType(r.type);
        colony.resources.push(resource);
      });

      colony.availableBuildingTypes = null;
    });
  }

  loadColonyBuildings(id: number) {
    const colony = this.store.getObjectById(id) as Colony;
    colony.buildings = [];
    this.getColonyBuildings(id).subscribe((buildings: ColonyBuildingDTO[]) => {
      colony.canBuildShips = false;

      buildings.forEach(b => {
        const building = new ColonyBuilding(b);
        building.type = this.store.getColonyBuildingType(b.type);
        colony.buildings.push(building);

        if (!colony.canBuildShips) {
          building.type.capabilities.forEach(e => {
            if (e.type.id === 'build ships') {
              colony.canBuildShips = true;
            }
          });
        }
      });
    });
  }

  loadColonyAvailableBuildingTypes(id: number) {
    const colony = this.store.getObjectById(id) as Colony;

    if (!colony.resources) {
      this.loadColonyResources(id);
    }

    if (colony.planet.starSystem.technologies && !colony.availableBuildingTypes) {

      colony.availableBuildingTypes = [];

      // acquired tech set
      const techIds = new Set<string>();

      colony.planet.starSystem.technologies.forEach(technology => {
        techIds.add(technology.id);
      });

      // maps resource -> quantity
      const resourceMap = new Map<string, number>();
      colony.resources.forEach(r => {
        resourceMap.set(r.type.id, r.quantity);
      });

      // maps built colony buildings -> quantity
      const builtMap = new Map<string, number>();
      if (colony.buildings) {
        colony.buildings.forEach(b => {
          if (builtMap.has(b.type.id)) {
            builtMap.set(b.type.id, 1 + builtMap.get(b.type.id));
          } else {
            builtMap.set(b.type.id, 1);
          }
        });
      }

      // check for every building type that there are enought of every resource, is buildable and
      // if it is not repeatable and it is built then it is not available
      this.store.colonyBuildingTypes.forEach(bt => {
        if (bt.buildable && (!builtMap.has(bt.id) || bt.repeatable)) {
          let available = true;
          bt.resources.forEach(r => {
            let availableQuantity = resourceMap.get(r.resourceType.id);
            if (!availableQuantity) {
              availableQuantity = 0;
            }
            available = available && (availableQuantity + r.quantity >= 0);
          });

          const allPrerequisitesMet = bt.prerequisites.every((tech) => {
            return techIds.has(tech.id);
          });

          available = available && allPrerequisitesMet;

          if (available) {
            colony.availableBuildingTypes.push(bt);
          }
        }
      });
    } else {
      colony.availableBuildingTypes = [];
    }
  }
}
