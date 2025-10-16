import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { Wizard } from '@permit-revocation/factory';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';

@Injectable({ providedIn: 'root' })
export class ConfirmSubmitGuard {
  constructor(
    private readonly store: PermitRevocationStore,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return combineLatest([this.store, this.store.isFinalAlrVisible$]).pipe(
      map(([store, isFinalAlrVisible]) => {
        const sectionStatusCompleted = store.sectionsCompleted?.[route.data.statusKey];
        return (
          (sectionStatusCompleted &&
            this.router.parseUrl(`/permit-revocation/${route.params['taskId']}/apply/summary`)) ||
          (!Wizard.completed(store.permitRevocation, isFinalAlrVisible) &&
            this.router.parseUrl(`/permit-revocation/${route.params['taskId']}`)) ||
          Wizard.completed(store.permitRevocation, isFinalAlrVisible)
        );
      }),
    );
  }
}
