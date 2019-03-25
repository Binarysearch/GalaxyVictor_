import { ColonyResourceDTO } from './../dtos/colony-resource';
import { HttpClient } from '@angular/common/http';
import { Injectable, isDevMode } from '@angular/core';
import { ColonyDTO } from '../dtos/colony';
import { Store } from '../store';
import { Colony } from '../game-objects/colony';
import { ColonyBuildingDTO } from '../dtos/colony-building';
import { Observable } from 'rxjs';
import { ColonyBuildingTypeDTO } from '../dtos/colony-building-type';

@Injectable({
  providedIn: 'root'
})
export class ColoniesService  {

  private host = (isDevMode()) ? 'http://localhost:8080' : 'https://galaxyvictor.com';

  private coloniesUrl = this.host + '/api/colonies';
  private buildingsUrl = this.host + '/api/colony-buildings';
  private resourcesUrl = this.host + '/api/colony-resources';


  constructor(private http: HttpClient, private store: Store) {

  }

  public loadColonies() {
    this.http.get<ColonyDTO[]>(this.coloniesUrl)
    .subscribe((data: ColonyDTO[]) => {
      data.forEach(c => {
        this.store.addColony(new Colony(c));
      });
    }, (error: any) => {
      console.log(error);
    });
  }

  getColonyBuildings(colonyId: number): Observable<ColonyBuildingDTO[]> {
    return this.http.get<ColonyBuildingDTO[]>(this.buildingsUrl + `?colony=${colonyId}`);
  }

  getColonyResources(colonyId: number): Observable<ColonyResourceDTO[]> {
    return this.http.get<ColonyResourceDTO[]>(this.resourcesUrl + `?colony=${colonyId}`);
  }

  changeColonyBuildingOrder(colonyId: number, buildingTypeId: number): void {
    this.http.post<ColonyBuildingTypeDTO>(this.buildingsUrl, {colony: colonyId, buildingType: buildingTypeId})
      .subscribe((type: ColonyBuildingTypeDTO) => {
        const colony = this.store.getObjectById(colonyId) as Colony;
        colony.buildingOrder = type.id;
        colony.buildingOrderName = type.name;
      });
  }
}
