import { FormControl } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { AviationAerEtsComplianceRules } from 'pmrv-api';

const regulatorGuidanceNotMetReasonValidator = [
  GovukValidators.required('Provide a reason why the regulator guidance on monitoring and reporting has not been met'),
];

export function createRegulatorGuidanceMetForm(destroy$: Subject<void>) {
  const field = {
    regulatorGuidanceMet: new FormControl<AviationAerEtsComplianceRules['regulatorGuidanceMet'] | null>(null, {
      updateOn: 'change',
      validators: [
        GovukValidators.required('Select if the regulator guidance on monitoring and reporting has been met'),
      ],
    }),

    regulatorGuidanceNotMetReason: new FormControl<
      AviationAerEtsComplianceRules['regulatorGuidanceNotMetReason'] | null
    >(null, regulatorGuidanceNotMetReasonValidator),
  };

  field.regulatorGuidanceMet.valueChanges.pipe(takeUntil(destroy$)).subscribe((value) => {
    if (value) {
      field.regulatorGuidanceNotMetReason.reset();
      field.regulatorGuidanceNotMetReason.clearValidators();
    } else {
      field.regulatorGuidanceNotMetReason.setValidators(regulatorGuidanceNotMetReasonValidator);
      field.regulatorGuidanceNotMetReason.updateValueAndValidity();
    }
  });

  return field;
}
