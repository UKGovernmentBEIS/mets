import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitBatchReissueStore } from '../store/permit-batch-reissue.store';
import { isFiltersWizardStepCompleted } from '../submit.wizard';

@Injectable({
  providedIn: 'root',
})
export class SignatoryGuard implements CanActivate {
  constructor(private router: Router, private readonly store: PermitBatchReissueStore) {}

  canActivate(_route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    const firstWizardStep = `${state.url.slice(0, state.url.lastIndexOf('/'))}/filters`;
    return this.store.pipe(
      map((storeState) => isFiltersWizardStepCompleted(storeState) || this.router.parseUrl(firstWizardStep)),
    );
  }
}
