import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup } from '@angular/forms';

import { atLeastOneRequiredValidator } from '@shared-user/utils/validators';

import { GovukValidators } from 'govuk-components';

import { EnvPermitOrLicence } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';

export const otherPermitsFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore],
  useFactory: (fb: UntypedFormBuilder, store: PermitApplicationStore<PermitApplicationState>) => {
    const value = store.permit.environmentalPermitsAndLicences;

    return fb.group({
      exist: [
        { value: value?.exist ?? null, disabled: !store.getValue().isEditable },
        { validators: GovukValidators.required('Select yes or no'), updateOn: 'change' },
      ],
      envPermitOrLicences: fb.array(value?.envPermitOrLicences?.map(createAnotherPermit) ?? [createAnotherPermit()]),
    });
  },
};

export function createAnotherPermit(value?: EnvPermitOrLicence): UntypedFormGroup {
  return new UntypedFormGroup(
    {
      type: new UntypedFormControl(value?.type ?? null, [
        GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
      ]),
      num: new UntypedFormControl(value?.num ?? null, [
        GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
      ]),
      issuingAuthority: new UntypedFormControl(value?.issuingAuthority ?? null, [
        GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
      ]),
      permitHolder: new UntypedFormControl(value?.permitHolder ?? null, [
        GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
      ]),
    },
    {
      validators: [
        atLeastOneRequiredValidator(
          'Enter the type or the number or the issuing authority or the permit holder of the permit or licence',
        ),
      ],
    },
  );
}
