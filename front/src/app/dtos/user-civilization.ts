import { PlanetDTO } from './planet';


export interface UserCivilizationDTO {
  id: number;
  name: string;
  serverTime: number;
  homeworld: PlanetDTO;
  planetsCache: number;
  tradeRoutesCache: number;
  researchOrdersCache: number;
  shipModelsCache: number;
  civilizationsCache: number;
}
