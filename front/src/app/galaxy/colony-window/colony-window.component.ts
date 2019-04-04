import { ColonyBuildingType } from './../../game-objects/colony-building-type';
import { ColonyResource } from './../../game-objects/colony-resource';
import { Colony } from './../../game-objects/colony';
import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { TextService } from 'src/app/services/text.service';
import { Store } from './../../store';
import { ColonyBuilding } from './../../game-objects/colony-building';
import { CoreService } from '../../services/core.service';
import { ShipModel } from 'src/app/game-objects/ship-model';

@Component({
  selector: 'app-colony-window',
  templateUrl: './colony-window.component.html',
  styleUrls: ['./colony-window.component.css']
})
export class ColonyWindowComponent implements OnInit {

  @Input() colony: Colony;
  @Output() closeButton = new EventEmitter();

  selectingBuildingOrder: boolean;
  private _availableBuildingTypes: ColonyBuildingType[];

  constructor(public ts: TextService, private core: CoreService, private store: Store) { }

  ngOnInit() {
    if (!this.colony.planet.starSystem.technologies) {
      this.core.loadStarSystemTechnologies(this.colony.planet.starSystem.id);
    }
  }

  get title(): string {
    return this.ts.strings.colony + ' ' + this.colony.id;
  }

  get buildings(): ColonyBuilding[] {
    if (!this.colony.buildings) {
      this.core.loadColonyBuildings(this.colony.id);
    }
    return this.colony.buildings;
  }

  get resources(): ColonyResource[] {
    if (!this.colony.resources) {
      this.core.loadColonyResources(this.colony.id);
    }
    return this.colony.resources;
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
    if (!this.colony.availableBuildingTypes) {
      this.core.loadColonyAvailableBuildingTypes(this.colony.id);
    }
    return this.colony.availableBuildingTypes;
  }

  get availableShipModels(): ShipModel[] {
    if (!this.colony.canBuildShips) {
      return [];
    }
    return this.store.shipModels;
  }

  changeBuildingOrder(buildingTypeId: string) {
    this.selectingBuildingOrder = false;
    this.core.changeColonyBuildingOrder(this.colony.id, buildingTypeId);
  }

  changeShipOrder(shipModelId: number) {
    this.selectingBuildingOrder = false;
    this.core.changeShipOrder(this.colony.id, shipModelId);
  }

}
