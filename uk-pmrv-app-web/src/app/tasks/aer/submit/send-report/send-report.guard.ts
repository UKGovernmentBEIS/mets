import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';
import { sendReportStatus } from '@tasks/aer/submit/send-report/send-report-status';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class SendReportGuard implements CanActivate {
  constructor(private readonly aerService: AerService, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.aerService.getPayload().pipe(
      map((payload) => payload as AerApplicationSubmitRequestTaskPayload),
      map((payload) => {
        const verificationPerformed = payload.verificationPerformed;
        const permitType = payload.permitType;

        return permitType === 'GHGE' && !verificationPerformed && sendReportStatus(payload) !== 'cannot start yet'
          ? this.router.parseUrl(`tasks/${route.paramMap.get('taskId')}/aer/submit/send-report/verification`)
          : permitType === 'GHGE' && verificationPerformed
          ? this.router.parseUrl(`tasks/${route.paramMap.get('taskId')}/aer/submit/send-report/regulator`)
          : true;
      }),
    );
  }
}
