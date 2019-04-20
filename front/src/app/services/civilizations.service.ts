import { ResearchService } from './research.service';
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
import { ShipModelsService } from './ship-models.service';

@Injectable({
  providedIn: 'root'
})
export class CivilizationsService {

  private host = (isDevMode()) ? 'http://localhost:8080' : 'https://galaxyvictor.com';

  private civilizationUrl = this.host + '/api/civilization';
  private civilizationsUrl = this.host + '/api/civilizations';

  private currentCivilizationSubject: Subject<UserCivilizationDTO> = new Subject<UserCivilizationDTO>();

  constructor(private http: HttpClient, private store: Store, private galaxiesService: GalaxiesService,
     private coloniesService: ColoniesService,
     private shipModelsService: ShipModelsService,
     private researchService: ResearchService,
      private fleetsService: FleetsService) {
    this.galaxiesService.getCurrentGalaxy().subscribe((currentGalaxy: GalaxyDTO) => {
      if (currentGalaxy) {
        this.reloadCurrentCivilization();
      }
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
    if (civ) {
      this.loadCivilizations();
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
      this.researchService.loadResearchOrders();
    }, (error: any) => {
      console.log(error);
    });
  }

  public getCurrentCivilization(): Observable<UserCivilizationDTO> {
    return this.currentCivilizationSubject.asObservable();
  }

}
