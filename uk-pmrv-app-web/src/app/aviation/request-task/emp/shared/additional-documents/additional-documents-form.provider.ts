import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { EmpRequestTaskPayload, RequestTaskStore } from '@aviation/request-task/store';
import { TaskFormProvider } from '@aviation/request-task/task-form.provider';
import { FileUpload } from '@shared/file-input/file-upload-event';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { GovukValidators } from 'govuk-components';

import { getRequestTaskAttachmentTypeForRequestTaskType } from '../../../util';

export interface AdditionalDocumentsModel {
  exist: boolean;
  documents: FileUpload[];
}

export interface AdditionalDocumentsFormModel {
  exist: FormControl<boolean>;
  documents: FormControl<FileUpload[]>;
}

@Injectable()
export class AdditionalDocumentsFormProvider
  implements TaskFormProvider<AdditionalDocumentsModel, AdditionalDocumentsFormModel>
{
  private fb = inject(FormBuilder);
  private requestTaskFileService = inject(RequestTaskFileService);
  private store = inject(RequestTaskStore);
  private _form: FormGroup;
  private destroy$ = new Subject<void>();

  get form(): FormGroup {
    if (!this._form) {
      this._buildForm();
    }

    return this._form;
  }

  get existCtrl() {
    return this.form.get('exist');
  }

  get documentsCtrl() {
    return this.form.get('documents');
  }

  destroyForm() {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  setFormValue(additionalDocuments: AdditionalDocumentsModel) {
    this.form.setValue({
      exist: additionalDocuments.exist,
      documents: additionalDocuments?.documents ?? [],
    });
  }

  getFormValue(): AdditionalDocumentsModel {
    return this.form.getRawValue();
  }

  private _buildForm() {
    const state = this.store.getState();
    const payload = state.requestTaskItem?.requestTask?.payload as EmpRequestTaskPayload;
    const additionalDocuments = payload?.emissionsMonitoringPlan?.additionalDocuments;

    this._form = this.fb.group({
      exist: [
        { value: additionalDocuments?.exist ?? null, disabled: !state.isEditable },
        {
          validators: GovukValidators.required('Select if you want to upload any additional documents or information'),
          updateOn: 'change',
        },
      ],
      documents: this.requestTaskFileService.buildFormControl(
        this.store.requestTaskId,
        additionalDocuments?.documents ?? [],
        payload?.empAttachments,
        getRequestTaskAttachmentTypeForRequestTaskType(state.requestTaskItem?.requestTask?.type),
        true,
        !state.isEditable,
      ),
    });

    this.existCtrl.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((exist) => {
      if (exist) {
        this.documentsCtrl.enable();
      } else {
        this.documentsCtrl.patchValue([]);
        this.documentsCtrl.disable();
      }
    });
  }
}
