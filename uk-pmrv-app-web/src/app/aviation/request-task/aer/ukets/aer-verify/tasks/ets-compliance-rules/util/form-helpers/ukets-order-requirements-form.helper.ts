import { FormControl } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { AviationAerEtsComplianceRules } from 'pmrv-api';

const ukEtsOrderRequirementsNotMetReasonValidator = [
  GovukValidators.required('Provide a reason why the requirements of the UK ETS Order have not been met'),
];

export function createUkEtsOrderRequirementsForm(destroy$: Subject<void>) {
  const field = {
    ukEtsOrderRequirementsMet: new FormControl<AviationAerEtsComplianceRules['ukEtsOrderRequirementsMet'] | null>(
      null,
      {
        updateOn: 'change',
        validators: [GovukValidators.required('Select if the requirements of the UK ETS Order have been met')],
      },
    ),

    ukEtsOrderRequirementsNotMetReason: new FormControl<
      AviationAerEtsComplianceRules['ukEtsOrderRequirementsNotMetReason'] | null
    >(null, ukEtsOrderRequirementsNotMetReasonValidator),
  };

  field.ukEtsOrderRequirementsMet.valueChanges.pipe(takeUntil(destroy$)).subscribe((value) => {
    if (value) {
      field.ukEtsOrderRequirementsNotMetReason.reset();
      field.ukEtsOrderRequirementsNotMetReason.clearValidators();
    } else {
      field.ukEtsOrderRequirementsNotMetReason.setValidators(ukEtsOrderRequirementsNotMetReasonValidator);
      field.ukEtsOrderRequirementsNotMetReason.updateValueAndValidity();
    }
  });

  return field;
}
