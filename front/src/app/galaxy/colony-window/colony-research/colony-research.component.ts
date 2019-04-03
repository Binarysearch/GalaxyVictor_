import { CoreService } from './../../../services/core.service';
import { Colony } from './../../../game-objects/colony';
import { Component, OnInit, Input } from '@angular/core';
import { Technology } from 'src/app/game-objects/technology';

@Component({
  selector: 'app-colony-research',
  templateUrl: './colony-research.component.html',
  styleUrls: ['./colony-research.component.css']
})
export class ColonyResearchComponent implements OnInit {

  @Input() colony: Colony;

  constructor(private core: CoreService) { }

  ngOnInit() {
  }

  public get acquiredTechnologies(): Technology[] {
    if (!this.colony.planet.starSystem.technologies) {
      this.core.loadStarSystemTechnologies(this.colony.planet.starSystem.id);
    }
    return this.colony.planet.starSystem.technologies;
  }

  public get availableTechnologies(): Technology[] {
    return this.colony.planet.starSystem.availableTechnologies;
  }


}
