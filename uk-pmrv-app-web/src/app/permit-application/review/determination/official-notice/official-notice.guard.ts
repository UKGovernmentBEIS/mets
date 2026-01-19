import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { combineLatest, map, Observable, switchMap } from 'rxjs';

import { InstallationAccountViewService } from 'pmrv-api';

import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';

@Injectable()
export class OfficialNoticeGuard {
  constructor(
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
    private readonly installationAccountViewService: InstallationAccountViewService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((state) => state.accountId),
        switchMap((accountId) =>
          combineLatest([this.store, this.installationAccountViewService.getInstallationAccountById(accountId)]),
        ),
        map(([storeState, result]) => {
          const wizardUrl = `/${this.store.urlRequestType}/${route.paramMap.get('taskId')}/review/determination`;

          return (
            (storeState.reviewSectionsCompleted?.[route.data.statusKey] &&
              this.router.parseUrl(wizardUrl.concat('/summary'))) ||
            (this.store.isDeterminationWizardComplete(result?.accountPermitDto?.account.emissionTradingScheme) &&
              this.router.parseUrl(wizardUrl.concat('/answers'))) ||
            (((this.store.isDeterminationTypeApplicable() && storeState.determination?.type !== 'REJECTED') ||
              !storeState.determination?.reason) &&
              this.router.parseUrl(wizardUrl)) ||
            true
          );
        }),
      )
    );
  }
}
