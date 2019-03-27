
export interface ColonyBuildingTypeDTO {
  id: string;
  name: string;
  buildable: boolean;
  resources: {type: string, quantity: number}[];
  costs: {type: string, quantity: number}[];
  capabilities: {type: string}[];
}
