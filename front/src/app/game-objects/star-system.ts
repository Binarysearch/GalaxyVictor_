import { Fleet } from './fleet';
import { StarSystemDTO } from './../dtos/star-system';
import { GameObject } from './game-object';
import { Planet } from './planet';
import { ResearchOrder } from './research-order';
import { Technology } from './technology';
import { StarSystemsService } from '../services/star-systems.service';

export class StarSystem implements GameObject {
  id: number;
  name?: string;
  x: number;
  y: number;
  type: number;
  size: number;
  planets: Planet[] = [];
  fleets: Fleet[] = [];

  private _technologies: Technology[] = [];
  private invalidatedTechnologies = true;

  private _availableTechnologies: Technology[] = [];
  private invalidatedAvailableTechnologies = true;

  researchOrder: ResearchOrder;

  constructor (data: StarSystemDTO, private starSystemService: StarSystemsService) {
    this.id = data.id;
    this.name = data.name;
    this.x = data.x;
    this.y = data.y;
    this.type = data.type;
    this.size = data.size;
  }

  get objectType() { return 'StarSystem'; }

  get availableTechnologies(): Technology[] {
    if (this.invalidatedAvailableTechnologies) {
      this.starSystemService.loadAvailableTechnologies(this);
      this.invalidatedAvailableTechnologies = false;
    }
    return this._availableTechnologies;
  }

  set availableTechnologies(availableTechnologies: Technology[]) {
    this._availableTechnologies = availableTechnologies;
  }

  get technologies(): Technology[] {
    if (this.invalidatedTechnologies) {
      this.starSystemService.loadStarSystemTechnologies(this);
      this.invalidatedTechnologies = false;
    }
    return this._technologies;
  }

  set technologies(technologies: Technology[]) {
    this._technologies = technologies;
  }

  invalidateAvailableTechnologies() {
    this.invalidatedAvailableTechnologies = true;
  }

  invalidateTechnologies() {
    this.invalidatedTechnologies = true;
  }

  clear() {
    this.fleets = [];
    this.planets = [];
    this._technologies = [];
    this._availableTechnologies = [];
    this.researchOrder = null;
    this.invalidatedAvailableTechnologies = true;
    this.invalidatedTechnologies = true;
  }
}
