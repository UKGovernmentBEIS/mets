import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { getTaskStatusByTaskCompletionState } from '@aviation/request-task/aer/shared/util/aer.util';
import { RequestTaskStore } from '@aviation/request-task/store';

import { AviationAerUkEtsApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class SendReportGuard implements CanActivate {
  constructor(private readonly store: RequestTaskStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      aerQuery.selectPayload,
      map((payload) => payload as AviationAerUkEtsApplicationSubmitRequestTaskPayload),
      map((payload: AviationAerUkEtsApplicationSubmitRequestTaskPayload) => {
        const verificationPerformed = payload.verificationPerformed;
        const reportingObligation = payload.reportingRequired;
        const monitoringApproach = payload?.aer?.monitoringApproach?.monitoringApproachType;
        const reductionClaimExists = payload?.aer?.saf?.exist;

        const needsVerification =
          reductionClaimExists ||
          monitoringApproach === 'FUEL_USE_MONITORING' ||
          monitoringApproach === 'EUROCONTROL_SMALL_EMITTERS';
        const isAmends = payload.payloadType === 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT_PAYLOAD';
        const statusOk = getTaskStatusByTaskCompletionState('sendReport', payload, isAmends) !== 'cannot start yet';
        return needsVerification && !verificationPerformed && statusOk
          ? this.router.parseUrl(`aviation/tasks/${route.paramMap.get('taskId')}/aer/send-report/verification`)
          : (needsVerification && verificationPerformed) || !reportingObligation
          ? this.router.parseUrl(`aviation/tasks/${route.paramMap.get('taskId')}/aer/send-report/regulator`)
          : true;
      }),
    );
  }
}
