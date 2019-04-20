import { HttpClient } from '@angular/common/http';
import { Injectable, isDevMode } from '@angular/core';
import { Store } from '../store';
import { ShipModelDTO } from '../dtos/ship-model';
import { ShipModel } from '../game-objects/ship-model';
import { CivilizationsService } from './civilizations.service';

@Injectable({
  providedIn: 'root'
})
export class ShipModelsService {

  private host = (isDevMode()) ? 'http://localhost:8080' : 'https://galaxyvictor.com';

  private shipModelsUrl = this.host + '/api/ship-models';


  constructor(private http: HttpClient, private store: Store, private civilizationsService: CivilizationsService) {
    this.civilizationsService.getCurrentCivilization().subscribe(civ => {
      if (civ) {
        this.loadModels();
      }
    });
  }

  public loadModels() {
    this.http.get<ShipModelDTO[]>(this.shipModelsUrl)
    .subscribe((data: ShipModelDTO[]) => {
      data.forEach(sm => {
        this.store.addShipModel(new ShipModel(sm));
      });
    }, (error: any) => {
      console.log(error);
    });
  }

}
