import { StarSystem } from './../game-objects/star-system';
import { PlanetDTO } from './../dtos/planet';
import { Store } from './../store';
import { Injectable, isDevMode } from '@angular/core';
import { Subject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { WebsocketService } from './websocket.service';
import { Planet } from '../game-objects/planet';
import { Fleet } from '../game-objects/fleet';
import { FleetDTO } from '../dtos/fleet';
import { ColonyDTO } from '../dtos/colony';
import { Colony } from '../game-objects/colony';
import { CivilizationDTO } from '../dtos/civilization';
import { Civilization } from '../game-objects/civilization';

const DEV_SOCKET_URL = `ws://localhost:8080/socket`;
const PROD_SOCKET_URL = `wss://galaxyvictor.com/socket/`;

export interface Message {
  type: string;
  payload: any;
}

interface FinishTravelDTO {
  planets: PlanetDTO[];
  fleets: FleetDTO[];
  colonies: ColonyDTO[];
  civilizations: CivilizationDTO[];
}

interface RemoveFleetDTO {
  id: number;
}

interface VisibilityLostDTO {
  starSystem: number;
}

@Injectable({
  providedIn: 'root'
})
export class MessagingService {
  private subject: Subject<Message>;

  constructor(wsService: WebsocketService, private store: Store) {
    this.subject = <Subject<Message>>wsService
      .connect((isDevMode()) ? DEV_SOCKET_URL : PROD_SOCKET_URL)
      .pipe(
        map((response: MessageEvent): Message => {
          let type: string;
          let payload: any;
          try {
            const data = JSON.parse(response.data);
            type = data.type;
            payload = data.payload;
          } catch (error) {
            type = 'Error';
            payload = { name: error.name, message: error.message };
          }

          return {type: type, payload: payload };
        })
      );

    this.getMessages().subscribe((m: Message) => {
      console.log(m);
      if (m.type === 'FinishTravel') {
        const payload = m.payload as FinishTravelDTO;
        if (payload.planets) {
          payload.planets.forEach((p: PlanetDTO) => {
            if (!this.store.getObjectById(p.id)) {
              this.store.addPlanet(new Planet(p));
            }
          });
        }
        if (payload.civilizations) {
          payload.civilizations.forEach((c: CivilizationDTO) => {
            const civilization = this.store.getObjectById(c.id) as Civilization;
            if (!civilization) {
              this.store.addCivilization(new Civilization(c));
            }
          });
        }
        if (payload.fleets) {
          payload.fleets.forEach((f: FleetDTO) => {
            this.store.addFleet(new Fleet(f));
          });
        }
        if (payload.colonies) {
          payload.colonies.forEach((c: ColonyDTO) => {
            this.store.addColony(new Colony(c));
          });
        }
      }
      if (m.type === 'RemoveFleet') {
        const payload = m.payload as RemoveFleetDTO;
        const fleet = this.store.getObjectById(payload.id) as Fleet;
        if (fleet) {
          this.store.removeFleet(fleet);
        }
      }
      if (m.type === 'Fleet') {
        const payload = m.payload as FleetDTO;
        const fleet = this.store.getObjectById(payload.id) as Fleet;
        const newFleet = new Fleet(payload);
        if (fleet) {
          newFleet.ships = fleet.ships;
          newFleet.selectedShips = fleet.selectedShips;
          this.store.removeFleet(fleet);
        }
        this.store.addFleet(newFleet);
      }
      if (m.type === 'Civilization') {
        const payload = m.payload as CivilizationDTO;
        const civilization = this.store.getObjectById(payload.id) as Civilization;
        if (!civilization) {
          this.store.addCivilization(new Civilization(payload));
        }
      }
      if (m.type === 'VisibilityLost') {
        const payload = m.payload as VisibilityLostDTO;
        const starSystem = this.store.getObjectById(payload.starSystem) as StarSystem;
        if (starSystem) {
          // TODO: remove all fleets and colonies from store that arent owned by user civilization
          starSystem.fleets.forEach((f: Fleet) => {
            if (f.civilizationId !== this.store.userCivilization.id) {
              this.store.removeFleet(f);
            }
          });
          starSystem.planets.forEach((p: Planet) => {
            if (p.colony && p.colony.civilizationId !== this.store.userCivilization.id) {
              this.store.removeColony(p.colony);
            }
          });


        }
      }

    });
  }

  send(msg: Message) {
    this.subject.next(msg);
  }

  getMessages(): Observable<Message> {
    return this.subject.asObservable();
  }
}
