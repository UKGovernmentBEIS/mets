import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { permitRevocationMapper } from '@permit-revocation/constants/permit-revocation-consts';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';

import { Wizard } from '../../factory';

@Injectable({ providedIn: 'root' })
export class WizardStepsGuard {
  constructor(
    private readonly store: PermitRevocationStore,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const key = route.data?.keys[0];
    const step = permitRevocationMapper[key]?.step;

    let previousStep = step - 1;

    const isChangePermitted = this.router.getCurrentNavigation().extras?.state?.changing;
    return (
      isChangePermitted ||
      combineLatest([this.store, this.store.isFinalAlrVisible$]).pipe(
        map(([store, isFinalAlrVisible]) => {
          const sectionStatusCompleted = store.sectionsCompleted?.[route.data.statusKey];

          if (!isFinalAlrVisible && step === 6) {
            previousStep = 4;
          }

          return (
            (sectionStatusCompleted &&
              this.router.parseUrl(`/permit-revocation/${route.params['taskId']}/apply/summary`)) ||
            (Wizard.completed(store.permitRevocation, isFinalAlrVisible) &&
              this.router.parseUrl(`/permit-revocation/${route.params['taskId']}/apply/answers`)) ||
            (!Wizard.stepStatus(previousStep, store.permitRevocation) &&
              this.router.parseUrl(`/permit-revocation/${route.params['taskId']}/apply/reason`)) ||
            true
          );
        }),
      )
    );
  }
}
