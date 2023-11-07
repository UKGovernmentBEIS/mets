import { FormControl } from '@angular/forms';

import { takeUntil } from 'rxjs';
import { Subject } from 'rxjs/internal/Subject';

import { GovukValidators } from 'govuk-components';

import { AviationAerEmissionsReductionClaimVerification } from 'pmrv-api';

const batchReferencesNotReviewedValidator = [GovukValidators.required('List the unreviewed batch references')];

const dataSamplingValidator = [GovukValidators.required('Describe how data sampling was done')];

export function createBatchClaimsForm(destroy$: Subject<void>) {
  const field = {
    safBatchClaimsReviewed: new FormControl<
      AviationAerEmissionsReductionClaimVerification['safBatchClaimsReviewed'] | null
    >(null, [GovukValidators.required('Select if you have reviewed all the operator’s SAF batch claims')]),

    batchReferencesNotReviewed: new FormControl<
      AviationAerEmissionsReductionClaimVerification['batchReferencesNotReviewed'] | null
    >(null, batchReferencesNotReviewedValidator),

    dataSampling: new FormControl<AviationAerEmissionsReductionClaimVerification['dataSampling'] | null>(
      null,
      dataSamplingValidator,
    ),
  };

  field.safBatchClaimsReviewed.valueChanges.pipe(takeUntil(destroy$)).subscribe((value) => {
    if (value === false) {
      field.batchReferencesNotReviewed.setValidators(batchReferencesNotReviewedValidator);
      field.batchReferencesNotReviewed.updateValueAndValidity();

      field.dataSampling.setValidators(dataSamplingValidator);
      field.dataSampling.updateValueAndValidity();
    } else {
      field.batchReferencesNotReviewed.reset();
      field.batchReferencesNotReviewed.clearValidators();

      field.dataSampling.reset();
      field.dataSampling.clearValidators();
    }
  });

  return field;
}
