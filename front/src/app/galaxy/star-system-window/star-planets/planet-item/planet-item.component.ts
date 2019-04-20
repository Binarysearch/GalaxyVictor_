import { Planet } from './../../../../game-objects/planet';
import { CoreService } from '../../../../services/core.service';
import { Fleet } from '../../../../game-objects/fleet';
import { Store } from './../../../../store';
import { GalaxyMap } from './../../../galaxy-map';
import { TextService } from './../../../../services/text.service';
import { PLANET_TYPES, PLANET_SIZES } from '../../../galaxy-constants';
import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-planet-item',
  templateUrl: './planet-item.component.html',
  styleUrls: ['./planet-item.component.css']
})
export class PlanetItemComponent implements OnInit {

  @Input() planet: Planet;

  constructor(private core: CoreService, public ts: TextService, private map: GalaxyMap, private store: Store) { }

  ngOnInit() {
  }

  getPlanetDescription() {
    return this.ts.strings.orbit + ' ' + this.planet.orbit;
  }

  selectPlanet() {
    this.map.select(this.planet.id);
  }


  get planetType() {
    return PLANET_TYPES[this.planet.type - 1];
  }

  get planetSize() {
    return PLANET_SIZES[this.planet.size - 1];
  }

  isColonizable(): boolean {
    if (!this.planet.colony) {
      const fleet = this.planet.starSystem.fleets.find((f: Fleet, i) => {
        return f.canColonize && f.civilization.id === this.store.civilization.id;
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

  selectColony() {
    this.map.select(this.planet.colony.id);
  }
}
