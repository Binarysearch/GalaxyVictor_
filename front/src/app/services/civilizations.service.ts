import { ResearchService } from './research.service';
import { TechnologyDTO } from './../dtos/technology';
import { Injectable, isDevMode } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { UserCivilizationDTO } from '../dtos/user-civilization';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';
import { GalaxiesService } from './galaxies.service';
import { GalaxyDTO } from '../dtos/galaxy';
import { Store } from '../store';
import { CivilizationDTO } from '../dtos/civilization';
import { Civilization } from '../game-objects/civilization';
import { ColoniesService } from './colonies.service';
import { FleetsService } from './fleets.service';
import { ColonyBuildingType } from '../game-objects/colony-building-type';
import { ColonyBuildingTypeDTO } from '../dtos/colony-building-type';
import { ResourceTypeDTO } from '../dtos/resource-type';
import { ResourceType } from '../game-objects/resource-type';
import { ColonyBuildingCapabilityTypeDTO } from '../dtos/colony-building-capability-type';
import { ColonyBuildingCapabilityType } from '../game-objects/colony-building-capability-type';
import { ShipModelsService } from './ship-models.service';
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
export class CivilizationsService {

  private host = (isDevMode()) ? 'http://localhost:8080' : 'https://galaxyvictor.com';

  private civilizationUrl = this.host + '/api/civilization';
  private civilizationsUrl = this.host + '/api/civilizations';

  private constantDataUrl = this.host + '/api/constant-data';

  private currentCivilizationSubject: Subject<UserCivilizationDTO> = new Subject<UserCivilizationDTO>();

  constructor(private http: HttpClient, private store: Store, private galaxiesService: GalaxiesService,
     private coloniesService: ColoniesService,
     private shipModelsService: ShipModelsService,
     private researchService: ResearchService,
      private fleetsService: FleetsService) {
    this.galaxiesService.getCurrentGalaxy().subscribe((currentGalaxy: GalaxyDTO) => {
      if (currentGalaxy) {
        this.reloadCurrentCivilization();
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

  createCivilization(galaxyId: number, civilizationName: string, homeStarName: string): Observable<UserCivilizationDTO> {
    return this.http.post<UserCivilizationDTO>(this.civilizationUrl,
      {galaxyId: galaxyId, name: civilizationName, homeStarName: homeStarName}
      ).pipe(
      tap<UserCivilizationDTO>((civ: UserCivilizationDTO) => {
        this.onCivilizationChange(civ);
      })
    );
  }

  reloadCurrentCivilization(): void {
    if (this.store.galaxy) {
      this.http.get<UserCivilizationDTO>(this.civilizationUrl)
        .subscribe((data: UserCivilizationDTO) => {
          this.onCivilizationChange(data);
        }, (error: any) => {
          this.onCivilizationChange(null);
        });
    } else {
      this.onCivilizationChange(null);
    }
  }

  onCivilizationChange(civ: UserCivilizationDTO) {
    this.store.clearCivilization();
    this.store.setCivilization(civ);
    this.currentCivilizationSubject.next(civ);
  }

  loadCivilizations(): any {
    this.http.get<CivilizationDTO[]>(this.civilizationsUrl)
    .subscribe((data: CivilizationDTO[]) => {
      data.forEach(p => {
        this.store.addCivilization(new Civilization(p));
      });
      this.coloniesService.loadColonies();
      this.fleetsService.loadFleets();
      this.researchService.loadResearchOrders();
    }, (error: any) => {
      console.log(error);
    });
  }

  public getCurrentCivilization(): Observable<UserCivilizationDTO> {
    return this.currentCivilizationSubject.asObservable();
  }

}
