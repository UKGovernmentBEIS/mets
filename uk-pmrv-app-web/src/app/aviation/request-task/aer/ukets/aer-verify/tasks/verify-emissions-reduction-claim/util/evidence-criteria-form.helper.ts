import { FormControl } from '@angular/forms';

import { takeUntil } from 'rxjs';
import { Subject } from 'rxjs/internal/Subject';

import { GovukValidators } from 'govuk-components';

import { AviationAerEmissionsReductionClaimVerification } from 'pmrv-api';

const noCriteriaMetIssuesValidator = [GovukValidators.required('State the issues that you have identified')];

export function createEvidenceCriteriaForm(destroy$: Subject<void>) {
  const field = {
    evidenceAllCriteriaMetExist: new FormControl<
      AviationAerEmissionsReductionClaimVerification['evidenceAllCriteriaMetExist'] | null
    >(null, [GovukValidators.required('Select if all the batch claims contained evidence of criteria being met')]),

    noCriteriaMetIssues: new FormControl<AviationAerEmissionsReductionClaimVerification['noCriteriaMetIssues'] | null>(
      null,
      noCriteriaMetIssuesValidator,
    ),
  };

  field.evidenceAllCriteriaMetExist.valueChanges.pipe(takeUntil(destroy$)).subscribe((value) => {
    if (value === false) {
      field.noCriteriaMetIssues.setValidators(noCriteriaMetIssuesValidator);
      field.noCriteriaMetIssues.updateValueAndValidity();
    } else {
      field.noCriteriaMetIssues.reset();
      field.noCriteriaMetIssues.clearValidators();
    }
  });

  return field;
}
