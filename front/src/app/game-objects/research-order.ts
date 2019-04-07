import { Technology } from './technology';
import { ResearchOrderDTO } from '../dtos/research-order';

export class ResearchOrder {
  technologyId: string;
  starSystem: number;
  startedTime: number;
  finishTime: number;
  technology: Technology;
  name: string;

  constructor(data: ResearchOrderDTO) {
    this.technologyId = data.technology;
    this.starSystem = data.starSystem;
    this.startedTime = data.startedTime;
    this.finishTime = data.finishTime;
  }
}
