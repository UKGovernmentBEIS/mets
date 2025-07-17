import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router } from '@angular/router';

import { combineLatest, map, of, switchMap } from 'rxjs';

import { getTaskStatusByTaskCompletionState } from '@aviation/request-task/aer/shared/util/aer.util';
import { amendsRequestedTaskActionTypes } from '@aviation/request-task/util';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { notFoundVerificationBodyError } from '@tasks/aer/error/business-errors';

import { AccountVerificationBodyService } from 'pmrv-api';

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

  const verifierBodyExists$ = store.pipe(
    requestTaskQuery.selectRequestInfo,
    switchMap((info) => accountVerificationBodyService.getVerificationBodyOfAccount(info.accountId)),
    map((vb) => !!vb),
  );

  return combineLatest([
    store.pipe(aerQuery.selectPayload),
    store.pipe(requestTaskQuery.selectRequestTaskType),
    verifierBodyExists$,
  ]).pipe(
    switchMap(([payload, taskType, verifierBodyExists]) => {
      return getTaskStatusByTaskCompletionState(
        'sendReport',
        payload,
        amendsRequestedTaskActionTypes.includes(taskType),
        true,
      ) !== 'cannot start yet' && !payload?.verificationPerformed
        ? verifierBodyExists
          ? of(true)
          : businessErrorService.showError(notFoundVerificationBodyError())
        : of(router.parseUrl(`aviation/tasks/${route.params.taskId}`));
    }),
  );
};

/**
 * Redirects to 'task list' if not all sections have been completed and if 'verificationPerformed == true'
 */
export const canActivateSendReportRegulator: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const router = inject(Router);

  return combineLatest([store.pipe(aerQuery.selectPayload), store.pipe(requestTaskQuery.selectRequestTaskType)]).pipe(
    map(([payload, taskType]) => {
      return getTaskStatusByTaskCompletionState(
        'sendReport',
        payload,
        amendsRequestedTaskActionTypes.includes(taskType),
        true,
      ) !== 'cannot start yet' &&
        (payload?.verificationPerformed || !payload?.reportingRequired)
        ? true
        : router.parseUrl(`aviation/tasks/${route.params.taskId}`);
    }),
  );
};
