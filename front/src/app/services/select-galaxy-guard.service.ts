import { Store } from './../store';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot, CanActivate, Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class SelectGalaxyGuard implements CanActivate {

  constructor(private store: Store, private router: Router) { }

  canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (this.store.session) {
      if (this.store.galaxy) {
        return true;
      }
      this.router.navigate(['/galaxies']);
    }

    return false;
  }

}
