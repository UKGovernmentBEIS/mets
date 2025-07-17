import {
  AbstractControl,
  AsyncValidatorFn,
  FormGroup,
  UntypedFormBuilder,
  UntypedFormGroup,
  ValidationErrors,
  ValidatorFn,
} from '@angular/forms';

import { first, map, Observable } from 'rxjs';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const naceCodeInstallationActivityFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) =>
    fb.group(
      {
        installationActivity: [null],
        installationActivityChild0: [null],
        installationActivityChild1: [null],
        installationActivityChild2: [null],
        installationActivityChild3: [null],
        installationActivityChild4: [null],
        installationActivityChild5: [null],
        installationActivityChild6: [null],
        installationActivityChild7: [null],
        installationActivityChild8: [null],
        installationActivityChild9: [null],
      },
      {
        updateOn: 'change',
        validators: [atLeastOneRequiredValidator('Select a category')],
        asyncValidators: [duplicateCode(store)],
      },
    ),
};

export function atLeastOneRequiredValidator(message: string): ValidatorFn {
  return GovukValidators.builder(message, (group: UntypedFormGroup) => {
    const childValue = findControlWithAnyValue(group) ? group.get(findControlWithAnyValue(group)).value : null;
    const activityValue = group.get('installationActivity').value;
    // activity value should start with a '_' to be a nace code. If it does not, it is a parent and a child has to be chosen.
    return childValue || (activityValue && activityValue.startsWith('_')) ? null : { atLeastOneRequired: true };
  });
}

export function findControlWithAnyValue(group: FormGroup): string | null {
  const keysWithValue = Object.keys(group.controls).filter(
    (key) =>
      group.get(key)?.value !== null && group.get(key)?.value !== undefined && group.get(key)?.value.startsWith('_'),
  );

  return keysWithValue.length === 1 ? keysWithValue[0] : null;
}

export function duplicateCode(store: CommonTasksStore): AsyncValidatorFn {
  return (control: AbstractControl): Observable<ValidationErrors | null> => {
    const value =
      control.get('installationActivityChild0').value ||
      control.get('installationActivityChild1').value ||
      control.get('installationActivityChild2').value ||
      control.get('installationActivityChild3').value ||
      control.get('installationActivityChild4').value ||
      control.get('installationActivityChild5').value ||
      control.get('installationActivityChild6').value ||
      control.get('installationActivityChild7').value ||
      control.get('installationActivityChild8').value ||
      control.get('installationActivityChild9').value ||
      control.get('installationActivity').value;
    return store.pipe(
      first(),
      map((state) =>
        !(
          state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload
        ).aer.naceCodes?.codes?.includes(value)
          ? null
          : { duplicateCode: 'You have already added this NACE code' },
      ),
    );
  };
}
