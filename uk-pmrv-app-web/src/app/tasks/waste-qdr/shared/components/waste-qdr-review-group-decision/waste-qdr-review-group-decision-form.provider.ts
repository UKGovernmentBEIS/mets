import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { ReviewDecisionRequiredChange, WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

import { createAnotherRequiredChange } from './waste-qdr-review-group-decision-form.utils';

export const WASTE_QDR_REVIEW_GROUP_DECISION_FORM = new InjectionToken<UntypedFormGroup>(
  'Waste QDR review group decision form',
);

export const wasteQDRReviewGroupDecisionFormProvider = {
  provide: WASTE_QDR_REVIEW_GROUP_DECISION_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute, RequestTaskFileService],
  useFactory: (
    fb: UntypedFormBuilder,
    store: CommonTasksStore,
    route: ActivatedRoute,
    requestTaskFileService: RequestTaskFileService,
  ) => {
    const state = store.getValue();

    const payload = state.requestTaskItem.requestTask
      .payload as WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload;
    const reviewDecision = payload.reviewDecision;

    return fb.group(
      {
        decision: [
          { value: reviewDecision?.type ?? null, disabled: !state.isEditable },
          { validators: [GovukValidators.required('Select a decision')] },
        ],
        notes: [
          { value: reviewDecision?.details?.notes ?? null, disabled: !state.isEditable },
          { validators: [GovukValidators.maxLength(10000, 'Enter up to 10000 characters')] },
        ],
        requiredChanges: fb.array(
          (reviewDecision?.details as { requiredChanges: ReviewDecisionRequiredChange[] })?.requiredChanges?.map(
            (requiredChange) => createAnotherRequiredChange(store, requestTaskFileService, requiredChange),
          ) ?? [createAnotherRequiredChange(store, requestTaskFileService, null)],
        ),
      },
      {
        updateOn: 'change',
      },
    );
  },
};
