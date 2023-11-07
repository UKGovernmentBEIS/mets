import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload, TransferCO2 } from 'pmrv-api';

import { AER_MEASUREMENT_FORM } from '../measurement-status';

export const transferredFormProvider = {
  provide: AER_MEASUREMENT_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const taskKey = route.snapshot.data.taskKey;
    const state = store.getValue();
    const disabled = !state.isEditable;

    const payload: AerApplicationSubmitRequestTaskPayload = state.requestTaskItem.requestTask.payload;

    const emissionPointEmission = route.snapshot.paramMap.get('index')
      ? (payload.aer?.monitoringApproachEmissions[taskKey] as any)?.emissionPointEmissions?.[
          Number(route.snapshot.paramMap.get('index'))
        ]
      : null;

    const transferredCo2 = emissionPointEmission?.transfer as TransferCO2;

    return fb.group({
      entryAccountingForTransfer: [
        {
          value: transferredCo2?.entryAccountingForTransfer ?? null,
          disabled,
        },
        { validators: GovukValidators.required('Select a category') },
      ],
      transferDirection: [
        {
          value: transferredCo2?.transferDirection ?? null,
          disabled,
        },
        { validators: GovukValidators.required('Select a category') },
      ],
    });
  },
};
