import { ColonyBuildingType } from './../../../game-objects/colony-building-type';
import { ColonyResource } from './../../../game-objects/colony-resource';
import { Colony } from './../../../game-objects/colony';
import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { TextService } from 'src/app/services/text.service';
import { Store } from './../../../store';
import { ColonyBuilding } from './../../../game-objects/colony-building';
import { CoreService } from '../../../services/core.service';
import { ShipModel } from 'src/app/game-objects/ship-model';

@Component({
  selector: 'app-colony-details',
  templateUrl: './colony-details.component.html',
  styleUrls: ['./colony-details.component.css']
})
export class ColonyDetailsComponent implements OnInit {

  @Input() colony: Colony;
  @Output() closeButton = new EventEmitter();

  selectingBuildingOrder: boolean;
  private _availableBuildingTypes: ColonyBuildingType[];

  constructor(public ts: TextService, private core: CoreService, private store: Store) { }

  ngOnInit() {  }

  get title(): string {
    return `${this.ts.strings.colony} en ${this.colony.planet.name}`;
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
