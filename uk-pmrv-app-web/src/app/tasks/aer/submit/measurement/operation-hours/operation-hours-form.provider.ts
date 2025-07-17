import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { AER_MEASUREMENT_FORM } from '../measurement-status';

export const operationHoursFormProvider = {
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

    return fb.group({
      operationalHours: [
        {
          value: emissionPointEmission?.operationalHours ?? null,
          disabled: disabled,
        },
        {
          validators: [GovukValidators.required('Enter a value'), GovukValidators.maxIntegersValidator(12)],
        },
      ],
    });
  },
};
