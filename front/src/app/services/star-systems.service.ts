import { Store } from './../store';
import { Injectable, isDevMode } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { StarSystemDTO } from '../dtos/star-system';
import { GalaxyDTO } from '../dtos/galaxy';
import { GalaxiesService } from './galaxies.service';
import { StarSystem } from '../game-objects/star-system';
import { PlanetsService } from './planets.service';
import { CivilizationsService } from './civilizations.service';
import { Technology } from '../game-objects/technology';

@Injectable({
  providedIn: 'root'
})
export class StarSystemsService {

  private host = (isDevMode()) ? 'http://localhost:8080' : 'https://galaxyvictor.com';

  private starSystemsUrl = this.host + '/api/star-systems';
  private starSystemsTechnologiesUrl = this.host + '/api/star-system-technologies';

  private currentGalaxyId: number;

  constructor(private http: HttpClient, private galaxiesService: GalaxiesService,
     private planetsService: PlanetsService,
     private civilizationsService: CivilizationsService,
      private store: Store) {
    this.galaxiesService.getCurrentGalaxy().subscribe((currentGalaxy: GalaxyDTO) => {
      if (currentGalaxy) {
        this.currentGalaxyId = currentGalaxy.id;
      } else {
        this.currentGalaxyId = null;
      }
      this.reloadStarSystems();
    });
  }

  getStarSystem(id: number): Observable<StarSystemDTO> {
    return this.http.get<StarSystemDTO>(this.starSystemsUrl + `/${id}`);
  }

  getStarSystemTechnologies(id: number): Observable<string[]> {
    return this.http.get<string[]>(this.starSystemsTechnologiesUrl + `?starSystem=${id}`);
  }

  loadStarSystemTechnologies(id: number): void {
    const starSystem = this.store.getObjectById(id) as StarSystem;
    if (starSystem) {
      starSystem.technologies = [];
      starSystem.availableTechnologies = [];

      this.getStarSystemTechnologies(id).subscribe(technologies => {
        const techIds = new Set<string>();

        technologies.forEach(technologyId => {
          techIds.add(technologyId);
          const technology = this.store.getTechnology(technologyId);
          starSystem.technologies.push(technology);
        });

        // add available technologies
        this.store.technologies.forEach(technology => {
          if (techIds.has(technology.id)) {
            return;
          }
          const allPrerequisitesMet = technology.prerequisites.every((tech) => {
            return techIds.has(tech.id);
          });

          if (allPrerequisitesMet) {
            starSystem.availableTechnologies.push(technology);
          }
        });

        starSystem.planets.forEach(p => {
          if (p.colony) {
            p.colony.availableBuildingTypes = null;
          }
        });
      });
    }
  }

  private reloadStarSystems() {
    this.store.clear();
    if (this.currentGalaxyId) {
      this.http.get<StarSystemDTO[]>(this.starSystemsUrl + `?galaxy=${this.currentGalaxyId}`)
      .subscribe((data: StarSystemDTO[]) => {
        data.forEach(ss => {
          this.store.addStarSystem(new StarSystem(ss));
        });
        this.planetsService.loadPlanets(this.currentGalaxyId);
      }, (error: any) => {
        console.log(error);
      });
    }
  }
}
