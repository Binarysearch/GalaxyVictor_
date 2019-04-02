import { Store } from './../../store';
import { Fleet } from './../../game-objects/fleet';
import { CoreService } from './../../services/core.service';
import { Planet } from './../../game-objects/planet';
import { TextService } from './../../services/text.service';
import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { GalaxyMap } from '../galaxy-map';

@Component({
  selector: 'app-planet-window',
  templateUrl: './planet-window.component.html',
  styleUrls: ['./planet-window.component.css']
})
export class PlanetWindowComponent implements OnInit {

  @Input() planet: Planet;
  @Output() closeButton = new EventEmitter();
  maximized = false;

  constructor(private core: CoreService, private map: GalaxyMap, public ts: TextService, private store: Store) { }

  ngOnInit() {
    const statusString = localStorage.getItem('planet-window-status');
    if (statusString) {
      const status = JSON.parse(statusString);
      this.maximized = status.maximized;
    }
  }

  get title(): string {
    return this.ts.strings.planet + ' ' + this.planet.id;
  }

  closeButtonClick() {
    this.closeButton.emit();
  }

  maximizeButtonClick() {
    this.maximized = true;
    this.storeStatus();
  }

  restoreButtonClick() {
    this.maximized = false;
    this.storeStatus();
  }

  storeStatus() {
    const status = {
      maximized: this.maximized,
    };
    localStorage.setItem('planet-window-status', JSON.stringify(status));
  }

  previousPlanet() {
    let idx = -1;
    this.planet.starSystem.planets.find((p, i) => {
      idx = i;
      return this.planet === p;
    });
    const prev = this.planet.starSystem.planets[(idx + this.planet.starSystem.planets.length - 1) % this.planet.starSystem.planets.length];
    this.map.select(prev.id);
  }

  nextPlanet() {
    let idx = -1;
    this.planet.starSystem.planets.find((p, i) => {
      idx = i;
      return this.planet === p;
    });
    const prev = this.planet.starSystem.planets[(idx + this.planet.starSystem.planets.length + 1) % this.planet.starSystem.planets.length];
    this.map.select(prev.id);
  }

  isColonizable(): boolean {
    if (!this.planet.colony) {
      const fleet = this.planet.starSystem.fleets.find((f: Fleet, i) => {
        return f.canColonize && f.civilization.id === this.store.userCivilization.id;
      });
      if (fleet) {
        return true;
      }
    }
    return false;
  }

  colonize() {
    this.core.createColony(this.planet.id);
  }
}
