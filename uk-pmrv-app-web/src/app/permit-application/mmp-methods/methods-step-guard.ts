import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { hasTwoOrMoreSubInstallationsCompleted, isMethodsWizardCompleted } from './mmp-methods';

@Injectable()
export class MMPMethodsStepGuard {
  constructor(
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const isSummaryPage = this.router.getCurrentNavigation().finalUrl.toString().split('/').at(-1) === 'summary';

    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((state) => {
          if (isSummaryPage) {
            return (
              isMethodsWizardCompleted(state) ||
              this.router.parseUrl(
                `/${this.store.urlRequestType}/${route.paramMap.get('taskId')}/mmp-methods${hasTwoOrMoreSubInstallationsCompleted(state) ? '' : '/avoid-double-count'}`,
              )
            );
          } else {
            return (
              !isMethodsWizardCompleted(state) ||
              this.router.parseUrl(`/${this.store.urlRequestType}/${route.paramMap.get('taskId')}/mmp-methods/summary`)
            );
          }
        }),
      )
    );
  }
}
