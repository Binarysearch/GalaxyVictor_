import { UserCivilizationDTO } from './../dtos/user-civilization';
import { Store } from './../store';
import { Injectable, isDevMode } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { StarSystemDTO } from '../dtos/star-system';
import { GalaxiesService } from './galaxies.service';
import { StarSystem } from '../game-objects/star-system';
import { CivilizationsService } from './civilizations.service';

interface StoredGalaxy {
  id: number;
  starSystems: StarSystemDTO[];
}

@Injectable({
  providedIn: 'root'
})
export class StarSystemsService {

  private host = (isDevMode()) ? 'http://localhost:8080' : 'https://galaxyvictor.com';

  private starSystemsUrl = this.host + '/api/star-systems';
  private starSystemsTechnologiesUrl = this.host + '/api/star-system-technologies';

  private starSystemsSubject: Subject<StarSystem[]> = new Subject();


  constructor(private http: HttpClient, private galaxiesService: GalaxiesService,
    private civilizationsService: CivilizationsService,
    private store: Store) {

    this.civilizationsService.getCurrentCivilization().subscribe((currentCivilization: UserCivilizationDTO) => {
      this.reloadStarSystems();
      if (currentCivilization) {
        if (this.store.starSystems.length > 0) {
          this.store.starSystems.forEach(ss => {
            ss.clear();
          });
          this.starSystemsSubject.next(this.store.starSystems);
        }
      }
    });
  }

  getStarSystem(id: number): Observable<StarSystemDTO> {
    return this.http.get<StarSystemDTO>(this.starSystemsUrl + `/${id}`);
  }

  getStarSystemTechnologies(id: number): Observable<string[]> {
    return this.http.get<string[]>(this.starSystemsTechnologiesUrl + `?starSystem=${id}`);
  }

  loadStarSystemTechnologies(starSystem: StarSystem): void {
    const technologies = [];
    starSystem.availableTechnologies = [];

    this.getStarSystemTechnologies(starSystem.id).subscribe(result => {

      result.forEach(technologyId => {
        const technology = this.store.getTechnology(technologyId);
        technologies.push(technology);
      });

      // add available technologies
      starSystem.invalidateAvailableTechnologies();

      starSystem.planets.forEach(p => {
        if (p.colony) {
          p.colony.invalidateAvailableBuildings();
        }
      });

      starSystem.technologies = technologies;
    });
  }

  loadAvailableTechnologies(starSystem: StarSystem) {
    const availableTechnologies = [];

    const techIds = new Set<string>();
    starSystem.technologies.forEach(technology => {
      techIds.add(technology.id);
    });

    this.store.technologies.forEach(technology => {
      if (techIds.has(technology.id)) {
        return;
      }
      const allPrerequisitesMet = technology.prerequisites.every((tech) => {
        return techIds.has(tech.id);
      });

      if (allPrerequisitesMet) {
        availableTechnologies.push(technology);
      }
    });

    starSystem.availableTechnologies = availableTechnologies;
  }

  private reloadStarSystems() {
    const storedGalaxyString = localStorage.getItem('stored-galaxy');
    if (storedGalaxyString) {
      const storedGalaxy = JSON.parse(storedGalaxyString) as StoredGalaxy;
      if (storedGalaxy.id === this.store.galaxy.id) {
        storedGalaxy.starSystems.forEach(ss => {
          this.store.addStarSystem(new StarSystem(ss, this));
        });
        this.starSystemsSubject.next(this.store.starSystems);
        return;
      }
    }


    this.http.get<StarSystemDTO[]>(this.starSystemsUrl)
      .subscribe((data: StarSystemDTO[]) => {
        localStorage.setItem('stored-galaxy', JSON.stringify({id: this.store.galaxy.id, starSystems: data}));
        data.forEach(ss => {
          this.store.addStarSystem(new StarSystem(ss, this));
        });
        this.starSystemsSubject.next(this.store.starSystems);
      }, (error: any) => {
        console.log(error);
      });

  }

  public getStarSystems(): Observable<StarSystem[]> {
    return this.starSystemsSubject.asObservable();
  }
}
