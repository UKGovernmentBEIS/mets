import { FormControl } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { AviationAerEtsComplianceRules } from 'pmrv-api';

const fuelConsistencyChecksNotPerformedReasonValidator = [
  GovukValidators.required(
    'Provide a reason why checks for consistency between aggregate fuel consumption and fuel purchase or supply data were not performed',
  ),
];

export function createFuelConsistencyChecksPerformedForm(destroy$: Subject<void>) {
  const field = {
    fuelConsistencyChecksPerformed: new FormControl<
      AviationAerEtsComplianceRules['fuelConsistencyChecksPerformed'] | null
    >(null, {
      updateOn: 'change',
      validators: [
        GovukValidators.required(
          'Select if checks for consistency between aggregate fuel consumption and fuel purchase or supply data have been performed',
        ),
      ],
    }),

    fuelConsistencyChecksNotPerformedReason: new FormControl<
      AviationAerEtsComplianceRules['fuelConsistencyChecksNotPerformedReason'] | null
    >(null, fuelConsistencyChecksNotPerformedReasonValidator),
  };

  field.fuelConsistencyChecksPerformed.valueChanges.pipe(takeUntil(destroy$)).subscribe((value) => {
    if (value) {
      field.fuelConsistencyChecksNotPerformedReason.reset();
      field.fuelConsistencyChecksNotPerformedReason.clearValidators();
    } else {
      field.fuelConsistencyChecksNotPerformedReason.setValidators(fuelConsistencyChecksNotPerformedReasonValidator);
      field.fuelConsistencyChecksNotPerformedReason.updateValueAndValidity();
    }
  });

  return field;
}
