import { UntypedFormBuilder } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { noSelection, noSourceStream } from '@tasks/aer/submit/fallback/errors/fallback-validation.errors';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload, FallbackEmissions } from 'pmrv-api';

export const fallbackFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const aer = (state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload).aer;
    const fallbackEmissions = aer.monitoringApproachEmissions?.FALLBACK as FallbackEmissions;

    return fb.group({
      sourceStreams: [
        {
          value: fallbackEmissions?.sourceStreams
            ? fallbackEmissions.sourceStreams.filter((source) =>
                aer.sourceStreams.some((stateSource) => stateSource.id === source),
              )
            : null,
          disabled,
        },
        { validators: GovukValidators.required(noSourceStream) },
      ],
      contains: [
        {
          value: fallbackEmissions?.biomass?.contains ?? null,
          disabled,
        },
        { validators: GovukValidators.required(noSelection) },
      ],
    });
  },
};
