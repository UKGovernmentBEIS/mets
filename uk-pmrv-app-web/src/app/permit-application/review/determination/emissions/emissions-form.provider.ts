import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { DateInputValidators } from '../../../../../../projects/govuk-components/src/lib/date-input/date-input.validators';
import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';

export const emissionsFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore],
  useFactory: (fb: UntypedFormBuilder, store: PermitApplicationStore<PermitApplicationState>) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    const value = state.determination;
    const originalValue = (state as any)?.originalPermitContainer;

    const annualEmissionsTargets = value?.annualEmissionsTargets
      ? Object.keys(value.annualEmissionsTargets).map((key) =>
          createAnotherEmissionsTarget(key, value.annualEmissionsTargets[key], disabled),
        )
      : originalValue?.annualEmissionsTargets
        ? Object.keys(originalValue.annualEmissionsTargets).map((key) =>
            createAnotherEmissionsTarget(key, originalValue.annualEmissionsTargets[key], disabled),
          )
        : null;

    return fb.group({
      annualEmissionsTargets: fb.array(annualEmissionsTargets ?? [createAnotherEmissionsTarget(null, null, disabled)]),
    });
  },
};

export function createAnotherEmissionsTarget(year?, emissions?, disabled = false): UntypedFormGroup {
  return new UntypedFormGroup({
    year: new UntypedFormControl({ value: year ?? null, disabled }, [
      GovukValidators.required('Enter a year value'),
      GovukValidators.pattern('[0-9]*', 'Enter a valid year value e.g. 2022'),
      GovukValidators.builder(
        `Enter a valid year value e.g. 2022`,
        DateInputValidators.dateFieldValidator('year', 1900, 2100),
      ),
    ]),
    emissions: new UntypedFormControl({ value: emissions ?? null, disabled }, [
      GovukValidators.required('Enter tonnes CO2e'),
      GovukValidators.notNaN('Enter a valid tonnes CO2e value'),
      GovukValidators.pattern('[0-9]*\\.?[0-9]{1}', 'Enter 1 decimal place only'),
      GovukValidators.max(99999999.9, 'Enter up to 8 digits'),
      GovukValidators.min(0.1, 'Enter a valid tonnes CO2e value'),
    ]),
  });
}
