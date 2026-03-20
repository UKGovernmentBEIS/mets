import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { BDRS2_TASK_FORM } from '@tasks/bdrs2/core';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

export const bdrs2InstallationSectorFormProvider = {
  provide: BDRS2_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const statePayload = state.requestTaskItem.requestTask
      .payload as BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload;
    const outcome = statePayload?.regulatorReviewOutcome;

    return fb.group({
      installationSectorOpinion: [
        {
          value: outcome?.installationSectorOpinion ?? null,
          disabled,
        },
        {
          updateOn: 'change',
          validators: [GovukValidators.required('Select your opinion on the installation sector')],
        },
      ],
      operatorNotes: [
        {
          value: outcome?.installationSectorReviewNotes?.operatorNotes ?? null,
          disabled,
        },
        {
          updateOn: 'change',
        },
      ],
      internalNotes: [
        {
          value: outcome?.installationSectorReviewNotes?.internalNotes ?? null,
          disabled,
        },
        {
          updateOn: 'change',
        },
      ],
    });
  },
};
