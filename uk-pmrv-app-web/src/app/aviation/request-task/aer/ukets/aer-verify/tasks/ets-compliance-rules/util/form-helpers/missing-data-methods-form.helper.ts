import { FormControl } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { AviationAerEtsComplianceRules } from 'pmrv-api';

const missingDataMethodsNotAppliedReasonValidator = [
  GovukValidators.required('Provide a reason why the methods used for applying missing data were not appropriate'),
];

export function createMissingDataMethodsAppliedForm(destroy$: Subject<void>) {
  const field = {
    missingDataMethodsApplied: new FormControl<AviationAerEtsComplianceRules['missingDataMethodsApplied'] | null>(
      null,
      {
        updateOn: 'change',
        validators: [GovukValidators.required('Select if methods used for applying missing data were appropriate')],
      },
    ),

    missingDataMethodsNotAppliedReason: new FormControl<
      AviationAerEtsComplianceRules['missingDataMethodsNotAppliedReason'] | null
    >(null, missingDataMethodsNotAppliedReasonValidator),
  };

  field.missingDataMethodsApplied.valueChanges.pipe(takeUntil(destroy$)).subscribe((value) => {
    if (value) {
      field.missingDataMethodsNotAppliedReason.reset();
      field.missingDataMethodsNotAppliedReason.clearValidators();
    } else {
      field.missingDataMethodsNotAppliedReason.setValidators(missingDataMethodsNotAppliedReasonValidator);
      field.missingDataMethodsNotAppliedReason.updateValueAndValidity();
    }
  });

  return field;
}
