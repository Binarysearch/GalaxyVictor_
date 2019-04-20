import { Planet } from 'src/app/game-objects/planet';
import { TradeRoute } from './../game-objects/trade-route';
import { CreationTradeRouteDTO } from './../dtos/creation-trade-route';
import { TradeRouteDTO } from './../dtos/trade-route';
import { Injectable, isDevMode } from '@angular/core';
import { Store } from '../store';
import { HttpClient } from '@angular/common/http';
import { DestructionTradeRouteDTO } from './../dtos/destruction-trade-route';
import { CreationTradeRoute } from './../game-objects/creation-trade-route';
import { DestructionTradeRoute } from './../game-objects/destruction-trade-route';
import { CivilizationsService } from './civilizations.service';
import { PlanetsService } from './planets.service';

interface TradeRoutesDTO {
  tradeRoutes: TradeRouteDTO[];
  creationTradeRoutes: CreationTradeRouteDTO[];
  destructionTradeRoutes: DestructionTradeRouteDTO[];
}

@Injectable({
  providedIn: 'root'
})
export class TradeService {

  private host = (isDevMode()) ? 'http://localhost:8080' : 'https://galaxyvictor.com';

  private tradeRoutesUrl = this.host + '/rest/trade-routes';

  private tradeRoutesDto: TradeRoutesDTO;

  constructor(private http: HttpClient, private store: Store,
    private civilizationsService: CivilizationsService,
    private planetsService: PlanetsService) {

    this.planetsService.getPlanets().subscribe(data => {
      this.formatTradeRoutes();
    });

    this.civilizationsService.getCurrentCivilization().subscribe(civ => {
      if (civ) {
        this.loadTradeRoutes();
      } else {
        this.tradeRoutesDto = null;
      }
    });
  }

  createTradeRoute(origin: number, destination: number, resourceType: string, quantity: number): void {
    this.http.post<any>(this.tradeRoutesUrl, {origin: origin, destination: destination, resourceType: resourceType, quantity: quantity})
    .subscribe();
  }

  loadTradeRoutes() {
    this.http.get<TradeRoutesDTO>(this.tradeRoutesUrl).subscribe((data) => {
      this.tradeRoutesDto = data;
      this.formatTradeRoutes();
    });
  }

  formatTradeRoutes() {
    const loadedPlanets = this.store.planets.length > 0;
    if (loadedPlanets && this.tradeRoutesDto) {
      if (this.tradeRoutesDto.tradeRoutes) {
        this.tradeRoutesDto.tradeRoutes.forEach(r => {
          const route = new TradeRoute(r);
          route.origin = this.store.getObjectById(r.origin) as Planet;
          route.destination = this.store.getObjectById(r.destination) as Planet;
          route.resourceType = this.store.getResourceType(r.resourceType);
          this.store.addTradeRoute(route);
        });
      }

      if (this.tradeRoutesDto.creationTradeRoutes) {
        this.tradeRoutesDto.creationTradeRoutes.forEach(r => {
          const route = new CreationTradeRoute(r);
          route.origin = this.store.getObjectById(r.origin) as Planet;
          route.destination = this.store.getObjectById(r.destination) as Planet;
          route.resourceType = this.store.getResourceType(r.resourceType);
          this.store.addCreationTradeRoute(route);
        });
      }

      if (this.tradeRoutesDto.destructionTradeRoutes) {
        this.tradeRoutesDto.destructionTradeRoutes.forEach(r => {
          const route = new DestructionTradeRoute(r);
          route.origin = this.store.getObjectById(r.origin) as Planet;
          route.destination = this.store.getObjectById(r.destination) as Planet;
          route.resourceType = this.store.getResourceType(r.resourceType);
          this.store.addDestructionTradeRoute(route);
        });
      }

      this.tradeRoutesDto = null;
    }
  }


}
