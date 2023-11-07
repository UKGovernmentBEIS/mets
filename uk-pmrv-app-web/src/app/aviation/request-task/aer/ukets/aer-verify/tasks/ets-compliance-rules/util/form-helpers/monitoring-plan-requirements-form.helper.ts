import { FormControl } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { AviationAerEtsComplianceRules } from 'pmrv-api';

const monitoringPlanRequirementsNotMetReasonValidator = [
  GovukValidators.required('Provide a reason why the monitoring and reporting requirements have not been met'),
];

export function createMonitoringPlanRequirementsForm(destroy$: Subject<void>) {
  const field = {
    monitoringPlanRequirementsMet: new FormControl<
      AviationAerEtsComplianceRules['monitoringPlanRequirementsMet'] | null
    >(null, {
      updateOn: 'change',
      validators: [GovukValidators.required('Select if the monitoring plan requirements and conditions have been met')],
    }),

    monitoringPlanRequirementsNotMetReason: new FormControl<
      AviationAerEtsComplianceRules['monitoringPlanRequirementsNotMetReason'] | null
    >(null, monitoringPlanRequirementsNotMetReasonValidator),
  };

  field.monitoringPlanRequirementsMet.valueChanges.pipe(takeUntil(destroy$)).subscribe((value) => {
    if (value) {
      field.monitoringPlanRequirementsNotMetReason.reset();
      field.monitoringPlanRequirementsNotMetReason.clearValidators();
    } else {
      field.monitoringPlanRequirementsNotMetReason.setValidators(monitoringPlanRequirementsNotMetReasonValidator);
      field.monitoringPlanRequirementsNotMetReason.updateValueAndValidity();
    }
  });

  return field;
}
