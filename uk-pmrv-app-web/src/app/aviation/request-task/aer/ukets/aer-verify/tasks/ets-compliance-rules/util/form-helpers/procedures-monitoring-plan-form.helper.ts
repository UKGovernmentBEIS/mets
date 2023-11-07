import { FormControl } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { AviationAerEtsComplianceRules } from 'pmrv-api';

const proceduresMonitoringPlanNotDocumentedReasonValidator = [
  GovukValidators.required(
    'Provide a reason why procedures in the emissions monitoring plan were not documented, implemented, maintained and effective to reduce risks',
  ),
];

export function createProceduresMonitoringPlanForm(destroy$: Subject<void>) {
  const field = {
    proceduresMonitoringPlanDocumented: new FormControl<
      AviationAerEtsComplianceRules['proceduresMonitoringPlanDocumented'] | null
    >(null, {
      updateOn: 'change',
      validators: [
        GovukValidators.required(
          'Select if procedures in the emissions monitoring plan were documented, implemented, maintained and effective to reduce risks',
        ),
      ],
    }),

    proceduresMonitoringPlanNotDocumentedReason: new FormControl<
      AviationAerEtsComplianceRules['proceduresMonitoringPlanNotDocumentedReason'] | null
    >(null, proceduresMonitoringPlanNotDocumentedReasonValidator),
  };

  field.proceduresMonitoringPlanDocumented.valueChanges.pipe(takeUntil(destroy$)).subscribe((value) => {
    if (value) {
      field.proceduresMonitoringPlanNotDocumentedReason.reset();
      field.proceduresMonitoringPlanNotDocumentedReason.clearValidators();
    } else {
      field.proceduresMonitoringPlanNotDocumentedReason.setValidators(
        proceduresMonitoringPlanNotDocumentedReasonValidator,
      );

      field.proceduresMonitoringPlanNotDocumentedReason.updateValueAndValidity();
    }
  });

  return field;
}
