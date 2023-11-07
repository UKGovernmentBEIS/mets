import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { TimeAllocationFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/time-allocation/time-allocation-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

export const canActivateTimeAllocation: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<TimeAllocationFormProvider>(TASK_FORM_PROVIDER);

  return store.pipe(
    aerVerifyCorsiaQuery.selectTimeAllocationScope,
    take(1),
    tap((timeAllocationScope) => {
      if (timeAllocationScope) {
        formProvider.setFormValue(timeAllocationScope);
      }
    }),
    map(() => true),
  );
};

export const canDeactivateTimeAllocation: CanActivateFn = () => {
  inject<TimeAllocationFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
