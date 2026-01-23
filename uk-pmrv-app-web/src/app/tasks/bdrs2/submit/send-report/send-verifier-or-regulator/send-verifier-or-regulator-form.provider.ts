import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { BDRS2_TASK_FORM } from '@tasks/bdrs2/core/bdrs2-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

export const bdrs2SendVerifierOrRegulatorFormProvider = {
  provide: BDRS2_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const sendTo = route.snapshot.queryParamMap.get('sendTo');

    return fb.group({
      needsVerification: [
        { value: sendTo ? (sendTo === 'verifier' ? true : false) : null, disabled },
        {
          updateOn: 'change',
          validators: [GovukValidators.required('Select yes if you want to send this report to a verifier')],
        },
      ],
    });
  },
};
