import { FormControl } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { AviationAerEtsComplianceRules } from 'pmrv-api';

const flightsCompletenessNotComparedWithAirTrafficDataReasonValidator = [
  GovukValidators.required(
    'Provide a reason why the completeness of flights or data has not been compared with air traffic data',
  ),
];

export function createFlightsCompletenessComparedWithAirTrafficDataForm(destroy$: Subject<void>) {
  const field = {
    flightsCompletenessComparedWithAirTrafficData: new FormControl<
      AviationAerEtsComplianceRules['flightsCompletenessComparedWithAirTrafficData'] | null
    >(null, {
      updateOn: 'change',
      validators: [
        GovukValidators.required(
          'Select if the completeness of flights or data has been compared with air traffic data',
        ),
      ],
    }),

    flightsCompletenessNotComparedWithAirTrafficDataReason: new FormControl<
      AviationAerEtsComplianceRules['flightsCompletenessNotComparedWithAirTrafficDataReason'] | null
    >(null, flightsCompletenessNotComparedWithAirTrafficDataReasonValidator),
  };

  field.flightsCompletenessComparedWithAirTrafficData.valueChanges.pipe(takeUntil(destroy$)).subscribe((value) => {
    if (value) {
      field.flightsCompletenessNotComparedWithAirTrafficDataReason.reset();
      field.flightsCompletenessNotComparedWithAirTrafficDataReason.clearValidators();
    } else {
      field.flightsCompletenessNotComparedWithAirTrafficDataReason.setValidators(
        flightsCompletenessNotComparedWithAirTrafficDataReasonValidator,
      );

      field.flightsCompletenessNotComparedWithAirTrafficDataReason.updateValueAndValidity();
    }
  });

  return field;
}
