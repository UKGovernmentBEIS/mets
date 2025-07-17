import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload, CalculationOfPfcEmissions } from 'pmrv-api';

import { AER_PFC_FORM } from '../pfc';

export const primaryAluminiumFormProvider = {
  provide: AER_PFC_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const state = store.getValue();

    const disabled = !state.isEditable;

    const payload = state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload;

    const sourceStreamEmission = route.snapshot.paramMap.get('index')
      ? (payload.aer.monitoringApproachEmissions.CALCULATION_PFC as CalculationOfPfcEmissions)?.sourceStreamEmissions[
          Number(route.snapshot.paramMap.get('index'))
        ]
      : null;

    return fb.group({
      totalPrimaryAluminium: [
        { value: sourceStreamEmission?.totalPrimaryAluminium ?? null, disabled: disabled },
        {
          validators: [GovukValidators.required('Enter a value'), GovukValidators.maxIntegersValidator(12)],
        },
      ],
    });
  },
};
