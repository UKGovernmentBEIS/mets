import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router } from '@angular/router';

import { combineLatest, map, switchMap } from 'rxjs';

import { getTaskStatusByTaskCompletionState } from '@aviation/request-task/aer/shared/util/aer.util';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { notFoundVerificationBodyError } from '@tasks/aer/error/business-errors';

import { AccountVerificationBodyService, AviationAerCorsiaApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { requestTaskQuery, RequestTaskStore } from '../../../../store';
import { aerQuery } from '../../../shared/aer.selectors';

/**
 * Redirects to 'task list' if not all sections have been completed
 * Redirects to 'aer-corsia/send-report/verifier' if 'verificationPerformed == false', otherwise to throws VerificationBody Error Page
 */
export const canActivateSendReportVerifier = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const router = inject(Router);
  const accountVerificationBodyService = inject(AccountVerificationBodyService);
  const businessErrorService = inject(BusinessErrorService);

  const statusOkAsVerifier$ = store.pipe(
    aerQuery.selectPayload,
    map(
      (payload: AviationAerCorsiaApplicationSubmitRequestTaskPayload) =>
        getTaskStatusByTaskCompletionState('sendReport', payload, false, true) !== 'cannot start yet' &&
        !payload?.verificationPerformed,
    ),
  );

  const verifierBodyExists$ = store.pipe(
    requestTaskQuery.selectRequestInfo,
    switchMap((info) => accountVerificationBodyService.getVerificationBodyOfAccount(info.accountId)),
    map((vb) => !!vb),
  );

  return combineLatest([statusOkAsVerifier$, verifierBodyExists$]).pipe(
    map(([statusOkAsVerifier, verifierBodyExists]) => {
      return statusOkAsVerifier
        ? verifierBodyExists
          ? true
          : businessErrorService.showError(notFoundVerificationBodyError())
        : router.parseUrl(`aviation/tasks/${route.params.taskId}`);
    }),
  );
};

/**
 * Redirects to 'task list' if not all sections have been completed and if 'verificationPerformed == true'
 */
export const canActivateSendReportRegulator: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const router = inject(Router);

  const statusOkForRegulator$ = store.pipe(
    aerQuery.selectPayload,
    map(
      (payload: AviationAerCorsiaApplicationSubmitRequestTaskPayload) =>
        getTaskStatusByTaskCompletionState('sendReport', payload, false, true) !== 'cannot start yet' &&
        (payload?.verificationPerformed || !payload?.reportingRequired),
    ),
  );

  return combineLatest([statusOkForRegulator$]).pipe(
    map(([statusOkForRegulator]) => {
      return statusOkForRegulator ? true : router.parseUrl(`aviation/tasks/${route.params.taskId}`);
    }),
  );
};
