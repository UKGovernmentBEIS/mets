import { AsyncValidatorFn, FormBuilder, ValidationErrors } from '@angular/forms';

import { first, map, Observable } from 'rxjs';

import { isFeeDueDateValid } from '@tasks/dre/core/section-status';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { DreApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { DRE_TASK_FORM } from '../../core/dre-task-form.token';

export const summaryFormFactory = {
  provide: DRE_TASK_FORM,
  deps: [FormBuilder, CommonTasksStore],
  useFactory: (fb: FormBuilder, store: CommonTasksStore) =>
    fb.group(
      {},
      {
        asyncValidators: [dueDateAsyncValidator(store)],
      },
    ),
};

function dueDateAsyncValidator(store: CommonTasksStore): AsyncValidatorFn {
  return (): Observable<ValidationErrors | null> =>
    store.pipe(
      first(),
      map((store) =>
        isFeeDueDateValid(store.requestTaskItem.requestTask.payload as DreApplicationSubmitRequestTaskPayload)
          ? null
          : { invalidDueDate: 'The date must be today or in the future' },
      ),
    );
}
