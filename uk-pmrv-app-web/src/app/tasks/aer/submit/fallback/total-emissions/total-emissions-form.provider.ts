import { UntypedFormBuilder } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import {
  noTotalBiomassEmissions,
  noTotalEnergyContentBiomass,
  noTotalFossilEmissions,
  noTotalFossilEnergyContent,
  noTotalNonSustainableBiomassEmissions,
} from '@tasks/aer/submit/fallback/errors/fallback-validation.errors';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload, FallbackEmissions } from 'pmrv-api';

export const totalEmissionsFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const fallbackEmissions = (state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload).aer
      ?.monitoringApproachEmissions?.FALLBACK as FallbackEmissions;
    const containsBiomass = fallbackEmissions?.biomass?.contains;

    return fb.group({
      totalFossilEmissions: [
        {
          value: fallbackEmissions?.totalFossilEmissions ?? null,
          disabled: disabled,
        },
        {
          validators: [GovukValidators.required(noTotalFossilEmissions), GovukValidators.maxDecimalsValidator(5)],
        },
      ],
      totalFossilEnergyContent: [
        {
          value: fallbackEmissions?.totalFossilEnergyContent ?? null,
          disabled: disabled,
        },
        {
          validators: [GovukValidators.required(noTotalFossilEnergyContent), GovukValidators.maxDecimalsValidator(5)],
        },
      ],
      ...getAdditionalBiomassControls(fallbackEmissions, containsBiomass, disabled)?.controls,
    });
  },
};

function getAdditionalBiomassControls(
  fallbackEmissions: FallbackEmissions,
  containsBiomass: boolean,
  disabled: boolean,
) {
  const fb = new UntypedFormBuilder();

  if (!containsBiomass) {
    return fb.group({});
  }

  return fb.group({
    totalSustainableBiomassEmissions: [
      {
        value: fallbackEmissions?.biomass?.totalSustainableBiomassEmissions ?? null,
        disabled: disabled,
      },
      {
        validators: [GovukValidators.required(noTotalBiomassEmissions), GovukValidators.maxDecimalsValidator(5)],
      },
    ],
    totalEnergyContentFromBiomass: [
      {
        value: fallbackEmissions?.biomass?.totalEnergyContentFromBiomass ?? null,
        disabled: disabled,
      },
      {
        validators: [GovukValidators.required(noTotalEnergyContentBiomass), GovukValidators.maxDecimalsValidator(5)],
      },
    ],
    totalNonSustainableBiomassEmissions: [
      {
        value: fallbackEmissions?.biomass?.totalNonSustainableBiomassEmissions ?? null,
        disabled: disabled,
      },
      {
        validators: [
          GovukValidators.required(noTotalNonSustainableBiomassEmissions),
          GovukValidators.maxDecimalsValidator(5),
        ],
      },
    ],
  });
}
