import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationReviewRequestTaskPayload, AerVerificationReportDataReviewDecision } from 'pmrv-api';

export const VERIFICATION_REVIEW_GROUP_DECISION_FORM = new InjectionToken<UntypedFormGroup>(
  'Verification review group decision form',
);

export const verificationReviewGroupDecisionFormProvider = {
  provide: VERIFICATION_REVIEW_GROUP_DECISION_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const groupKey = route.snapshot.data.groupKey;
    const state = store.getValue();

    const reviewGroupDecisions = (state.requestTaskItem.requestTask.payload as AerApplicationReviewRequestTaskPayload)
      .reviewGroupDecisions;
    const reviewDecision = reviewGroupDecisions?.[groupKey] as AerVerificationReportDataReviewDecision;

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
      },
      {
        updateOn: 'change',
      },
    );
  },
};
