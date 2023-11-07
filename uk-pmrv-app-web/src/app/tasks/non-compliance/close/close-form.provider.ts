import { UntypedFormBuilder } from '@angular/forms';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { NonComplianceApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { NON_COMPLIANCE_TASK_FORM } from '../core/non-compliance-form.token';

export const closeFormProvider = {
  provide: NON_COMPLIANCE_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    return fb.group({
      reason: [
        null as string,
        [
          GovukValidators.required('You must give an explanation'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],

      files: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        [],
        (store.getValue()?.requestTaskItem?.requestTask?.payload as NonComplianceApplicationSubmitRequestTaskPayload)
          ?.nonComplianceAttachments,
        'NON_COMPLIANCE_UPLOAD_ATTACHMENT',
        false,
      ),
    });
  },
};
