import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { createAnotherRequiredChange } from '@tasks/aer/shared/components/decision/aer-review-group-decision/aer-review-group-decision-form-utils';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationReviewRequestTaskPayload, AerDataReviewDecision, ReviewDecisionRequiredChange } from 'pmrv-api';

export const AER_REVIEW_GROUP_DECISION_FORM = new InjectionToken<UntypedFormGroup>('Aer review group decision form');

export const aerReviewGroupDecisionFormProvider = {
  provide: AER_REVIEW_GROUP_DECISION_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute, RequestTaskFileService],
  useFactory: (
    fb: UntypedFormBuilder,
    store: CommonTasksStore,
    route: ActivatedRoute,
    requestTaskFileService: RequestTaskFileService,
  ) => {
    const groupKey = route.snapshot.data.groupKey;
    const state = store.getValue();

    const reviewGroupDecisions = (state.requestTaskItem.requestTask.payload as AerApplicationReviewRequestTaskPayload)
      .reviewGroupDecisions;
    const reviewDecision = reviewGroupDecisions?.[groupKey] as AerDataReviewDecision;

    return fb.group(
      {
        decision: [
          { value: reviewDecision?.type ?? null, disabled: !state.isEditable },
          { validators: [GovukValidators.required('Select a decision for this review group')] },
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
