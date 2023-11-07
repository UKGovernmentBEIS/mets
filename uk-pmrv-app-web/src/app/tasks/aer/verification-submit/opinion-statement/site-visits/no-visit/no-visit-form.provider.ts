import { UntypedFormBuilder } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import {
  maxDescription,
  noSiteVisitReason,
} from '@tasks/aer/verification-submit/opinion-statement/errors/opinion-statement-validation.errors';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationVerificationSubmitRequestTaskPayload, NoSiteVisit } from 'pmrv-api';

export const noVisitFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const opinionStatement = (
      state.requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload
    ).verificationReport.opinionStatement;
    const noSiteVisit = opinionStatement?.siteVisit as NoSiteVisit;
    return fb.group({
      reason: [
        { value: noSiteVisit?.reason ?? null, disabled: !state.isEditable },
        {
          validators: [GovukValidators.required(noSiteVisitReason), GovukValidators.maxLength(10000, maxDescription)],
        },
      ],
    });
  },
};
