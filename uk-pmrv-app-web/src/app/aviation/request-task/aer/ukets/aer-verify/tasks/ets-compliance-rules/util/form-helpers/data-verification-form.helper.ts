import { FormControl } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { AviationAerEtsComplianceRules } from 'pmrv-api';

const dataVerificationNotCompletedReasonValidator = [
  GovukValidators.required('Provide a reason why data verification was not completed as required'),
];

export function createDataVerificationCompletedForm(destroy$: Subject<void>) {
  const field = {
    dataVerificationCompleted: new FormControl<AviationAerEtsComplianceRules['dataVerificationCompleted'] | null>(
      null,
      {
        updateOn: 'change',
        validators: [GovukValidators.required('Select if data verification has been completed as required')],
      },
    ),

    dataVerificationNotCompletedReason: new FormControl<
      AviationAerEtsComplianceRules['dataVerificationNotCompletedReason'] | null
    >(null, dataVerificationNotCompletedReasonValidator),
  };

  field.dataVerificationCompleted.valueChanges.pipe(takeUntil(destroy$)).subscribe((value) => {
    if (value) {
      field.dataVerificationNotCompletedReason.reset();
      field.dataVerificationNotCompletedReason.clearValidators();
    } else {
      field.dataVerificationNotCompletedReason.setValidators(dataVerificationNotCompletedReasonValidator);
      field.dataVerificationNotCompletedReason.updateValueAndValidity();
    }
  });

  return field;
}
