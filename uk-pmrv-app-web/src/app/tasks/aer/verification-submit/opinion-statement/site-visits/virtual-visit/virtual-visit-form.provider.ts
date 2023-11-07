import { UntypedFormBuilder } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import {
  maxDescription,
  noTeamMembersDescription,
  noVirtualVisitReason,
} from '@tasks/aer/verification-submit/opinion-statement/errors/opinion-statement-validation.errors';
import { createVisitDatesArray } from '@tasks/aer/verification-submit/opinion-statement/utils/visit-dates.util';
import { duplicatedDateValidator } from '@tasks/aer/verification-submit/opinion-statement/validators/duplicated-date.validator';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationVerificationSubmitRequestTaskPayload, VirtualSiteVisit } from 'pmrv-api';

export const virtualVisitFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const opinionStatement = (
      state.requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload
    ).verificationReport.opinionStatement;
    const virtualSiteVisit = opinionStatement?.siteVisit as VirtualSiteVisit;
    return fb.group(
      {
        visitDates: createVisitDatesArray(state.isEditable, virtualSiteVisit),
        reason: [
          { value: virtualSiteVisit?.reason ?? null, disabled: !state.isEditable },
          {
            validators: [
              GovukValidators.required(noVirtualVisitReason),
              GovukValidators.maxLength(10000, maxDescription),
            ],
          },
        ],
        teamMembers: [
          { value: virtualSiteVisit?.teamMembers ?? null, disabled: !state.isEditable },
          {
            validators: [
              GovukValidators.required(noTeamMembersDescription),
              GovukValidators.maxLength(10000, maxDescription),
            ],
          },
        ],
      },
      {
        validators: duplicatedDateValidator('visitDates'),
      },
    );
  },
};
