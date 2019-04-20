import { StarSystemsService } from './star-systems.service';
import { HttpClient } from '@angular/common/http';
import { Injectable, isDevMode } from '@angular/core';
import { PlanetDTO } from '../dtos/planet';
import { Store } from '../store';
import { Planet } from '../game-objects/planet';
import { CivilizationsService } from './civilizations.service';
import { Subject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PlanetsService {

  private host = (isDevMode()) ? 'http://localhost:8080' : 'https://galaxyvictor.com';

  private planetsUrl = this.host + '/api/planets';

  planetDtos: PlanetDTO[];
  private planetsSubject: Subject<Planet[]> = new Subject();

  constructor(private starSystemsService: StarSystemsService, private http: HttpClient,
     private store: Store, private civilizationsService: CivilizationsService) {

      starSystemsService.getStarSystems().subscribe(data => {
        this.formatPlanets();
      });

      civilizationsService.getCurrentCivilization().subscribe(civ => {
        if (civ) {
          this.http.get<PlanetDTO[]>(this.planetsUrl)
          .subscribe((data: PlanetDTO[]) => {
            this.planetDtos = data;
            this.formatPlanets();
          }, (error: any) => {
            console.log(error);
          });
        } else {
          this.planetDtos = null;
        }
      });
  }

  private formatPlanets() {
    const loadedStarSystems = this.store.starSystems.length > 0;
    if (loadedStarSystems && this.planetDtos) {
      this.planetDtos.forEach(p => {
        this.store.addPlanet(new Planet(p));
      });
      this.planetDtos = null;
      this.planetsSubject.next(this.store.planets);
    }
  }

  public getPlanets(): Observable<Planet[]> {
    return this.planetsSubject.asObservable();
  }
}
