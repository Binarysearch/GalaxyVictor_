import { SessionDTO } from './../dtos/session';
import { AuthService } from './auth.service';
import { TechnologyDTO } from './../dtos/technology';
import { Injectable, isDevMode } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Store } from '../store';
import { ColonyBuildingType } from '../game-objects/colony-building-type';
import { ColonyBuildingTypeDTO } from '../dtos/colony-building-type';
import { ResourceTypeDTO } from '../dtos/resource-type';
import { ResourceType } from '../game-objects/resource-type';
import { ColonyBuildingCapabilityTypeDTO } from '../dtos/colony-building-capability-type';
import { ColonyBuildingCapabilityType } from '../game-objects/colony-building-capability-type';
import { Technology } from '../game-objects/technology';


interface ConstantDataDTO {
  technologies: TechnologyDTO[];
  resourceTypes: ResourceTypeDTO[];
  colonyBuildingCapabilityTypes: ColonyBuildingCapabilityTypeDTO[];
  colonyBuildingTypes: ColonyBuildingTypeDTO[];
}

@Injectable({
  providedIn: 'root'
})
export class ConstantDataService {

  private host = (isDevMode()) ? 'http://localhost:8080' : 'https://galaxyvictor.com';

  private constantDataUrl = this.host + '/api/constant-data';

  constructor(private http: HttpClient, private store: Store, private authService: AuthService) {
   this.authService.getCurrentSession().subscribe((session: SessionDTO) => {
     if (session) {
       this.loadConstantData();
     }
   });
 }

  private loadConstantData() {
    this.http.get<ConstantDataDTO>(this.constantDataUrl)
    .subscribe((data: ConstantDataDTO) => {

      // technologies
      data.technologies.forEach(t => {
        const technology = new Technology(t);
        if (t.prerequisites) {
          t.prerequisites.forEach(id => {
            const prerequisite = this.store.getTechnology(id);
            technology.prerequisites.push(prerequisite);
          });
        }
        this.store.addTechnology(technology);
      });

      // resource types
      data.resourceTypes.forEach(c => {
        const resourceType = new ResourceType(c);
        this.store.addResourceType(resourceType);
      });

      // colony building capability types
      data.colonyBuildingCapabilityTypes.forEach(c => {
        const capabilityType = new ColonyBuildingCapabilityType(c);
        this.store.addColonyBuildingCapabilityType(capabilityType);
      });

      // colony building types
      data.colonyBuildingTypes.forEach(c => {
        const buildingType = new ColonyBuildingType(c);
        if (c.resources) {
          c.resources.forEach(r => {
            buildingType.resources.push({resourceType: this.store.getResourceType(r.type), quantity: r.quantity});
          });
        }
        if (c.costs) {
          c.costs.forEach(r => {
            buildingType.costs.push({resourceType: this.store.getResourceType(r.type), quantity: r.quantity});
          });
        }
        if (c.capabilities) {
          c.capabilities.forEach(r => {
            buildingType.capabilities.push({type: this.store.getColonyBuildingCapabilityType(r.type)});
          });
        }
        if (c.prerequisites) {
          c.prerequisites.forEach(id => {
            const prerequisite = this.store.getTechnology(id);
            buildingType.prerequisites.push(prerequisite);
          });
        }
        this.store.addColonyBuildingType(buildingType);
      });

      console.log(this.store);
    }, (error: any) => {
      console.log(error);
    });
  }
}
