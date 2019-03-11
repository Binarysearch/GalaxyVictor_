import { CivilizationsService } from './civilizations.service';
import { GalaxiesService } from './galaxies.service';
import { Injectable } from '@angular/core';
import { AuthService } from './auth.service';
import { GalaxyDTO } from '../dtos/galaxy';
import { Observable, of } from 'rxjs';
import { StarSystemsService } from './star-systems.service';
import { SessionDTO } from '../dtos/session';
import { StarSystemDTO } from '../dtos/star-system';
import { UserCivilizationDTO } from '../dtos/user-civilization';
import { PlanetsService } from './planets.service';
import { Store } from '../store';

@Injectable({
  providedIn: 'root'
})
export class CoreService {

  constructor(
    private authService: AuthService,
    private starSystemsService: StarSystemsService,
    private store: Store,
    private civilizationsService: CivilizationsService,
    private galaxiesService: GalaxiesService
  ) { }

  public get isAuthenticated(): boolean {
    return this.store.session != null;
  }

  public logout() {
    this.authService.logout();
  }

  public getGalaxies(): Observable<GalaxyDTO[]> {
    return this.galaxiesService.getGalaxies();
  }

  public selectGalaxy(id: number): Observable<GalaxyDTO> {
    return this.galaxiesService.selectGalaxy(id);
  }

  public login(email: string, password: string): Observable<SessionDTO> {
    return this.authService.login(email, password);
  }

  public register(email: string, password: string): Observable<SessionDTO> {
    return this.authService.register(email, password);
  }

  public getStarSystem(id: number): Observable<StarSystemDTO> {
    return this.starSystemsService.getStarSystem(id);
  }

  public createCivilization(civilizationName: string, homeStarName: string): Observable<UserCivilizationDTO> {
    return this.civilizationsService.createCivilization(this.store.galaxy.id, civilizationName, homeStarName);
  }

  public get hasCivilization(): boolean {
    return this.civilizationsService.hasCivilization;
  }

  getCurrentCivilization(): Observable<UserCivilizationDTO> {
    return this.civilizationsService.getCurrentCivilization();
  }

  public get currentCivilization(): UserCivilizationDTO {
    return this.civilizationsService.currentCivilization;
  }
}
