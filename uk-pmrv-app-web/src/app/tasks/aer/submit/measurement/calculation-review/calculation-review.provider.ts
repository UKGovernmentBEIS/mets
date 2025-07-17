import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { AER_MEASUREMENT_FORM } from '../measurement-status';

export const calculationReviewFormProvider = {
  provide: AER_MEASUREMENT_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const taskKey = route.snapshot.data.taskKey;
    const state = store.getValue();

    const disabled = !state.isEditable;

    const payload = state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload;

    const emissionPointEmission = route.snapshot.paramMap.get('index')
      ? (payload.aer?.monitoringApproachEmissions[taskKey] as any)?.emissionPointEmissions?.[
          Number(route.snapshot.paramMap.get('index'))
        ]
      : null;

    const containsBiomass = emissionPointEmission?.biomassPercentages?.contains;

    return fb.group({
      calculationCorrect: [
        { value: emissionPointEmission?.calculationCorrect ?? null, disabled },
        {
          validators: GovukValidators.required('Select yes if the calculated emissions are correct'),
        },
      ],
      reasonForProvidingManualEmissions: [
        { value: emissionPointEmission?.providedEmissions?.reasonForProvidingManualEmissions ?? null, disabled },
        {
          validators: GovukValidators.required('Explain why you are providing your own emission figures'),
        },
      ],
      totalProvidedReportableEmissions: [
        { value: emissionPointEmission?.providedEmissions?.totalProvidedReportableEmissions ?? null, disabled },
        {
          validators: [
            GovukValidators.required(
              `Enter the total reportable emissions ${containsBiomass ? `, including non-sustainable biomass` : ''}`,
            ),
            GovukValidators.maxDecimalsValidator(5),
          ],
        },
      ],
      ...(emissionPointEmission?.biomassPercentages?.contains
        ? {
            totalProvidedSustainableBiomassEmissions: [
              {
                value: emissionPointEmission?.providedEmissions?.totalProvidedSustainableBiomassEmissions ?? null,
                disabled,
              },
              {
                validators: [
                  GovukValidators.required('Enter the total sustainable biomass emissions'),
                  GovukValidators.maxDecimalsValidator(5),
                ],
              },
            ],
          }
        : []),
      fossilEmissionsTotalEnergyContent: [
        {
          value: emissionPointEmission?.measurementAdditionalInformation?.fossilEmissionsTotalEnergyContent ?? null,
          disabled,
        },
        {
          validators: [GovukValidators.notNaN('Enter a numeric value')],
        },
      ],
      biomassEmissionsTotalEnergyContent: [
        {
          value: emissionPointEmission?.measurementAdditionalInformation?.biomassEmissionsTotalEnergyContent ?? null,
          disabled,
        },
        {
          validators: [GovukValidators.notNaN('Enter a numeric value')],
        },
      ],
      fossilEmissionsCorroboratingCalculation: [
        {
          value:
            emissionPointEmission?.measurementAdditionalInformation?.fossilEmissionsCorroboratingCalculation ?? null,
          disabled,
        },
        {
          validators: [GovukValidators.notNaN('Enter a numeric value')],
        },
      ],
      biomassEmissionsCorroboratingCalculation: [
        {
          value:
            emissionPointEmission?.measurementAdditionalInformation?.biomassEmissionsCorroboratingCalculation ?? null,
          disabled,
        },
        {
          validators: [GovukValidators.notNaN('Enter a numeric value')],
        },
      ],
    });
  },
};
