import { FormControl } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { AviationAerEtsComplianceRules } from 'pmrv-api';

export function createNonConformitiesForm() {
  return {
    nonConformities: new FormControl<AviationAerEtsComplianceRules['nonConformities'] | null>(null, {
      validators: [GovukValidators.required('Select if any non-conformities from last year have been corrected')],
    }),
  };
}
