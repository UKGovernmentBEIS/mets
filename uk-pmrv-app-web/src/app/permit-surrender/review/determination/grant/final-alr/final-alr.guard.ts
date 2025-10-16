import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { combineLatest, first, map, Observable } from 'rxjs';

import { PermitSurrenderReviewDeterminationGrant } from 'pmrv-api';

import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';
import { isWizardComplete } from '../wizard';

@Injectable({
  providedIn: 'root',
})
export class FinalAlrGuard {
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
            (!isFinalAlrVisible && this.router.parseUrl(wizardBaseGrantUrl.concat('/allowances'))) ||
            (storeState.reviewDeterminationCompleted && this.router.parseUrl(wizardBaseGrantUrl.concat('/summary'))) ||
            (isWizardComplete(determination, isFinalAlrVisible) &&
              this.router.parseUrl(`${wizardBaseGrantUrl}/answers`)) ||
            ((determination?.type !== 'GRANTED' ||
              !determination?.stopDate ||
              !determination?.noticeDate ||
              determination.reportRequired === undefined) &&
              this.router.parseUrl(wizardBaseUrl)) ||
            true
          );
        }),
      )
    );
  }
}
