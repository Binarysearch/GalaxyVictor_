import { StarSystemsService } from './star-systems.service';
import { CivilizationsService } from './civilizations.service';
import { StarSystem } from './../game-objects/star-system';
import { ResearchOrderDTO } from './../dtos/research-order';
import { Store } from './../store';
import { Injectable, isDevMode } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ResearchOrder } from '../game-objects/research-order';

@Injectable({
  providedIn: 'root'
})
export class ResearchService {

  private host = (isDevMode()) ? 'http://localhost:8080' : 'https://galaxyvictor.com';

  private researchOrdersUrl = this.host + '/api/research-orders';

  private researchOrderDtos: ResearchOrderDTO[];

  constructor(private http: HttpClient,
    private store: Store,
    private starSystemsService: StarSystemsService,
    private civilizationsService: CivilizationsService) {

    this.civilizationsService.getCurrentCivilization().subscribe(civ => {
      if (civ) {
        this.loadResearchOrders();
      } else {
        this.researchOrderDtos = null;
      }
    });

    this.starSystemsService.getStarSystems().subscribe(data => {
      this.formatResearchOrders();
    });

  }

  startResearching(starSystemId: number, technologyId: string): void {
    this.http.post<any>(this.researchOrdersUrl, {starSystem: starSystemId, technology: technologyId})
      .subscribe();
  }

  formatResearchOrders() {
    const loadedStarSystems = this.store.starSystems.length > 0;
    if (loadedStarSystems && this.researchOrderDtos) {
      this.researchOrderDtos.forEach(order => {
        const starSystem = this.store.getObjectById(order.starSystem) as StarSystem;
        const researchOrder = new ResearchOrder(order);
        researchOrder.technology = this.store.getTechnology(order.technology);
        starSystem.researchOrder = researchOrder;
        this.store.addResearchOrder(researchOrder);
      });
      this.researchOrderDtos = null;
    }

  }

  loadResearchOrders() {
    this.http.get<ResearchOrderDTO[]>(this.researchOrdersUrl).subscribe((data) => {
      this.researchOrderDtos = data;
      this.formatResearchOrders();
    });
  }
}
