import { FormControl } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { AviationAerEmissionsReductionClaimVerification } from 'pmrv-api';

export function createReviewResultsForm() {
  const field = {
    reviewResults: new FormControl<AviationAerEmissionsReductionClaimVerification['reviewResults'] | null>(null, [
      GovukValidators.required('Describe the results of your review'),
    ]),
  };

  return field;
}
