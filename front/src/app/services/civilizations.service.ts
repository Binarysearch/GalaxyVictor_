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

@Injectable({
  providedIn: 'root'
})
export class CivilizationsService {

  private host = (isDevMode()) ? 'http://localhost:8080' : 'https://galaxyvictor.com';

  private civilizationUrl = this.host + '/api/civilization';
  private civilizationsUrl = this.host + '/api/civilizations';

  private buildingTypesUrl = this.host + '/api/colony-building-types';
  private resourceTypesUrl = this.host + '/api/resource-types';

  private _currentCivilization: UserCivilizationDTO;
  private currentCivilizationSubject: Subject<UserCivilizationDTO> = new Subject<UserCivilizationDTO>();
  private currentGalaxyId: number;

  constructor(private http: HttpClient, private store: Store, private galaxiesService: GalaxiesService,
     private coloniesService: ColoniesService, private fleetsService: FleetsService) {
    this.galaxiesService.getCurrentGalaxy().subscribe((currentGalaxy: GalaxyDTO) => {
      if (currentGalaxy) {
        this.loadTypes();
        this.currentGalaxyId = currentGalaxy.id;
      } else {
        this.currentGalaxyId = null;
      }
    });
  }



  private loadBuildingTypes() {
    this.http.get<ColonyBuildingTypeDTO[]>(this.buildingTypesUrl)
    .subscribe((data: ColonyBuildingTypeDTO[]) => {
      data.forEach(c => {
        const buildingType = new ColonyBuildingType(c);
        c.resources.forEach(r => {
          buildingType.resources.push({resourceType: this.store.getResourceType(r.type), quantity: r.quantity});
        });
        this.store.addColonyBuildingType(buildingType);
      });
    }, (error: any) => {
      console.log(error);
    });
  }

  private loadTypes() {
    this.http.get<ResourceTypeDTO[]>(this.resourceTypesUrl)
    .subscribe((data: ResourceTypeDTO[]) => {
      data.forEach(c => {
        const resourceType = new ResourceType(c);
        this.store.addResourceType(resourceType);
      });
      this.loadBuildingTypes();
    }, (error: any) => {
      console.log(error);
    });
  }

  createCivilization(galaxyId: number, civilizationName: string, homeStarName: string): Observable<UserCivilizationDTO> {
    return this.http.post<UserCivilizationDTO>(this.civilizationUrl,
      {galaxyId: galaxyId, name: civilizationName, homeStarName: homeStarName}
      ).pipe(
      tap<UserCivilizationDTO>((civ: UserCivilizationDTO) => {
        this.currentCivilizationSubject.next(civ);
        this.store.serverTime = civ.serverTime;
        this.store.userCivilization = civ;
        this._currentCivilization = civ;
      })
    );
  }

  public get currentCivilization(): UserCivilizationDTO {
    return this._currentCivilization;
  }

  reloadCurrentCivilization(): void {
    if (this.currentGalaxyId) {
      this.http.get<UserCivilizationDTO>(this.civilizationUrl + `?galaxy=${this.currentGalaxyId}`)
        .subscribe((data: UserCivilizationDTO) => {
          this.currentCivilizationSubject.next(data);
          this.store.serverTime = data.serverTime;
          this.store.userCivilization = data;
          this._currentCivilization = data;
        }, (error: any) => {
          this._currentCivilization = null;
          console.log(error);
        });
    } else {
      this._currentCivilization = null;
    }
  }

  loadCivilizations(): any {
    this.http.get<CivilizationDTO[]>(this.civilizationsUrl)
    .subscribe((data: CivilizationDTO[]) => {
      data.forEach(p => {
        this.store.addCivilization(new Civilization(p));
      });
      this.coloniesService.loadColonies();
      this.fleetsService.loadFleets();
    }, (error: any) => {
      console.log(error);
    });
  }

  public get hasCivilization(): boolean {
    return this._currentCivilization != null;
  }

  public getCurrentCivilization(): Observable<UserCivilizationDTO> {
    return this.currentCivilizationSubject.asObservable();
  }

}
