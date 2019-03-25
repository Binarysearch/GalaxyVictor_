import { Colony } from './../../game-objects/colony';
import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { TextService } from 'src/app/services/text.service';
import { Store } from './../../store';
import { ColonyBuilding } from './../../game-objects/colony-building';
import { ColonyBuildingDTO } from './../../dtos/colony-building';
import { CoreService } from '../../services/core.service';

@Component({
  selector: 'app-colony-window',
  templateUrl: './colony-window.component.html',
  styleUrls: ['./colony-window.component.css']
})
export class ColonyWindowComponent implements OnInit {

  @Input() colony: Colony;
  @Output() closeButton = new EventEmitter();


  constructor(public ts: TextService, private core: CoreService, private store: Store) { }

  ngOnInit() {
  }

  get title(): string {
    return this.ts.strings.colony + ' ' + this.colony.id;
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

  closeButtonClick() {
    this.closeButton.emit();
  }

}
