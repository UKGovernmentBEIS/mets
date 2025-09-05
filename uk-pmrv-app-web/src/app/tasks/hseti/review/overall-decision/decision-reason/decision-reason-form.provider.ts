import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { HSE_TI_TASK_FORM } from '@tasks/hseti/core';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { HSETIApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

export const decisionReasonFormProvider = {
  provide: HSE_TI_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const payload = state.requestTaskItem.requestTask
      .payload as HSETIApplicationRegulatorReviewSubmitRequestTaskPayload;

    return fb.group({
      reason: [
        {
          value: payload?.overallDecision?.reason ?? null,
          disabled,
        },
      ],
    });
  },
};
