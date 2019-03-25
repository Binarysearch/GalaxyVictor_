import { ResourceTypeDTO } from '../dtos/resource-type';

export class ResourceType {
  id: string;
  name: string;

  constructor(data: ResourceTypeDTO) {
    this.id = data.id;
    this.name = data.name;
  }
}
