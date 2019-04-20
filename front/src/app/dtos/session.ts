import { UserCivilizationDTO } from './user-civilization';
import { GalaxyDTO } from './galaxy';

export interface SessionDTO {
  token: string;
  serverTime: number;
  user: {
    id: number,
    email: String,
    currentGalaxy?: GalaxyDTO
    currentCivilization?: UserCivilizationDTO
  };
}
