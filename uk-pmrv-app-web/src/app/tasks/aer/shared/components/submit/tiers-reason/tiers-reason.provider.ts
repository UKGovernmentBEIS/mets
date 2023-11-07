import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { AER_APPROACHES_FORM } from '../emissions';

export const tiersReasonFormProvider = {
  provide: AER_APPROACHES_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const taskKey = route.snapshot.data.taskKey;
    const state = store.getValue();
    const disabled = !state.isEditable;

    const payload: AerApplicationSubmitRequestTaskPayload = state.requestTaskItem.requestTask.payload;

    const sourceStreamEmission = route.snapshot.paramMap.get('index')
      ? (payload.aer.monitoringApproachEmissions[taskKey] as any)?.sourceStreamEmissions[
          Number(route.snapshot.paramMap.get('index'))
        ]
      : null;

    const type = sourceStreamEmission?.parameterMonitoringTierDiffReason?.type;
    const reason = sourceStreamEmission?.parameterMonitoringTierDiffReason?.reason;
    const relatedNotifications = sourceStreamEmission?.parameterMonitoringTierDiffReason?.relatedNotifications;

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
        { value: type === 'DATA_GAP' ? reason ?? null : null, disabled },
        {
          validators: GovukValidators.required(
            'You must say why you could not use the tiers from your monitoring plan during the data gap',
          ),
        },
      ],
      reasonOther: [
        { value: type === 'OTHER' ? reason ?? null : null, disabled },
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
