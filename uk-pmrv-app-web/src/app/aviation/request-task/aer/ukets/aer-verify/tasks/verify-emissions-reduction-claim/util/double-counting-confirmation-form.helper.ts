import { FormControl } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { AviationAerEmissionsReductionClaimVerification } from 'pmrv-api';

export function createDoubleCountingConfirmationForm() {
  const field = {
    noDoubleCountingConfirmation: new FormControl<
      AviationAerEmissionsReductionClaimVerification['noDoubleCountingConfirmation'] | null
    >(null, [
      GovukValidators.required('Describe the steps you took to confirm that no double-counting was taking place'),
    ]),
  };

  return field;
}
