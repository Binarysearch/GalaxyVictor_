import { TechnologyDTO } from '../dtos/technology';

export class Technology {
  id: string;
  level: number;
  name: string;
  prerequisites: Technology[] = [];

  constructor(data: TechnologyDTO) {
    this.id = data.id;
    this.level = data.level;
    this.name = data.name;
  }
}
