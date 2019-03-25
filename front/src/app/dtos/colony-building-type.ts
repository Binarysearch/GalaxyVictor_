
export interface ColonyBuildingTypeDTO {
  id: string;
  name: string;
  resources: {type: string, quantity: number}[];
}
