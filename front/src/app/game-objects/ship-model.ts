import { ShipModelDTO } from './../dtos/ship-model';

export class ShipModel {
  id: number;
  name: string;
  canColonize: boolean;
  canFight: boolean;

  constructor(data: ShipModelDTO) {
    this.id = data.id;
    this.name = data.name;
    this.canColonize = data.canColonize;
    this.canFight = data.canFight;
  }
}
