import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { VIR_TASK_FORM } from '@tasks/vir/core/vir-task-form.token';
import { maxLength, noReportSummary } from '@tasks/vir/errors/validation-errors';

import { GovukValidators } from 'govuk-components';

import { VirApplicationReviewRequestTaskPayload } from 'pmrv-api';

export const createSummaryFormProvider = {
  provide: VIR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.value;
    const disabled = !state.isEditable;
    const regulatorReviewResponse = (
      state.requestTaskItem.requestTask.payload as VirApplicationReviewRequestTaskPayload
    )?.regulatorReviewResponse;

    return fb.group({
      reportSummary: [
        { value: regulatorReviewResponse?.reportSummary ?? null, disabled },
        { validators: [GovukValidators.required(noReportSummary), GovukValidators.maxLength(10000, maxLength)] },
      ],
    });
  },
};
