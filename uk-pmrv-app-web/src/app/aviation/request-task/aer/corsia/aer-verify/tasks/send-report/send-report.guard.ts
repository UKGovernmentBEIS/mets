import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';

import { map } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { getTaskStatusByTaskCompletionState } from '@aviation/request-task/aer/corsia/aer-verify/util/aer-verify-corsia.util';
import { RequestTaskStore } from '@aviation/request-task/store';

export const canActivateSendReport: CanActivateFn = () => {
  return inject(RequestTaskStore)
    .pipe(aerVerifyCorsiaQuery.selectPayload)
    .pipe(map((payload) => getTaskStatusByTaskCompletionState('sendReport', payload) !== 'cannot start yet'));
};
