import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { combineLatest, first, map, Observable } from 'rxjs';

import { PermitSurrenderReviewDeterminationGrant } from 'pmrv-api';

import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';
import { isWizardComplete } from '../wizard';

@Injectable({
  providedIn: 'root',
})
export class AnswersGuard {
  constructor(
    private readonly store: PermitSurrenderStore,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      combineLatest([this.store.pipe(), this.store.isFinalAlrVisible$]).pipe(
        first(),
        map(([storeState, isFinalAlrVisible]) => {
          const taskId = route.paramMap.get('taskId');
          const wizardBaseUrl = `/permit-surrender/${taskId}/review/determination`;
          const wizardBaseGrantUrl = `${wizardBaseUrl}/grant`;
          const determination = storeState.reviewDetermination as PermitSurrenderReviewDeterminationGrant;

          return (
            (storeState.reviewDeterminationCompleted && this.router.parseUrl(wizardBaseGrantUrl.concat('/summary'))) ||
            (determination?.type !== 'GRANTED' && this.router.parseUrl(wizardBaseUrl)) ||
            (!isWizardComplete(determination, isFinalAlrVisible) && this.router.parseUrl(wizardBaseGrantUrl)) ||
            true
          );
        }),
      )
    );
  }
}
