import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { AIR_TASK_FORM } from '@tasks/air/shared/air-task-form.token';
import { maxLength, noComment } from '@tasks/air/shared/errors/validation-errors';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AirApplicationReviewRequestTaskPayload } from 'pmrv-api';

export const provideSummaryFormProvider = {
  provide: AIR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.value;
    const disabled = !state.isEditable;
    const regulatorReviewResponse = (
      state.requestTaskItem.requestTask.payload as AirApplicationReviewRequestTaskPayload
    )?.regulatorReviewResponse;

    return fb.group({
      reportSummary: [
        { value: regulatorReviewResponse?.reportSummary ?? null, disabled },
        { validators: [GovukValidators.required(noComment), GovukValidators.maxLength(10000, maxLength)] },
      ],
    });
  },
};
