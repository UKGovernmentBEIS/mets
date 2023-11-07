import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { AIR_TASK_FORM } from '@tasks/air/shared/air-task-form.token';
import { maxLength, noComment, noSelection } from '@tasks/air/shared/errors/validation-errors';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AirApplicationRespondToRegulatorCommentsRequestTaskPayload } from 'pmrv-api';

export const operatorFollowupFormProvider = {
  provide: AIR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService, ActivatedRoute],
  useFactory: (
    fb: UntypedFormBuilder,
    store: CommonTasksStore,
    requestTaskFileService: RequestTaskFileService,
    route: ActivatedRoute,
  ) => {
    const state = store.value;
    const disabled = !state.isEditable;
    const id = route.snapshot.paramMap.get('id');
    const operatorImprovementFollowUpResponse = (
      state.requestTaskItem.requestTask.payload as AirApplicationRespondToRegulatorCommentsRequestTaskPayload
    )?.operatorImprovementFollowUpResponses?.[id];
    const statePayload = state.requestTaskItem.requestTask
      .payload as AirApplicationRespondToRegulatorCommentsRequestTaskPayload;
    const airAttachments = statePayload?.airAttachments;

    return fb.group({
      improvementCompleted: [
        { value: operatorImprovementFollowUpResponse?.improvementCompleted ?? null, disabled },
        { validators: [GovukValidators.required(noSelection)] },
      ],
      dateCompleted: [
        {
          value: operatorImprovementFollowUpResponse?.dateCompleted
            ? new Date(operatorImprovementFollowUpResponse.dateCompleted)
            : null,
          disabled,
        },
      ],
      reason: [
        { value: operatorImprovementFollowUpResponse?.reason ?? null, disabled },
        { validators: [GovukValidators.required(noComment), GovukValidators.maxLength(10000, maxLength)] },
      ],
      files: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        operatorImprovementFollowUpResponse?.files ?? [],
        airAttachments,
        'AIR_UPLOAD_ATTACHMENT',
        false,
        disabled,
      ),
    });
  },
};
