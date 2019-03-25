import { Store } from './../../../store';
import { ColonyBuilding } from './../../../game-objects/colony-building';
import { ColonyBuildingDTO } from './../../../dtos/colony-building';
import { CoreService } from '../../../services/core.service';
import { TextService } from './../../../services/text.service';
import { Component, OnInit, Input } from '@angular/core';
import { Colony } from '../../../game-objects/colony';

@Component({
  selector: 'app-colony-details',
  templateUrl: './colony-details.component.html',
  styleUrls: ['./colony-details.component.css']
})
export class ColonyDetailsComponent implements OnInit {

  @Input() colony: Colony;

  constructor(public ts: TextService, private core: CoreService, private store: Store) { }

  ngOnInit() {
  }


  get buildings(): ColonyBuilding[] {
    if (!this.colony.buildings) {
      this.colony.buildings = [];
      this.loadBuildings();
    }
    return this.colony.buildings;
  }

  loadBuildings() {
    this.core.getColonyBuildings(this.colony.id).subscribe((buildings: ColonyBuildingDTO[]) => {
      this.colony.buildings = [];
      buildings.forEach(b => {
        const building = new ColonyBuilding(b);
        building.type = this.store.getColonyBuildingType(b.type);
        this.colony.buildings.push(building);
      });
    });
  }
}
