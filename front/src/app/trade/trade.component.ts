import { CoreService } from 'src/app/services/core.service';
import { Colony } from './../game-objects/colony';
import { Planet } from 'src/app/game-objects/planet';
import { Store } from './../store';
import { TextService } from 'src/app/services/text.service';
import { Component, OnInit } from '@angular/core';
import { CreationTradeRoute } from '../game-objects/creation-trade-route';
import { ResourceType } from '../game-objects/resource-type';
import { JsonPipe } from '@angular/common';

@Component({
  selector: 'app-trade',
  templateUrl: './trade.component.html',
  styleUrls: ['./trade.component.css']
})
export class TradeComponent implements OnInit {

  now = 1;
  creatingRoute = false;

  newRouteOriginId = 0;
  newRouteDestinationId = 0;
  newRouteQuantity = 2;
  newRouteResourceTypeId = null;

  constructor(public ts: TextService, public store: Store, private core: CoreService) { }

  ngOnInit() {
    setInterval(() => {
      this.now = new Date().getTime();
    }, 87);
  }

  getProgress(route: CreationTradeRoute) {
    const totalTime = route.finishTime - route.startedTime;

    return (100 * (this.store.gameTime - route.startedTime) / totalTime).toFixed(1);
  }

  getPlanetName(planet: Planet): string {
    return planet.starSystem.name + ' ' + planet.orbit;
  }

  toggleCreatingRoute() {
    this.creatingRoute = !this.creatingRoute;
    if (!this.creatingRoute) {
      this.newRouteOriginId = 0;
      this.newRouteDestinationId = 0;
      this.newRouteQuantity = 2;
      this.newRouteResourceTypeId = null;
    }
  }

  createRoute() {
    this.core.createTradeRoute(this.newRouteOriginId * 1,
       this.newRouteDestinationId * 1, this.newRouteResourceTypeId, this.newRouteQuantity * 1);
  }

  get newRouteOrigin(): Colony {
    return this.store.getObjectById(this.newRouteOriginId * 1) as Colony;
  }

  get newRouteDestination(): Colony {
    return this.store.getObjectById(this.newRouteDestinationId * 1) as Colony;
  }

}
