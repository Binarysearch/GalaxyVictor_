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

  constructor(private http: HttpClient, private store: Store) { }

  startResearching(starSystemId: number, technologyId: string): void {
    this.http.post<any>(this.researchOrdersUrl, {starSystem: starSystemId, technology: technologyId})
      .subscribe();
  }

  loadResearchOrders() {
    this.http.get<ResearchOrderDTO[]>(this.researchOrdersUrl)
      .subscribe((data) => {
        data.forEach(order => {
          const starSystem = this.store.getObjectById(order.starSystem) as StarSystem;
          const researchOrder = new ResearchOrder(order);
          researchOrder.technology = this.store.getTechnology(order.technology);
          starSystem.researchOrder = researchOrder;
          this.store.addResearchOrder(researchOrder);
        });
      });
  }
}
