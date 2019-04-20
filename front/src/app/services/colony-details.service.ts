import { TradeService } from './trade.service';
import { ColonyBuilding } from './../game-objects/colony-building';
import { ColonyResource } from './../game-objects/colony-resource';
import { ColonyResourceDTO } from './../dtos/colony-resource';
import { HttpClient } from '@angular/common/http';
import { Injectable, isDevMode } from '@angular/core';
import { ColonyDTO } from '../dtos/colony';
import { Store } from '../store';
import { Colony } from '../game-objects/colony';
import { ColonyBuildingDTO } from '../dtos/colony-building';
import { Observable, Subject } from 'rxjs';
import { ColonyBuildingTypeDTO } from '../dtos/colony-building-type';
import { PlanetsService } from './planets.service';
import { CivilizationsService } from './civilizations.service';

@Injectable({
  providedIn: 'root'
})
export class ColonyDetailsService {

  private host = (isDevMode()) ? 'http://localhost:8080' : 'https://galaxyvictor.com';

  private createColoniesUrl = this.host + '/rest/colonies';
  private coloniesUrl = this.host + '/api/colonies';
  private buildingsUrl = this.host + '/api/colony-buildings';
  private resourcesUrl = this.host + '/api/colony-resources';
  private createShipUrl = this.host + '/api/ship-orders';

  colonyDtos: ColonyDTO[];
  private coloniesSubject: Subject<Colony[]> = new Subject();

  constructor(private http: HttpClient, private store: Store) { }


  getColonyBuildings(colonyId: number): Observable<ColonyBuildingDTO[]> {
    return this.http.get<ColonyBuildingDTO[]>(this.buildingsUrl + `?colony=${colonyId}`);
  }

  getColonyResources(colonyId: number): Observable<ColonyResourceDTO[]> {
    return this.http.get<ColonyResourceDTO[]>(this.resourcesUrl + `?colony=${colonyId}`);
  }

  loadColonyResources(colony: Colony) {
    const resources = [];
    this.getColonyResources(colony.id).subscribe((result: ColonyResourceDTO[]) => {
      result.forEach(r => {
        const resource = new ColonyResource(r);
        resource.type = this.store.getResourceType(r.type);
        resources.push(resource);
      });

      colony.resources = resources;
      colony.invalidateAvailableBuildings();
    });
  }

  loadColonyBuildings(colony: Colony) {
    const buildings = [];
    this.getColonyBuildings(colony.id).subscribe((result: ColonyBuildingDTO[]) => {
      colony.canBuildShips = false;

      result.forEach(b => {
        const building = new ColonyBuilding(b);
        building.type = this.store.getColonyBuildingType(b.type);
        buildings.push(building);

        if (!colony.canBuildShips) {
          building.type.capabilities.forEach(e => {
            if (e.type.id === 'build ships') {
              colony.canBuildShips = true;
            }
          });
        }
      });

      colony.buildings = buildings;
    });
  }

  loadColonyAvailableBuildingTypes(colony: Colony) {

    if (colony.planet.starSystem.technologies) {

      const availableBuildingTypes = [];

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
      colony.buildings.forEach(b => {
        if (builtMap.has(b.type.id)) {
          builtMap.set(b.type.id, 1 + builtMap.get(b.type.id));
        } else {
          builtMap.set(b.type.id, 1);
        }
      });

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
            availableBuildingTypes.push(bt);
          }
        }
      });

      colony.availableBuildings = availableBuildingTypes;
    }
  }
}
