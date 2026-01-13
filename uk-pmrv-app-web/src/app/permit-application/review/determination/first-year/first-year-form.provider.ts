import { AbstractControl, UntypedFormBuilder, ValidationErrors, ValidatorFn } from '@angular/forms';

import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { GovukValidators } from 'govuk-components';

import { PermitIssuanceGrantDetermination } from 'pmrv-api';

export const firstYearFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore],
  useFactory: (fb: UntypedFormBuilder, store: PermitApplicationStore<PermitApplicationState>) => {
    const state = store.getValue();
    const value = state.determination as PermitIssuanceGrantDetermination;

    return fb.group({
      firstYearOfReportingObligation: [
        {
          value: value?.['firstYearOfReportingObligation'] ?? null,
          disabled: !state.isEditable,
        },
        {
          validators: [
            reportingFirstYearValidator(),
            GovukValidators.naturalNumber('The value must be a positive integer'),
            GovukValidators.required('Enter the first year of Registry reporting obligation'),
          ],
          updateOn: 'change',
        },
      ],
    });
  },
};

export function reportingFirstYearValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors => {
    const firstYearOfReportingObligation = +control.value;
    const minYear = 2021;
    const maxYear = new Date().getFullYear() + 1;

    if (!control.value) {
      return null;
    }

    if (firstYearOfReportingObligation < minYear) {
      return {
        invalidYear: 'The year must be the same as or after 2021',
      };
    } else if (firstYearOfReportingObligation > maxYear) {
      return {
        invalidYear: 'The year can only be one year in the future from today',
      };
    } else {
      return null;
    }
  };
}
