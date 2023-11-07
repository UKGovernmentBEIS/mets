import { Provider } from '@angular/core';
import { FormBuilder, FormControl } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { EmpRequestTaskPayload, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { FileUpload } from '@shared/file-input/file-upload-event';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { GovukValidators } from 'govuk-components';

import { getRequestTaskAttachmentTypeForRequestTaskType } from '../../../util';

export interface AdditionalDocumentsFormModel {
  exist: FormControl<boolean>;
  documents: FormControl<FileUpload[]>;
}

export const additionalDocumentsFormProvider: Provider = {
  provide: TASK_FORM_PROVIDER,
  deps: [FormBuilder, RequestTaskStore, RequestTaskFileService],
  useFactory: (fb: FormBuilder, store: RequestTaskStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getState();
    const payload = state.requestTaskItem?.requestTask?.payload as EmpRequestTaskPayload;
    const additionalDocuments = payload?.emissionsMonitoringPlan?.additionalDocuments;
    const destroy$ = new Subject<void>();

    const form = fb.group({
      exist: [
        { value: additionalDocuments?.exist ?? null, disabled: !state.isEditable },
        {
          validators: GovukValidators.required('Select if you want to upload any additional documents or information'),
          updateOn: 'change',
        },
      ],
      documents: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        additionalDocuments?.documents ?? [],
        payload?.empAttachments,
        getRequestTaskAttachmentTypeForRequestTaskType(state.requestTaskItem?.requestTask?.type),
        true,
        !state.isEditable,
      ),
    });

    form
      .get('exist')
      .valueChanges.pipe(takeUntil(destroy$))
      .subscribe((exist) => {
        if (exist) form.get('documents').enable();
        else form.get('documents').disable();
      });

    return form;
  },
};
