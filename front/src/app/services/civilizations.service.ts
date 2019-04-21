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

interface StoredCivilizations {
  ref: number;
  civilizations: CivilizationDTO[];
}

@Injectable({
  providedIn: 'root'
})
export class CivilizationsService {

  private host = (isDevMode()) ? 'http://localhost:8080' : 'https://galaxyvictor.com';

  private civilizationUrl = this.host + '/api/civilization';
  private civilizationsUrl = this.host + '/api/civilizations';

  civilizationDtos: CivilizationDTO[];

  private currentCivilizationSubject: Subject<UserCivilizationDTO> = new Subject<UserCivilizationDTO>();
  private civilizationsSubject: Subject<Civilization[]> = new Subject<Civilization[]>();

  constructor(private http: HttpClient, private store: Store, private galaxiesService: GalaxiesService) {

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
    const storedCivilizationsString = localStorage.getItem('stored-civilizations');
    const civ = this.store.civilization;
    if (storedCivilizationsString) {
      const storedCivilizations = JSON.parse(storedCivilizationsString) as StoredCivilizations;
      if (civ.civilizationsCache && storedCivilizations.ref === civ.civilizationsCache) {
        this.civilizationDtos = storedCivilizations.civilizations;
        this.formatCivilizations();
        return;
      }
    }

    this.http.get<CivilizationDTO[]>(this.civilizationsUrl)
    .subscribe((data: CivilizationDTO[]) => {
      this.civilizationDtos = data;
      localStorage.setItem('stored-civilizations', JSON.stringify({ref: civ.civilizationsCache, civilizations: data}));
      this.formatCivilizations();
    }, (error: any) => {
      console.log(error);
    });
  }

  private formatCivilizations() {
    this.civilizationDtos.forEach(p => {
      this.store.addCivilization(new Civilization(p));
    });
    this.civilizationsSubject.next(this.store.civilizations);
  }

  public getCurrentCivilization(): Observable<UserCivilizationDTO> {
    return this.currentCivilizationSubject.asObservable();
  }

  public getCivilizations(): Observable<Civilization[]> {
    return this.civilizationsSubject.asObservable();
  }

}
