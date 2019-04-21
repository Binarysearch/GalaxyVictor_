import { HttpClient } from '@angular/common/http';
import { Injectable, isDevMode } from '@angular/core';
import { ColonyDTO } from '../dtos/colony';
import { Store } from '../store';
import { Colony } from '../game-objects/colony';
import { Subject } from 'rxjs';
import { ColonyBuildingTypeDTO } from '../dtos/colony-building-type';
import { PlanetsService } from './planets.service';
import { CivilizationsService } from './civilizations.service';
import { ColonyDetailsService } from './colony-details.service';

@Injectable({
  providedIn: 'root'
})
export class ColoniesService  {

  private host = (isDevMode()) ? 'http://localhost:8080' : 'https://galaxyvictor.com';

  private createColoniesUrl = this.host + '/rest/colonies';
  private coloniesUrl = this.host + '/api/colonies';
  private buildingsUrl = this.host + '/api/colony-buildings';
  private createShipUrl = this.host + '/api/ship-orders';

  colonyDtos: ColonyDTO[];
  private coloniesSubject: Subject<Colony[]> = new Subject();

  constructor(private http: HttpClient, private store: Store,
    private civilizationsService: CivilizationsService,
    private planetsService: PlanetsService,
    private colonyDetailsService: ColonyDetailsService) {

    this.planetsService.getPlanets().subscribe(data => {
      this.formatColonies();
    });

    this.civilizationsService.getCivilizations().subscribe(data => {
      this.formatColonies();
    });

    this.civilizationsService.getCurrentCivilization().subscribe(civ => {
      if (civ) {
        this.loadColonies();
      } else {
        this.colonyDtos = null;
      }
    });
  }

  private loadColonies() {
    this.http.get<ColonyDTO[]>(this.coloniesUrl).subscribe((data: ColonyDTO[]) => {
      this.colonyDtos = data;
      this.formatColonies();
    }, (error: any) => {
      console.log(error);
    });
  }

  private formatColonies() {
    const loadedPlanets = this.store.planets.length > 0;
    const loadedCivilizations = this.store.civilizations.length > 0;
    if (loadedCivilizations && loadedPlanets && this.colonyDtos) {
      this.colonyDtos.forEach(c => {
        this.store.addColony(new Colony(c, this.colonyDetailsService));
      });
      this.colonyDtos = null;
      this.coloniesSubject.next(this.store.colonies);
    }
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
    this.http.post<any>(this.createColoniesUrl, {planet: planetId})
      .subscribe();
  }

}
