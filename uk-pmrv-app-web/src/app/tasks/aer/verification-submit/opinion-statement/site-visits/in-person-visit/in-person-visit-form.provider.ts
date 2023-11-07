import { UntypedFormBuilder } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import {
  maxDescription,
  noTeamMembersDescription,
} from '@tasks/aer/verification-submit/opinion-statement/errors/opinion-statement-validation.errors';
import { createVisitDatesArray } from '@tasks/aer/verification-submit/opinion-statement/utils/visit-dates.util';
import { duplicatedDateValidator } from '@tasks/aer/verification-submit/opinion-statement/validators/duplicated-date.validator';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationVerificationSubmitRequestTaskPayload, InPersonSiteVisit } from 'pmrv-api';

export const inPersonVisitFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const opinionStatement = (
      state.requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload
    ).verificationReport.opinionStatement;
    const inPersonSiteVisit = opinionStatement?.siteVisit as InPersonSiteVisit;
    return fb.group(
      {
        teamMembers: [
          { value: inPersonSiteVisit?.teamMembers ?? null, disabled: !state.isEditable },
          {
            validators: [
              GovukValidators.required(noTeamMembersDescription),
              GovukValidators.maxLength(10000, maxDescription),
            ],
          },
        ],
        visitDates: createVisitDatesArray(state.isEditable, inPersonSiteVisit),
      },
      {
        validators: duplicatedDateValidator('visitDates'),
      },
    );
  },
};
