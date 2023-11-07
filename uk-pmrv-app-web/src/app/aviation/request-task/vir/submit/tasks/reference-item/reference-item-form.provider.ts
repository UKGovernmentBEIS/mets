import { Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TaskFormProvider } from '@aviation/request-task/task-form.provider';
import { getRequestTaskAttachmentTypeForRequestTaskType } from '@aviation/request-task/util';
import { FileUpload } from '@shared/file-input/file-upload-event';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { GovukValidators } from 'govuk-components';

import { OperatorImprovementResponse } from 'pmrv-api';

export interface ReferenceItemFormModel {
  isAddressed: FormControl<boolean | null>;
  addressedDescription: FormControl<string | null>;
  addressedDate: FormControl<string | null>;
  addressedDescription2: FormControl<string | null>;
  uploadEvidence: FormControl<boolean | null>;
  files: FormControl<FileUpload[]>;
}

@Injectable()
export class ReferenceItemFormProvider
  implements TaskFormProvider<OperatorImprovementResponse, ReferenceItemFormModel>
{
  private _form: FormGroup<ReferenceItemFormModel>;
  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private requestTaskFileService: RequestTaskFileService,
    private store: RequestTaskStore,
  ) {}

  destroyForm(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  get form(): FormGroup<ReferenceItemFormModel> {
    if (!this._form) {
      this.buildForm();
    }

    return this._form;
  }

  getFormValue(): OperatorImprovementResponse {
    return {
      isAddressed: this.form.get('isAddressed').value,
      addressedDescription: this.form.get('isAddressed').value
        ? this.form.get('addressedDescription').value
        : this.form.get('addressedDescription2').value,
      addressedDate: this.form.get('addressedDate').value,
      uploadEvidence: this.form.get('uploadEvidence').value,
      files: (this.form.get('files').value as Array<any>)?.map((a) => a.uuid) ?? [],
    };
  }

  get isAddressedCtrl(): FormControl {
    return this.form.get('isAddressed') as FormControl;
  }

  get addressedDescriptionCtrl(): FormControl {
    return this.form.get('addressedDescription') as FormControl;
  }

  get addressedDateCtrl(): FormControl {
    return this.form.get('addressedDate') as FormControl;
  }

  get addressedDescription2Ctrl(): FormControl {
    return this.form.get('addressedDescription2') as FormControl;
  }

  get uploadEvidenceCtrl(): FormControl {
    return this.form.get('uploadEvidence') as FormControl;
  }

  get filesCtrl(): FormControl {
    return this.form.get('files') as FormControl;
  }

  setFormValue(formValue: OperatorImprovementResponse): void {
    this.form.setValue({
      isAddressed: formValue?.isAddressed ?? null,
      addressedDescription: formValue?.isAddressed ? formValue?.addressedDescription ?? null : null,
      addressedDate: formValue?.isAddressed
        ? formValue?.addressedDate
          ? (new Date(formValue.addressedDate) as any)
          : null
        : null,
      addressedDescription2: !formValue?.isAddressed ? formValue?.addressedDescription ?? null : null,
      uploadEvidence: formValue?.uploadEvidence ?? null,
      files: formValue?.uploadEvidence
        ? formValue.files?.map((uuid) => ({
            file: { name: this.store.virDelegate.payload.virAttachments[uuid] } as File,
            uuid,
          })) ?? []
        : [],
    });
  }

  private buildForm() {
    this._form = this.fb.group(
      {
        isAddressed: new FormControl<boolean>(null, {
          validators: [
            GovukValidators.required('Select if you have addressed this recommendation or plan to in the future'),
          ],
        }),
        addressedDescription: new FormControl<string>(null, {
          validators: [
            GovukValidators.required('State how the recommendation will be addressed'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        }),
        addressedDate: new FormControl<string>(null, {}),
        addressedDescription2: new FormControl<string>(null, {
          validators: [
            GovukValidators.required('State why you have not addressed this recommendation'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        }),
        uploadEvidence: new FormControl<boolean>(null, {
          validators: [GovukValidators.required('Select yes or no')],
        }),
        files: this.requestTaskFileService.buildFormControl(
          this.store.requestTaskId,
          [],
          this.store.virDelegate.payload.virAttachments,
          getRequestTaskAttachmentTypeForRequestTaskType(this.store.getState().requestTaskItem?.requestTask?.type),
          true,
          !this.store.getState().isEditable,
        ) as FormControl<FileUpload[]>,
      },
      { updateOn: 'change' },
    );

    this._form
      .get('isAddressed')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        if (value) {
          this._form.get('addressedDescription').enable();
          this._form.get('addressedDescription2').disable();
          this._form.get('addressedDescription2').setValue(null);
        } else {
          this._form.get('addressedDescription2').enable();
          this._form.get('addressedDescription').disable();
          this._form.get('addressedDescription').setValue(null);
        }
      });

    this._form
      .get('uploadEvidence')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        if (value) {
          this._form.get('files').enable();
        } else {
          this._form.get('files').disable();
        }
      });
  }
}
