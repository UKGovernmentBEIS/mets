import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { combineLatest, map, Observable, switchMap } from 'rxjs';

import { InstallationAccountViewService } from 'pmrv-api';

import { isHSEAnnualEmissionTargetsCompleted } from '../../../../permit-application/review/determination/determination-wizard';
import { PermitVariationStore } from '../../../store/permit-variation.store';

@Injectable({ providedIn: 'root' })
export class ReasonTemplateGuard {
  constructor(
    private readonly store: PermitVariationStore,
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
            ((!storeState.determination?.reason ||
              !storeState.determination?.activationDate ||
              (storeState.permitType === 'HSE' && !isHSEAnnualEmissionTargetsCompleted(storeState))) &&
              this.router.parseUrl(wizardUrl)) ||
            true
          );
        }),
      )
    );
  }
}
