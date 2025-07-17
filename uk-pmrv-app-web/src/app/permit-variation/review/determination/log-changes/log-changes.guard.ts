import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { isHSEAnnualEmissionTargetsCompleted } from '../../../../permit-application/review/determination/determination-wizard';
import { PermitVariationStore } from '../../../store/permit-variation.store';
import { isVariationReasonTemplateCompleted } from '../../review';

@Injectable({ providedIn: 'root' })
export class LogChangesGuard {
  constructor(
    private readonly store: PermitVariationStore,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((storeState) => {
          const wizardUrl = `/${this.store.urlRequestType}/${route.paramMap.get('taskId')}/review/determination`;

          return (
            (storeState.reviewSectionsCompleted?.[route.data.statusKey] &&
              this.router.parseUrl(wizardUrl.concat('/summary'))) ||
            (this.store.isDeterminationWizardComplete() && this.router.parseUrl(wizardUrl.concat('/answers'))) ||
            (((this.store.isDeterminationTypeApplicable() && storeState.determination?.type !== 'GRANTED') ||
              !storeState.determination?.reason ||
              !storeState.determination?.activationDate ||
              (storeState.permitType === 'HSE' && !isHSEAnnualEmissionTargetsCompleted(storeState)) ||
              (storeState.requestTaskType === 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT' &&
                !isVariationReasonTemplateCompleted(storeState?.determination))) &&
              this.router.parseUrl(wizardUrl)) ||
            true
          );
        }),
      )
    );
  }
}
