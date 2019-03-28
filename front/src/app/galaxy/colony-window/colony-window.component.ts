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
      this.colony.canBuildShips = false;

      buildings.forEach(b => {
        const building = new ColonyBuilding(b);
        building.type = this.store.getColonyBuildingType(b.type);
        this.colony.buildings.push(building);

        if (!this.colony.canBuildShips) {
          building.type.capabilities.forEach(e => {
            if (e.type.id === 'build ships') {
              this.colony.canBuildShips = true;
            }
          });
        }
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
      this._availableBuildingTypes = null;
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
    if (this.colony.resources && !this._availableBuildingTypes) {

      this._availableBuildingTypes = [];

      // maps resource -> quantity
      const resourceMap = new Map<string, number>();
      this.colony.resources.forEach(r => {
        resourceMap.set(r.type.id, r.quantity);
      });

      // maps built colony buildings -> quantity
      const builtMap = new Map<string, number>();
      if (this.colony.buildings) {
        this.colony.buildings.forEach(b => {
          if (builtMap.has(b.type.id)) {
            builtMap.set(b.type.id, 1 + builtMap.get(b.type.id));
          } else {
            builtMap.set(b.type.id, 1);
          }
        });
      }

      // check for every building type that there are enought of every resource, is buildable and
      // if it is not repeatable and it is built then it is not available
      this.store.colonyBuildingTypes.forEach(bt => {
        if (bt.buildable && (!builtMap.has(bt.id) || bt.repeatable)) {
          let available = true;
          bt.resources.forEach(r => {
            const availableQuantity = resourceMap.get(r.resourceType.id);
            available = available && (availableQuantity + r.quantity >= 0);
          });
          if (available) {
            this._availableBuildingTypes.push(bt);
          }
        }
      });
    }
    return this._availableBuildingTypes;
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
