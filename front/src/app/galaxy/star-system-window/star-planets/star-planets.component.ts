import { StarSystem } from '../../../game-objects/star-system';
import { CoreService } from '../../../services/core.service';
import { Fleet } from '../../../game-objects/fleet';
import { Store } from './../../../store';
import { GalaxyMap } from './../../galaxy-map';
import { Planet } from './../../../game-objects/planet';
import { TextService } from './../../../services/text.service';
import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-star-planets',
  templateUrl: './star-planets.component.html',
  styleUrls: ['./star-planets.component.css']
})
export class StarPlanetsComponent implements OnInit {

  @Input() starSystem: StarSystem;


  constructor(private core: CoreService, public ts: TextService, private map: GalaxyMap, private store: Store) { }

  ngOnInit() {
  }

  getPlanetDescription(planet: Planet) {
    return this.ts.strings.orbit + ' ' + planet.orbit;
  }

  selectPlanet(planet: Planet) {
    this.map.select(planet.id);
  }

  isColonizable(planet: Planet): boolean {
    if (!planet.colony) {
      const fleet = this.starSystem.fleets.find((f: Fleet, i) => {
        return f.canColonize && f.civilization.id === this.store.userCivilization.id;
      });
      if (fleet) {
        return true;
      }
    }
    return false;
  }

  colonize(planet: Planet) {
    this.core.createColony(planet.id);
  }
}
