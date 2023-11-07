import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { NonComplianceApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../../../../tasks/store/common-tasks.store';
import { NON_COMPLIANCE_TASK_FORM } from '../../../core/non-compliance-form.token';

export const chooseWorkflowAddFormProvider = {
  provide: NON_COMPLIANCE_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const index = route.snapshot.paramMap.get('index');

    const selectedRequests = (
      state.requestTaskItem.requestTask.payload as NonComplianceApplicationSubmitRequestTaskPayload
    )?.selectedRequests;

    return fb.group({
      selectedRequests: [
        { value: index === null ? null : selectedRequests[Number(index)], disabled },
        [GovukValidators.required('You must add at least one item')],
      ],
    });
  },
};
