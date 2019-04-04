import { CoreService } from './../../../services/core.service';
import { Colony } from './../../../game-objects/colony';
import { Component, OnInit, Input } from '@angular/core';
import { Technology } from 'src/app/game-objects/technology';
import { TextService } from 'src/app/services/text.service';

@Component({
  selector: 'app-colony-research',
  templateUrl: './colony-research.component.html',
  styleUrls: ['./colony-research.component.css']
})
export class ColonyResearchComponent implements OnInit {

  @Input() colony: Colony;

  constructor(private core: CoreService, public ts: TextService) { }

  ngOnInit() {
  }

  public get acquiredTechnologies(): Technology[] {
    return this.colony.planet.starSystem.technologies;
  }

  public get availableTechnologies(): Technology[] {
    return this.colony.planet.starSystem.availableTechnologies;
  }

  startResearching(tech: Technology) {
    this.core.startResearching(this.colony.planet.starSystem.id, tech.id);
  }
}
