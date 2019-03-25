import { ColonyBuildingType } from './../../game-objects/colony-building-type';
import { ColonyResourceDTO } from './../../dtos/colony-resource';
import { ColonyResource } from './../../game-objects/colony-resource';
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

  selectingBuildingOrder: boolean;

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

  get resources(): ColonyResource[] {
    if (!this.colony.resources) {
      this.colony.resources = [];
      this.loadResources();
    }
    return this.colony.resources;
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

  loadResources() {
    this.core.getColonyResources(this.colony.id).subscribe((resources: ColonyResourceDTO[]) => {
      this.colony.resources = [];
      resources.forEach(r => {
        const resource = new ColonyResource(r);
        resource.type = this.store.getResourceType(r.type);
        this.colony.resources.push(resource);
      });
    });
  }

  closeButtonClick() {
    this.closeButton.emit();
  }

  changeBuildingOrderClicked() {
    this.selectingBuildingOrder = true;
  }

  cancelChangeBuildingOrderClicked() {
    this.selectingBuildingOrder = false;
  }

  get availableBuildingTypes(): ColonyBuildingType[] {
    return this.store.colonyBuildingTypes;
  }
}
