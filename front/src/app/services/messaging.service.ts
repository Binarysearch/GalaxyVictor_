import { ColoniesService } from './colonies.service';
import { ResearchOrderDTO } from './../dtos/research-order';
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
import { ResearchOrder } from '../game-objects/research-order';
import { ColonyBuilding } from '../game-objects/colony-building';

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

interface ColonyBuildingOrderDTO {
  colony: number;
  buildingTypeId: string;
  name: string;
}

interface ShipBuildingOrderDTO {
  colony: number;
  shipModelId: number;
  name: string;
}

interface FinishResearchOrderDTO {
  starSystem: number;
  technology: string;
}

interface FinishColonyBuildingDTO {
  colony: number;
}

interface FinishBuildingShipDTO {
  colony: number;
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

  constructor(wsService: WebsocketService, private store: Store, private coloniesService: ColoniesService) {
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
            this.store.addColony(new Colony(c, this.coloniesService));
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
      if (m.type === 'ResearchOrder') {
        const payload = m.payload as ResearchOrderDTO;
        const starSystem = this.store.getObjectById(payload.starSystem) as StarSystem;
        const researchOrder = new ResearchOrder(payload);
        this.store.removeResearchOrder(payload.starSystem);
        researchOrder.technology = this.store.getTechnology(payload.technology);
        starSystem.researchOrder = researchOrder;
        this.store.addResearchOrder(researchOrder);
      }
      if (m.type === 'FinishResearchOrder') {
        const payload = m.payload as FinishResearchOrderDTO;
        const starSystem = this.store.getObjectById(payload.starSystem) as StarSystem;
        this.store.removeResearchOrder(payload.starSystem);
        starSystem.technologies.push(starSystem.researchOrder.technology);
        starSystem.invalidateAvailableTechnologies();
        starSystem.researchOrder = null;
        starSystem.planets.forEach(p => {
          if (p.colony) {
            // reload buildings
            p.colony.invalidateAvailableBuildings();
          }
        });
      }
      if (m.type === 'ColonyBuildingOrder') {
        const payload = m.payload as ColonyBuildingOrderDTO;
        const colony = this.store.getObjectById(payload.colony) as Colony;
        if (colony) {
          colony.shipOrder = null;
          colony.shipOrderName = null;
          colony.buildingOrder = payload.buildingTypeId;
          colony.buildingOrderName = payload.name;
        }
      }
      if (m.type === 'ShipBuildingOrder') {
        const payload = m.payload as ShipBuildingOrderDTO;
        const colony = this.store.getObjectById(payload.colony) as Colony;
        if (colony) {
          colony.buildingOrder = null;
          colony.buildingOrderName = null;
          colony.shipOrder = payload.shipModelId;
          colony.shipOrderName = payload.name;
        }
      }
      if (m.type === 'FinishColonyBuilding') {
        const payload = m.payload as FinishColonyBuildingDTO;
        const colony = this.store.getObjectById(payload.colony) as Colony;
        if (colony) {
          colony.buildingOrder = null;
          colony.buildingOrderName = null;
          colony.shipOrder = null;
          colony.shipOrderName = null;
          colony.invalidateBuildings();
          colony.invalidateResources();
          colony.invalidateAvailableBuildings();
        }
      }
      if (m.type === 'FinishBuildingShip') {
        const payload = m.payload as FinishBuildingShipDTO;
        const colony = this.store.getObjectById(payload.colony) as Colony;
        if (colony) {
          colony.buildingOrder = null;
          colony.buildingOrderName = null;
          colony.shipOrder = null;
          colony.shipOrderName = null;
          colony.invalidateBuildings();
          colony.invalidateResources();
          colony.invalidateAvailableBuildings();
        }
      }
      if (m.type === 'Fleet') {
        const payload = m.payload as FleetDTO;
        const fleet = this.store.getObjectById(payload.id) as Fleet;
        const newFleet = new Fleet(payload);
        if (fleet) {
          this.store.removeFleet(fleet);
        }
        this.store.addFleet(newFleet);
      }
      if (m.type === 'Colony') {
        const payload = m.payload as ColonyDTO;
        const colony = this.store.getObjectById(payload.id) as Colony;
        const newColony = new Colony(payload, this.coloniesService);
        if (colony) {
          this.store.removeColony(colony);
        }
        this.store.addColony(newColony);
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
