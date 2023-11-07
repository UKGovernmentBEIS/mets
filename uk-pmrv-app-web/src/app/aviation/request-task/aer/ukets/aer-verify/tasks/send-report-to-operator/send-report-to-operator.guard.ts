import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';

import { map } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';

import { aerVerifyQuery } from '../../aer-verify.selector';
import { getTaskStatusByTaskCompletionState } from '../../util/aer-verify.util';

export const canActivateSendReportToOperator: CanActivateFn = () => {
  return inject(RequestTaskStore)
    .pipe(aerVerifyQuery.selectPayload)
    .pipe(map((payload) => getTaskStatusByTaskCompletionState('sendReport', payload) !== 'cannot start yet'));
};
