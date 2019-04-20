import { StarSystemsService } from './star-systems.service';
import { CivilizationsService } from './civilizations.service';
import { TravelDTO } from './../dtos/travel';
import { HttpClient } from '@angular/common/http';
import { Injectable, isDevMode } from '@angular/core';
import { FleetDTO } from '../dtos/fleet';
import { Store } from '../store';
import { Fleet } from '../game-objects/fleet';
import { StarSystem } from '../game-objects/star-system';

@Injectable({
  providedIn: 'root'
})
export class FleetsService {

  private host = (isDevMode()) ? 'http://localhost:8080' : 'https://galaxyvictor.com';

  private fleetsUrl = this.host + '/api/fleets';
  private travelsUrl = this.host + '/api/travels';

  private fleetDtos: FleetDTO[];

  constructor(private http: HttpClient,
    private store: Store,
    private starSystemsService: StarSystemsService,
    private civilizationsService: CivilizationsService) {

    this.civilizationsService.getCurrentCivilization().subscribe(civ => {
      if (civ) {
        this.loadFleets();
      } else {
        this.fleetDtos = null;
      }
    });

    this.starSystemsService.getStarSystems().subscribe(data => {
      this.formatFleets();
    });

    this.civilizationsService.getCivilizations().subscribe(data => {
      this.formatFleets();
    });

  }

  formatFleets() {
    const loadedStarSystems = this.store.starSystems.length > 0;
    const loadedCivilizations = this.store.civilizations.length > 0;
    if (loadedStarSystems && loadedCivilizations && this.fleetDtos) {
      this.fleetDtos.forEach(f => {
        this.store.addFleet(new Fleet(f));
      });
      this.fleetDtos = null;
    }
  }

  loadFleets() {
    this.http.get<FleetDTO[]>(this.fleetsUrl).subscribe((data) => {
      this.fleetDtos = data;
      this.formatFleets();
    });
  }

  startTravel(fleet: Fleet, destination: StarSystem): any {
    if (fleet.ships.length === fleet.selectedShips.length) {
      this.http.post<TravelDTO>(this.travelsUrl, {fleet: fleet.id, destination: destination.id}).subscribe();
    } else {
      const shipIds = [];
      fleet.selectedShips.forEach(s => {
        shipIds.push(s.id);
      });
      this.http.post<TravelDTO>(this.fleetsUrl, {fleet: fleet.id, ships: shipIds, destination: destination.id}).subscribe();
    }

  }
}
