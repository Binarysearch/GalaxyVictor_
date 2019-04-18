import { GalaxyMap } from './../galaxy/galaxy-map';
import { TextService } from './../services/text.service';
import { Component, OnInit } from '@angular/core';
import { Store } from '../store';
import { Colony } from '../game-objects/colony';
import { Router } from '@angular/router';

@Component({
  selector: 'app-colonies',
  templateUrl: './colonies.component.html',
  styleUrls: ['./colonies.component.css']
})
export class ColoniesComponent implements OnInit {

  private _colonies: Colony[] = [];
  orderBy = 'civilizationId';
  orderDir = -1;

  constructor(private store: Store, public ts: TextService, private galaxyMap: GalaxyMap, private router: Router) { }

  ngOnInit() {
    const state = localStorage.getItem('colonies-window');
    if (state) {
      const stateObj = JSON.parse(state);
      this.orderBy = stateObj.orderBy;
      this.orderDir = stateObj.orderDir;
    }
  }

  get colonies(): Colony[] {
    if (this._colonies.length !== this.store.colonies.length) {

      this._colonies = [];
      this.store.colonies.forEach(c => {
        this._colonies.push(c);
      });

      if (this.orderBy === 'construction') {
        this._colonies.sort((c1, c2) => {
          const cc1 = c1.buildingOrder ? 1 : c1.shipOrder ? 2 : 0;
          const cc2 = c2.buildingOrder ? 1 : c2.shipOrder ? 2 : 0;
          return this.orderDir * (cc1 - cc2);
        });
      } else if (this.orderBy === 'name') {
        this._colonies.sort((c1, c2) => {
          return this.orderDir * ('' + c1.planet.name).localeCompare(c2.planet.name);
        });
      } else {
        this._colonies.sort((c1, c2) => {
          return this.orderDir * (c1[this.orderBy] - c2[this.orderBy]);
        });
      }
    }

    return this._colonies;
  }

  public getConstruction(colony: Colony): string {
    if (colony.buildingOrder) {
      return colony.buildingOrderName;
    } else if (colony.shipOrder) {
      return colony.shipOrderName;
    } else {
      return this.ts.strings.noBuildingOrder;
    }
  }

  public changeBuildingOrderClicked(colony: Colony) {

  }

  public sortBy(fieldName: string) {
    if (fieldName === this.orderBy) {
      this.orderDir *= -1;
    } else {
      this.orderBy = fieldName;
    }
    this._colonies = [];
    localStorage.setItem('colonies-window', JSON.stringify({orderBy: this.orderBy, orderDir: this.orderDir}));
  }

  public clickInColony(colony: Colony) {
    this.galaxyMap.selectAndFocus(colony.planetId);
    this.router.navigate(['galaxy']);
  }
}
