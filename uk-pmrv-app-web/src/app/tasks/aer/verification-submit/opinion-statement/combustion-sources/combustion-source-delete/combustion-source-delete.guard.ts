import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class CombustionSourceDeleteGuard {
  constructor(
    private readonly aerService: AerService,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const combustionSources$ = (
      this.aerService.getPayload() as Observable<AerApplicationVerificationSubmitRequestTaskPayload>
    ).pipe(map((payload) => payload.verificationReport.opinionStatement.combustionSources));

    return combustionSources$.pipe(
      first(),
      map((combustionSources) => {
        const combustionSource = route.paramMap.get('combustionSource');
        const combustionSourceFound = combustionSources?.find((item) => item === combustionSource);
        return (
          !!combustionSourceFound ||
          this.router.parseUrl(
            `tasks/${route.paramMap.get('taskId')}/aer/verification-submit/opinion-statement/combustion-sources`,
          )
        );
      }),
    );
  }
}
