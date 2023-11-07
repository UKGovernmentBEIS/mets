import { FormControl, FormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

export function createAnotherVariationScheduleItem(value): FormGroup {
  return new FormGroup({
    item: new FormControl(value ?? null, [
      GovukValidators.required('Add an item to the variation schedule'),
      GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
    ]),
  });
}
