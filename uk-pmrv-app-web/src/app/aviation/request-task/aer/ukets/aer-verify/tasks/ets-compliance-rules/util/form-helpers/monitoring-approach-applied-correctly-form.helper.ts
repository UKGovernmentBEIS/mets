import { FormControl } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { AviationAerEtsComplianceRules } from 'pmrv-api';

const monitoringApproachNotAppliedCorrectlyReasonValidator = [
  GovukValidators.required('Provide a reason why the monitoring approach was not applied correctly'),
];

export function createMonitoringApproachAppliedCorrectlyForm(destroy$: Subject<void>) {
  const field = {
    monitoringApproachAppliedCorrectly: new FormControl<
      AviationAerEtsComplianceRules['monitoringApproachAppliedCorrectly'] | null
    >(null, {
      updateOn: 'change',
      validators: [GovukValidators.required('Select if the monitoring approach has been applied correctly')],
    }),

    monitoringApproachNotAppliedCorrectlyReason: new FormControl<
      AviationAerEtsComplianceRules['monitoringApproachNotAppliedCorrectlyReason'] | null
    >(null, monitoringApproachNotAppliedCorrectlyReasonValidator),
  };

  field.monitoringApproachAppliedCorrectly.valueChanges.pipe(takeUntil(destroy$)).subscribe((value) => {
    if (value) {
      field.monitoringApproachNotAppliedCorrectlyReason.reset();
      field.monitoringApproachNotAppliedCorrectlyReason.clearValidators();
    } else {
      field.monitoringApproachNotAppliedCorrectlyReason.setValidators(
        monitoringApproachNotAppliedCorrectlyReasonValidator,
      );

      field.monitoringApproachNotAppliedCorrectlyReason.updateValueAndValidity();
    }
  });

  return field;
}
