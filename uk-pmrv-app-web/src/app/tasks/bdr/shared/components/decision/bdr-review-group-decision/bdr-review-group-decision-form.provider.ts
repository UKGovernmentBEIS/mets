import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import {
  BDRApplicationRegulatorReviewSubmitRequestTaskPayload,
  BDRBdrDataRegulatorReviewDecision,
  ReviewDecisionRequiredChange,
} from 'pmrv-api';

import { createAnotherRequiredChange } from './bdr-review-group-decision-form.util';

export const BDR_REVIEW_GROUP_DECISION_FORM = new InjectionToken<UntypedFormGroup>('Bdr review group decision form');

export const bdrReviewGroupDecisionFormProvider = {
  provide: BDR_REVIEW_GROUP_DECISION_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute, RequestTaskFileService],
  useFactory: (
    fb: UntypedFormBuilder,
    store: CommonTasksStore,
    route: ActivatedRoute,
    requestTaskFileService: RequestTaskFileService,
  ) => {
    const groupKey = route.snapshot.data.groupKey;
    const state = store.getValue();

    const payload = state.requestTaskItem.requestTask.payload as BDRApplicationRegulatorReviewSubmitRequestTaskPayload;
    const reviewGroupDecisions = payload.regulatorReviewGroupDecisions;
    const reviewDecision = reviewGroupDecisions?.[groupKey] as BDRBdrDataRegulatorReviewDecision;

    return fb.group(
      {
        ...(payload?.bdr?.isApplicationForFreeAllocation
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
