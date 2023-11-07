import { FormControl } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { AviationAerEtsComplianceRules } from 'pmrv-api';

const controlActivitiesNotDocumentedReasonValidator = [
  GovukValidators.required(
    'Provide a reason why control activities were not documented, implemented, maintained and effective to reduce risks',
  ),
];

export function createControlActivitiesForm(destroy$: Subject<void>) {
  const field = {
    controlActivitiesDocumented: new FormControl<AviationAerEtsComplianceRules['controlActivitiesDocumented'] | null>(
      null,
      {
        updateOn: 'change',
        validators: [
          GovukValidators.required(
            'Select if control activities were documented, implemented, maintained and effective to reduce risks',
          ),
        ],
      },
    ),

    controlActivitiesNotDocumentedReason: new FormControl<
      AviationAerEtsComplianceRules['controlActivitiesNotDocumentedReason'] | null
    >(null, controlActivitiesNotDocumentedReasonValidator),
  };

  field.controlActivitiesDocumented.valueChanges.pipe(takeUntil(destroy$)).subscribe((value) => {
    if (value) {
      field.controlActivitiesNotDocumentedReason.reset();
      field.controlActivitiesNotDocumentedReason.clearValidators();
    } else {
      field.controlActivitiesNotDocumentedReason.setValidators(controlActivitiesNotDocumentedReasonValidator);
      field.controlActivitiesNotDocumentedReason.updateValueAndValidity();
    }
  });

  return field;
}
