import { UntypedFormBuilder } from '@angular/forms';

import { DRE_TASK_FORM } from '@tasks/dre/core/dre-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { DreApplicationSubmitRequestTaskPayload, DreMonitoringApproachReportingEmissions } from 'pmrv-api';

export const reportableEmissionsFormProvider = {
  provide: DRE_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    const approaches = (state.requestTaskItem.requestTask.payload as DreApplicationSubmitRequestTaskPayload)?.dre
      ?.monitoringApproachReportingEmissions;

    return fb.group({
      calculationOfCO2: fb.group({
        combustionEmissions: reportableAndBiomassEmissionFormGroup(
          fb,
          approaches,
          'CALCULATION_CO2',
          'combustionEmissions',
          disabled,
          !!approaches?.CALCULATION_CO2,
        ),
        processEmissions: reportableAndBiomassEmissionFormGroup(
          fb,
          approaches,
          'CALCULATION_CO2',
          'processEmissions',
          disabled,
          !!approaches?.CALCULATION_CO2,
        ),
        massBalanceEmissions: reportableAndBiomassEmissionFormGroup(
          fb,
          approaches,
          'CALCULATION_CO2',
          'massBalanceEmissions',
          disabled,
          !!approaches?.CALCULATION_CO2,
        ),
        ...((approaches?.CALCULATION_CO2 as any)?.calculateTransferredCO2
          ? {
              transferredCO2Emissions: reportableAndBiomassEmissionFormGroup(
                fb,
                approaches,
                'CALCULATION_CO2',
                'transferredCO2Emissions',
                disabled,
                true,
              ),
            }
          : {}),
      }),

      measurementOfCO2: fb.group({
        emissions: reportableAndBiomassEmissionFormGroup(
          fb,
          approaches,
          'MEASUREMENT_CO2',
          'emissions',
          disabled,
          !!approaches?.MEASUREMENT_CO2,
        ),
        ...((approaches?.MEASUREMENT_CO2 as any)?.measureTransferredCO2
          ? {
              transferredCO2Emissions: reportableAndBiomassEmissionFormGroup(
                fb,
                approaches,
                'MEASUREMENT_CO2',
                'transferredCO2Emissions',
                disabled,
                true,
              ),
            }
          : {}),
      }),

      measurementOfN2O: fb.group({
        emissions: reportableAndBiomassEmissionFormGroup(
          fb,
          approaches,
          'MEASUREMENT_N2O',
          'emissions',
          disabled,
          !!approaches?.MEASUREMENT_N2O,
        ),
        ...((approaches?.MEASUREMENT_N2O as any)?.measureTransferredN2O
          ? {
              transferredN2OEmissions: reportableAndBiomassEmissionFormGroup(
                fb,
                approaches,
                'MEASUREMENT_N2O',
                'transferredN2OEmissions',
                disabled,
                true,
              ),
            }
          : {}),
      }),

      calculationOfPFC: fb.group({
        totalEmissions: reportableEmissionFormGroup(
          fb,
          approaches,
          'CALCULATION_PFC',
          'totalEmissions',
          disabled,
          !!approaches?.CALCULATION_PFC,
        ),
      }),

      inherentOfCO2: fb.group({
        totalEmissions: reportableEmissionFormGroup(
          fb,
          approaches,
          'INHERENT_CO2',
          'totalEmissions',
          disabled,
          !!approaches?.INHERENT_CO2,
        ),
      }),

      fallback: fb.group({
        emissions: reportableAndBiomassEmissionFormGroup(
          fb,
          approaches,
          'FALLBACK',
          'emissions',
          disabled,
          !!approaches?.FALLBACK,
        ),
      }),
    });
  },
};

const reportableAndBiomassEmissionFormGroup = (
  fb: UntypedFormBuilder,
  approaches: {
    [key: string]: DreMonitoringApproachReportingEmissions;
  },
  type: string,
  subType: string,
  disabled: boolean,
  enableValidators: boolean,
) => {
  return fb.group(
    {
      reportableEmissions: [
        {
          value: (approaches?.[type] as any)?.[subType]?.reportableEmissions ?? null,
          disabled,
        },
        {
          validators: enableValidators
            ? [
                GovukValidators.required(`Enter the total reportable emissions, including non-sustainable biomass`),
                GovukValidators.maxDecimalsValidator(5),
              ]
            : [],
        },
      ],
      sustainableBiomass: [
        {
          value: (approaches?.[type] as any)?.[subType]?.sustainableBiomass ?? null,
          disabled,
        },
        {
          validators: enableValidators
            ? [
                GovukValidators.required(`Enter the total sustainable biomass emissions`),
                GovukValidators.maxDecimalsValidator(5),
              ]
            : [],
        },
      ],
    },
    {
      updateOn: 'change',
    },
  );
};

const reportableEmissionFormGroup = (
  fb: UntypedFormBuilder,
  approaches: {
    [key: string]: DreMonitoringApproachReportingEmissions;
  },
  type: string,
  subType: string,
  disabled: boolean,
  enableValidators: boolean,
) => {
  return fb.group(
    {
      reportableEmissions: [
        {
          value: (approaches?.[type] as any)?.[subType]?.reportableEmissions ?? null,
          disabled,
        },
        {
          validators: enableValidators
            ? [
                GovukValidators.required(`Enter the total reportable emissions`),
                GovukValidators.maxDecimalsValidator(5),
              ]
            : [],
        },
      ],
    },
    {
      updateOn: 'change',
    },
  );
};
