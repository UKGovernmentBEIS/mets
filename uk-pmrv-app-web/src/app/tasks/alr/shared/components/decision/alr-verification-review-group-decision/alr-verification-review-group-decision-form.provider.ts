import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { ALRAlrDataRegulatorReviewDecision, ALRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

export const ALR_REVIEW_VERIFICATION_REVIEW_DECISION_FORM = new InjectionToken<UntypedFormGroup>(
  'Alr review verification group decision form',
);

export const alrVerificationReviewGroupDecisionFormProvider = {
  provide: ALR_REVIEW_VERIFICATION_REVIEW_DECISION_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute, RequestTaskFileService],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const groupKey = route.snapshot.data.groupKey;
    const state = store.getValue();
    const payload = state.requestTaskItem.requestTask.payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload;
    const reviewGroupDecisions = payload.regulatorReviewGroupDecisions;
    const reviewDecision = reviewGroupDecisions?.[groupKey] as ALRAlrDataRegulatorReviewDecision;
    return fb.group(
      {
        decision: [
          { value: reviewDecision?.type ?? null, disabled: !state.isEditable },
          { validators: [GovukValidators.required('Select your decision')] },
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
