import { FormControl, FormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { LocationOnShoreStateDTO } from 'pmrv-api';

export const getLocationOnShoreFormGroup = (): FormGroup<Record<keyof LocationOnShoreStateDTO, FormControl>> => {
  return new FormGroup(
    {
      type: new FormControl<LocationOnShoreStateDTO['type']>('ONSHORE_STATE'),
      line1: new FormControl<LocationOnShoreStateDTO['line1'] | null>(null, {
        validators: [
          GovukValidators.required('Enter the first line of your address'),
          GovukValidators.maxLength(100, 'Address line 1 should not be more than 100 characters'),
        ],
      }),
      line2: new FormControl<LocationOnShoreStateDTO['line2'] | null>(null, {
        validators: GovukValidators.maxLength(100, 'Address line 2 should not be more than 100 characters'),
      }),
      city: new FormControl<LocationOnShoreStateDTO['city'] | null>(null, {
        validators: [
          GovukValidators.required('Enter your town or city'),
          GovukValidators.maxLength(100, 'Town or city should not be more than 100 characters'),
        ],
      }),
      state: new FormControl<LocationOnShoreStateDTO['state'] | null>(null, {
        validators: GovukValidators.maxLength(100, 'State, province or region should not be more than 100 characters'),
      }),
      postcode: new FormControl<LocationOnShoreStateDTO['postcode'] | null>(null, {
        validators: GovukValidators.maxLength(20, 'Postal code should not be more than 20 characters'),
      }),
      country: new FormControl<LocationOnShoreStateDTO['country'] | null>(null, {
        validators: GovukValidators.required('Enter your country'),
      }),
    },
    { updateOn: 'change' },
  );
};
