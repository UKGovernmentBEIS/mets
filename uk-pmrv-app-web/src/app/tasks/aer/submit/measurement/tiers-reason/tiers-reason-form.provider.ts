import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { AER_MEASUREMENT_FORM } from '../measurement-status';

export const tiersReasonFormProvider = {
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

    const type = emissionPointEmission?.parameterMonitoringTierDiffReason?.type;
    const reason = emissionPointEmission?.parameterMonitoringTierDiffReason?.reason;
    const relatedNotifications = emissionPointEmission?.parameterMonitoringTierDiffReason?.relatedNotifications;

    return fb.group({
      type: [
        { value: type ?? null, disabled },
        {
          validators: GovukValidators.required(
            'You must say why you could not use the tiers from your monitoring plan',
          ),
        },
      ],
      reasonDataGap: [
        { value: type === 'DATA_GAP' ? (reason ?? null) : null, disabled },
        {
          validators: GovukValidators.required(
            'You must say why you could not use the tiers from your monitoring plan during the data gap',
          ),
        },
      ],
      reasonOther: [
        { value: type === 'OTHER' ? (reason ?? null) : null, disabled },
        {
          validators: GovukValidators.required(
            'You must say why you could not use the tiers from your monitoring plan',
          ),
        },
      ],
      relatedNotifications: [{ value: relatedNotifications ?? null, disabled }],
    });
  },
};
