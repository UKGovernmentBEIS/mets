import { UntypedFormBuilder } from '@angular/forms';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const additionalDocumentsFormFactory = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getValue();
    const payload = state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload;
    const additionalDocuments = payload.aer?.additionalDocuments;

    return fb.group({
      exist: [
        { value: additionalDocuments?.exist ?? null, disabled: !store.getValue().isEditable },
        { validators: GovukValidators.required('Select yes or no'), updateOn: 'change' },
      ],
      documents: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        additionalDocuments?.documents ?? [],
        payload?.aerAttachments,
        'AER_UPLOAD_SECTION_ATTACHMENT',
        true,
        !state.isEditable,
      ),
    });
  },
};
