import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload, CalculationOfPfcEmissions } from 'pmrv-api';

import { AER_PFC_FORM } from '../pfc';

export const methodsFormProvider = {
  provide: AER_PFC_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const methodType = route.snapshot.data.methodType;

    const state = store.getValue();

    const disabled = !state.isEditable;

    const payload = state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload;

    const sourceStreamEmission = route.snapshot.paramMap.get('index')
      ? (payload.aer.monitoringApproachEmissions.CALCULATION_PFC as CalculationOfPfcEmissions)?.sourceStreamEmissions[
          Number(route.snapshot.paramMap.get('index'))
        ]
      : null;

    const methodData = sourceStreamEmission?.pfcSourceStreamEmissionCalculationMethodData as any;

    return fb.group({
      ...(methodType === 'METHOD_A'
        ? {
            anodeEffectsPerCellDay: [
              { value: methodData?.anodeEffectsPerCellDay ?? null, disabled: disabled },
              {
                validators: [GovukValidators.required('Enter a value'), GovukValidators.maxIntegersValidator(12)],
              },
            ],
            averageDurationOfAnodeEffectsInMinutes: [
              { value: methodData?.averageDurationOfAnodeEffectsInMinutes ?? null, disabled: disabled },
              {
                validators: [GovukValidators.required('Enter a value'), GovukValidators.maxIntegersValidator(12)],
              },
            ],
            slopeCF4EmissionFactor: [
              { value: methodData?.slopeCF4EmissionFactor ?? null, disabled: disabled },
              {
                validators: [GovukValidators.required('Enter a value'), GovukValidators.maxIntegersValidator(12)],
              },
            ],
          }
        : {}),
      ...(methodType === 'METHOD_B'
        ? {
            anodeEffectsOverVoltagePerCell: [
              { value: methodData?.anodeEffectsOverVoltagePerCell ?? null, disabled: disabled },
              {
                validators: [GovukValidators.required('Enter a value'), GovukValidators.maxIntegersValidator(12)],
              },
            ],
            aluminiumAverageCurrentEfficiencyProduction: [
              { value: methodData?.aluminiumAverageCurrentEfficiencyProduction ?? null, disabled: disabled },
              {
                validators: [
                  GovukValidators.required('Enter a value'),
                  GovukValidators.maxDecimalsValidator(5),
                  GovukValidators.positiveNumber(),
                  GovukValidators.max(100, `Enter a number between 0.00001 and 100`),
                ],
              },
            ],
            overVoltageCoefficient: [
              { value: methodData?.overVoltageCoefficient ?? null, disabled: disabled },
              {
                validators: [GovukValidators.required('Enter a value'), GovukValidators.maxIntegersValidator(12)],
              },
            ],
          }
        : {}),

      c2F6WeightFraction: [
        { value: methodData?.c2F6WeightFraction ?? null, disabled: disabled },
        {
          validators: [GovukValidators.required('Enter a value'), GovukValidators.maxIntegersValidator(12)],
        },
      ],
      percentageOfCollectionEfficiency: [
        { value: methodData?.percentageOfCollectionEfficiency ?? null, disabled: disabled },
        {
          validators: [
            GovukValidators.required('Enter a value'),
            GovukValidators.maxDecimalsValidator(5),
            GovukValidators.positiveNumber(),
            GovukValidators.max(100, `Enter a number between 0.00001 and 100`), // TODO replace with GovukValidators.minMaxRangeNumberValidator(0.00001, 100),
          ],
        },
      ],
    });
  },
};
