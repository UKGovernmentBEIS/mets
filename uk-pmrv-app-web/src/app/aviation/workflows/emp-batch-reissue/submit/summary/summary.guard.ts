import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { EmpBatchReissueStore } from '@aviation/workflows/emp-batch-reissue/submit/store/emp-batch-reissue.store';

import { isWizardCompleted } from '../submit.wizard';

@Injectable({
  providedIn: 'root',
})
export class SummaryGuard {
  constructor(
    private router: Router,
    private readonly store: EmpBatchReissueStore,
  ) {}

  canActivate(_route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    const firstWizardStep = `${state.url.slice(0, state.url.lastIndexOf('/'))}/filters`;
    return this.store.pipe(map((storeState) => isWizardCompleted(storeState) || this.router.parseUrl(firstWizardStep)));
  }
}
