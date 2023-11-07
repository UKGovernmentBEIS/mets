import {
  AbstractControl,
  AsyncValidatorFn,
  FormGroup,
  UntypedFormBuilder,
  ValidationErrors,
  ValidatorFn,
} from '@angular/forms';

import { first, map, Observable } from 'rxjs';

import { formGroupOptions } from '@shared/components/regulated-activities/regulated-activities-form-options';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import {
  duplicatedActivitySelected,
  noRegulatedActivitySelected,
} from '@tasks/aer/verification-submit/opinion-statement/errors/opinion-statement-validation.errors';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export const regulatedActivityAddFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getState();
    const group = fb.group(
      {
        activityCategory: [null],
        activity: [null],
      },
      {
        updateOn: 'change',
        validators: [atLeastOneRequiredValidator(noRegulatedActivitySelected)],
        asyncValidators: [duplicatedActivity(store)],
      },
    );
    if (!state.isEditable) {
      group.disable();
    }
    return group;
  },
};

function atLeastOneRequiredValidator(message: string): ValidatorFn {
  return GovukValidators.builder(message, (group: FormGroup) => {
    const activityCategoryValue = group.get('activityCategory')?.value;
    const activityValue = group.get('activity')?.value;
    return activityValue ||
      Object.values(formGroupOptions)
        .reduce((acc, value) => acc.concat(value), [])
        .includes(activityCategoryValue)
      ? null
      : { atLeastOneRequired: true };
  });
}

function duplicatedActivity(store: CommonTasksStore): AsyncValidatorFn {
  return (control: AbstractControl): Observable<ValidationErrors | null> => {
    const value = control.get('activity').value || control.get('activityCategory').value;
    return store.pipe(
      first(),
      map((state) => {
        const regulatedActivities = (
          state.requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload
        )?.verificationReport?.opinionStatement?.regulatedActivities;
        return !regulatedActivities ||
          !regulatedActivities.some((regulatedActivityType) => regulatedActivityType === value)
          ? null
          : { duplicatedActivity: duplicatedActivitySelected };
      }),
    );
  };
}
