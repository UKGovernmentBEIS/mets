import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload, UncorrectedNonConformities } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class ListGuard {
  constructor(
    private readonly aerService: AerService,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.aerService.getPayload().pipe(
      first(),
      map((payload) => {
        const uncorrectedNonConformities = (payload as AerApplicationVerificationSubmitRequestTaskPayload)
          .verificationReport.uncorrectedNonConformities as UncorrectedNonConformities;
        return (
          (uncorrectedNonConformities?.areThereUncorrectedNonConformities &&
            uncorrectedNonConformities?.uncorrectedNonConformities?.length > 0) ||
          this.router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/aer/verification-submit/non-conformities`)
        );
      }),
    );
  }
}
