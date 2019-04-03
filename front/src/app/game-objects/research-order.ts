import { Technology } from './technology';
import { ResearchOrderDTO } from '../dtos/research-order';

export class ResearchOrder {
  technologyId: string;
  technology: Technology;
  name: string;

  constructor(data: ResearchOrderDTO) {
    this.technologyId = data.technology;
  }
}
