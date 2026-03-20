import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import {
  BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload,
  BDRS2Bdrs2DataRegulatorReviewDecision,
  ReviewDecisionRequiredChange,
} from 'pmrv-api';

import { createAnotherRequiredChange } from './bdrs2-review-group-decision-form.util';

export const BDRS2_REVIEW_GROUP_DECISION_FORM = new InjectionToken<UntypedFormGroup>('Bdr review group decision form');

export const bdrs2ReviewGroupDecisionFormProvider = {
  provide: BDRS2_REVIEW_GROUP_DECISION_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute, RequestTaskFileService],
  useFactory: (
    fb: UntypedFormBuilder,
    store: CommonTasksStore,
    route: ActivatedRoute,
    requestTaskFileService: RequestTaskFileService,
  ) => {
    const groupKey = route.snapshot.data.groupKey;
    const state = store.getValue();

    const payload = state.requestTaskItem.requestTask
      .payload as BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload;
    const reviewGroupDecisions = payload.regulatorReviewGroupDecisions;
    const reviewDecision = reviewGroupDecisions?.[groupKey] as BDRS2Bdrs2DataRegulatorReviewDecision;

    return fb.group(
      {
        ...(payload?.bdrs2?.bdrs2guardQuestions?.requiresAdditionalSubInstallationSplitsForCbam
          ? {
              verificationRequired: [
                { value: reviewDecision?.details?.['verificationRequired'] ?? null, disabled: !state.isEditable },
                {
                  validators: [
                    GovukValidators.required('Select yes if the operator needs to send the amends to the verifier'),
                  ],
                },
              ],
            }
          : {}),

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
