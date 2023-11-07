import { AsyncValidatorFn, FormBuilder, ValidationErrors } from '@angular/forms';

import { first, map, Observable } from 'rxjs';

import { ReturnOfAllowancesApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../../store/common-tasks.store';
import { RETURN_OF_ALLOWANCES_TASK_FORM } from '../../core/return-of-allowances-task-form.token';
import { isDateToBeReturnedValid } from '../../core/section-status';

export const summaryFormFactory = {
  provide: RETURN_OF_ALLOWANCES_TASK_FORM,
  deps: [FormBuilder, CommonTasksStore],
  useFactory: (fb: FormBuilder, store: CommonTasksStore) =>
    fb.group(
      {},
      {
        asyncValidators: [dateToBeReturnedAsyncValidator(store)],
      },
    ),
};

function dateToBeReturnedAsyncValidator(store: CommonTasksStore): AsyncValidatorFn {
  return (): Observable<ValidationErrors | null> =>
    store.pipe(
      first(),
      map((store) =>
        isDateToBeReturnedValid(
          store.requestTaskItem.requestTask.payload as ReturnOfAllowancesApplicationSubmitRequestTaskPayload,
        )
          ? null
          : { invalidDueDate: 'The date must be in the future' },
      ),
    );
}
