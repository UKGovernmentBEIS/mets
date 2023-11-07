import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { AIR_TASK_FORM } from '@tasks/air/shared/air-task-form.token';
import { maxLength, noComment, noSelection } from '@tasks/air/shared/errors/validation-errors';
import { futureDateValidator } from '@tasks/air/shared/validators/future-date.validator';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AirApplicationReviewRequestTaskPayload } from 'pmrv-api';

export const improvementResponseReviewFormProvider = {
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
    const regulatorImprovementResponse = (
      state.requestTaskItem.requestTask.payload as AirApplicationReviewRequestTaskPayload
    )?.regulatorReviewResponse?.regulatorImprovementResponses?.[id];
    const statePayload = state.requestTaskItem.requestTask.payload as AirApplicationReviewRequestTaskPayload;
    const reviewAttachments = statePayload?.reviewAttachments;

    return fb.group({
      improvementRequired: [
        { value: regulatorImprovementResponse?.improvementRequired ?? null, disabled },
        { validators: [GovukValidators.required(noSelection)] },
      ],
      improvementDeadline: [
        {
          value: regulatorImprovementResponse?.improvementDeadline
            ? new Date(regulatorImprovementResponse.improvementDeadline)
            : null,
          disabled,
        },
        { validators: [futureDateValidator()] },
      ],
      officialResponse: [
        { value: regulatorImprovementResponse?.officialResponse ?? null, disabled },
        { validators: [GovukValidators.required(noComment), GovukValidators.maxLength(10000, maxLength)] },
      ],
      comments: [
        { value: regulatorImprovementResponse?.comments ?? null, disabled },
        { validators: [GovukValidators.required(noComment), GovukValidators.maxLength(10000, maxLength)] },
      ],
      files: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        regulatorImprovementResponse?.files ?? [],
        reviewAttachments,
        'AIR_REVIEW_UPLOAD_ATTACHMENT',
        false,
        disabled,
      ),
    });
  },
};
