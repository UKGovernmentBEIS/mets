import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { EmpBatchReissueStore } from '@aviation/workflows/emp-batch-reissue/submit/store/emp-batch-reissue.store';
import { isFiltersWizardStepCompleted } from '@aviation/workflows/emp-batch-reissue/submit/submit.wizard';

@Injectable({
  providedIn: 'root',
})
export class SignatoryGuard {
  constructor(private router: Router, private readonly store: EmpBatchReissueStore) {}

  canActivate(_route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    const firstWizardStep = `${state.url.slice(0, state.url.lastIndexOf('/'))}/filters`;
    return this.store.pipe(
      map((storeState) => isFiltersWizardStepCompleted(storeState) || this.router.parseUrl(firstWizardStep)),
    );
  }
}
