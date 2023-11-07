import { FormControl } from '@angular/forms';

import { takeUntil } from 'rxjs';
import { Subject } from 'rxjs/internal/Subject';

import { GovukValidators } from 'govuk-components';

import { AviationAerEmissionsReductionClaimVerification } from 'pmrv-api';

const notCompliedWithUkEtsRequirementsAspectsValidator = [
  GovukValidators.required('Describe the aspects of the claim that were not compliant'),
];

export function createComplianceWithUkEtsRequirementsForm(destroy$: Subject<void>) {
  const field = {
    complianceWithUkEtsRequirementsExist: new FormControl<
      AviationAerEmissionsReductionClaimVerification['complianceWithUkEtsRequirementsExist'] | null
    >(null, [
      GovukValidators.required(
        'Select if the operator’s ERC was compliant with their EMP, the legislation and regulator guidance',
      ),
    ]),

    notCompliedWithUkEtsRequirementsAspects: new FormControl<
      AviationAerEmissionsReductionClaimVerification['notCompliedWithUkEtsRequirementsAspects'] | null
    >(null, notCompliedWithUkEtsRequirementsAspectsValidator),
  };

  field.complianceWithUkEtsRequirementsExist.valueChanges.pipe(takeUntil(destroy$)).subscribe((value) => {
    if (value === false) {
      field.notCompliedWithUkEtsRequirementsAspects.setValidators(notCompliedWithUkEtsRequirementsAspectsValidator);
      field.notCompliedWithUkEtsRequirementsAspects.updateValueAndValidity();
    } else {
      field.notCompliedWithUkEtsRequirementsAspects.reset();
      field.notCompliedWithUkEtsRequirementsAspects.clearValidators();
    }
  });

  return field;
}
