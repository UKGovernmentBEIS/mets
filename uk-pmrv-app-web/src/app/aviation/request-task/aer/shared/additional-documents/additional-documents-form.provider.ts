import { Provider } from '@angular/core';
import { FormBuilder, FormControl } from '@angular/forms';

import { FileUpload } from '@shared/file-input/file-upload-event';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { GovukValidators } from 'govuk-components';

import { AerRequestTaskPayload, RequestTaskStore } from '../../../store';
import { TASK_FORM_PROVIDER } from '../../../task-form.provider';

export interface AdditionalDocumentsFormModel {
  exist: FormControl<boolean>;
  documents: FormControl<FileUpload[]>;
}

export const aerAdditionalDocumentsFormProvider: Provider = {
  provide: TASK_FORM_PROVIDER,
  deps: [FormBuilder, RequestTaskStore, RequestTaskFileService],
  useFactory: (fb: FormBuilder, store: RequestTaskStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getState();
    const payload = state.requestTaskItem?.requestTask?.payload as AerRequestTaskPayload;
    const additionalDocuments = payload?.aer?.additionalDocuments;

    const formGroup = fb.group({
      exist: [
        { value: additionalDocuments?.exist ?? null, disabled: !state.isEditable },
        { validators: GovukValidators.required('Select yes or no'), updateOn: 'change' },
      ],
      documents: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        additionalDocuments?.documents ?? [],
        payload?.aerAttachments,
        'AVIATION_AER_UPLOAD_SECTION_ATTACHMENT',
        true,
        !state.isEditable,
      ),
    });

    formGroup.get('exist').valueChanges.subscribe((exist) => {
      if (!exist) {
        formGroup.controls.documents.disable();
        formGroup.controls.documents.setValue([]);
      } else {
        formGroup.controls.documents.enable();
      }
    });

    return formGroup;
  },
};
