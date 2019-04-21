import { HttpClient } from '@angular/common/http';
import { Injectable, isDevMode } from '@angular/core';
import { Store } from '../store';
import { ShipModelDTO } from '../dtos/ship-model';
import { ShipModel } from '../game-objects/ship-model';
import { CivilizationsService } from './civilizations.service';

interface StoredShipModels {
  ref: number;
  shipModels: ShipModelDTO[];
}

@Injectable({
  providedIn: 'root'
})
export class ShipModelsService {

  private host = (isDevMode()) ? 'http://localhost:8080' : 'https://galaxyvictor.com';

  private shipModelsUrl = this.host + '/api/ship-models';

  private shipModelDtos: ShipModelDTO[];


  constructor(private http: HttpClient, private store: Store, private civilizationsService: CivilizationsService) {
    this.civilizationsService.getCurrentCivilization().subscribe(civ => {
      if (civ) {
        this.loadModels();
      }
    });
  }

  public loadModels() {
    const storedShipModelsString = localStorage.getItem('stored-ship-models');
    const civ = this.store.civilization;
    if (storedShipModelsString) {
      const storedShipModels = JSON.parse(storedShipModelsString) as StoredShipModels;
      if (civ.shipModelsCache && storedShipModels.ref === civ.shipModelsCache) {
        this.shipModelDtos = storedShipModels.shipModels;
        this.formatShipModels();
        return;
      }
    }


    this.http.get<ShipModelDTO[]>(this.shipModelsUrl)
    .subscribe((data: ShipModelDTO[]) => {
      this.shipModelDtos = data;
      localStorage.setItem('stored-ship-models', JSON.stringify({ref: civ.shipModelsCache, shipModels: data}));
      this.formatShipModels();
    }, (error: any) => {
      console.log(error);
    });
  }

  private formatShipModels() {
    this.shipModelDtos.forEach(sm => {
      this.store.addShipModel(new ShipModel(sm));
    });
  }
}
