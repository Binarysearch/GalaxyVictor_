
export interface ColonyBuildingTypeDTO {
  id: string;
  name: string;
  buildable: boolean;
  resources: {type: string, quantity: number}[];
}
