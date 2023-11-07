import { FormControl } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { AviationAerEtsComplianceRules } from 'pmrv-api';

const reportedDataConsistencyChecksNotPerformedReasonValidator = [
  GovukValidators.required(
    'Provide a reason why checks for consistency between reported data and "mass and balance" documentation were not performed',
  ),
];

export function createReportedDataConsistencyChecksPerformedForm(destroy$: Subject<void>) {
  const field = {
    reportedDataConsistencyChecksPerformed: new FormControl<
      AviationAerEtsComplianceRules['reportedDataConsistencyChecksPerformed'] | null
    >(null, {
      updateOn: 'change',
      validators: [
        GovukValidators.required(
          'Select if checks for consistency between reported data and ‘mass and balance’ documentation have been performed',
        ),
      ],
    }),

    reportedDataConsistencyChecksNotPerformedReason: new FormControl<
      AviationAerEtsComplianceRules['reportedDataConsistencyChecksNotPerformedReason'] | null
    >(null, reportedDataConsistencyChecksNotPerformedReasonValidator),
  };

  field.reportedDataConsistencyChecksPerformed.valueChanges.pipe(takeUntil(destroy$)).subscribe((value) => {
    if (value) {
      field.reportedDataConsistencyChecksNotPerformedReason.reset();
      field.reportedDataConsistencyChecksNotPerformedReason.clearValidators();
    } else {
      field.reportedDataConsistencyChecksNotPerformedReason.setValidators(
        reportedDataConsistencyChecksNotPerformedReasonValidator,
      );

      field.reportedDataConsistencyChecksNotPerformedReason.updateValueAndValidity();
    }
  });

  return field;
}
