import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ValidatorFn, Validators } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TaskFormProvider } from '@aviation/request-task/task-form.provider';
import { FileUpload } from '@shared/file-input/file-upload-event';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { cloneDeep } from 'lodash-es';

import { GovukValidators } from 'govuk-components';

import { AviationAerCorsiaEmissionsReductionClaim } from 'pmrv-api';

export interface EmissionsReductionClaimDetailsFormModel {
  cefFiles: FormControl<FileUpload[]>;
  totalEmissions: FormControl<string>;
  noDoubleCountingDeclarationFiles: FormControl<FileUpload[]>;
}

export interface EmissionsReductionClaimFormModel {
  exist: FormControl<boolean | null>;
  emissionsReductionClaimDetails?: FormGroup<EmissionsReductionClaimDetailsFormModel>;
}

@Injectable()
export class EmissionsReductionClaimFormProvider
  implements TaskFormProvider<AviationAerCorsiaEmissionsReductionClaim, EmissionsReductionClaimFormModel>
{
  private fb = inject(FormBuilder);
  private _form: FormGroup;
  private store = inject(RequestTaskStore);
  private requestTaskFileService = inject(RequestTaskFileService);
  private destroy$ = new Subject<void>();

  get form(): FormGroup {
    if (!this._form) {
      this.buildForm();
    }
    return this._form;
  }

  destroyForm() {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  setFormValue(emissionsReductionClaim: AviationAerCorsiaEmissionsReductionClaim | undefined): void {
    const value: any = cloneDeep(emissionsReductionClaim);

    if (value) {
      this.form.get('exist').patchValue(value.exist);
      if (value.emissionsReductionClaimDetails?.totalEmissions) {
        (this.form.get('emissionsReductionClaimDetails') as FormGroup)
          .get('totalEmissions')
          .patchValue(value.emissionsReductionClaimDetails.totalEmissions);
      }

      if (value.emissionsReductionClaimDetails?.cefFiles) {
        (this.form.get('emissionsReductionClaimDetails') as FormGroup)?.get('cefFiles').patchValue(
          value.emissionsReductionClaimDetails.cefFiles?.map((uuid) => ({
            file: { name: this.store.aerDelegate.payload.aerAttachments[uuid] } as File,
            uuid,
          })) ?? [],
        );
      }

      if (value.emissionsReductionClaimDetails?.noDoubleCountingDeclarationFiles) {
        (this.form.get('emissionsReductionClaimDetails') as FormGroup)
          ?.get('noDoubleCountingDeclarationFiles')
          .patchValue(
            value.emissionsReductionClaimDetails.noDoubleCountingDeclarationFiles?.map((uuid) => ({
              file: { name: this.store.aerDelegate.payload.aerAttachments[uuid] } as File,
              uuid,
            })) ?? [],
          );
      }

      this.form.get('emissionsReductionClaimDetails')?.updateValueAndValidity();
    }
  }

  getFormValue(): AviationAerCorsiaEmissionsReductionClaim {
    const result = cloneDeep(this.form.value);

    if (!this.form.value.exist) {
      return { ...this.form.value };
    }
    return {
      ...this.form.value,
      emissionsReductionClaimDetails: {
        ...this.form?.value?.emissionsReductionClaimDetails,
        cefFiles: result.emissionsReductionClaimDetails.cefFiles?.map((x) => x.uuid),
        noDoubleCountingDeclarationFiles: result.emissionsReductionClaimDetails.noDoubleCountingDeclarationFiles?.map(
          (x) => x.uuid,
        ),
      },
    } as AviationAerCorsiaEmissionsReductionClaim;
  }

  get existCtrl(): FormControl {
    return this.form.get('exist') as FormControl;
  }

  get emissionsReductionClaimDetailsCtrl(): FormGroup {
    return this.form.get('emissionsReductionClaimDetails') as FormGroup;
  }

  get declarationFileCtlr(): FormGroup {
    return this.emissionsReductionClaimDetailsCtrl.get('noDoubleCountingDeclarationFiles') as FormGroup;
  }

  get cefFilesCtlr(): FormControl {
    return this.emissionsReductionClaimDetailsCtrl.get('cefFiles') as FormControl;
  }

  get totalEmissionsCtlr(): FormControl {
    return this.emissionsReductionClaimDetailsCtrl.get('totalEmissions') as FormControl;
  }

  buildForm() {
    this._form = this.fb.group(
      {
        exist: new FormControl<boolean | null>(null, {
          validators: [GovukValidators.required('Select yes or no')],
          updateOn: 'change',
        }),
      },
      { updateOn: 'change' },
    );

    this.form
      .get('exist')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((exist) => {
        if (exist === true) {
          this.addEmissionsReductionClaimDetails();
        } else {
          this.removeEmissionsReductionClaimDetails();
        }
        this.form.updateValueAndValidity();
      });
  }

  addEmissionsReductionClaimDetails() {
    if (!this.form.contains('emissionsReductionClaimDetails')) {
      this.form.addControl(
        'emissionsReductionClaimDetails',
        this.createEmissionsReductionClaimDetailsForm([Validators.required]),
      );
    }
  }

  removeEmissionsReductionClaimDetails() {
    if (this.form.contains('emissionsReductionClaimDetails')) {
      this.form.removeControl('emissionsReductionClaimDetails');
    }
  }

  createEmissionsReductionClaimDetailsForm(
    validators: ValidatorFn[] = [],
  ): FormGroup<EmissionsReductionClaimDetailsFormModel> {
    const formgroup = this.fb.group<EmissionsReductionClaimDetailsFormModel>(
      {
        cefFiles: this.requestTaskFileService.buildFormControl(
          this.store.requestTaskId,
          [],
          this.store.aerDelegate.payload.aerAttachments,
          'AVIATION_AER_UPLOAD_SECTION_ATTACHMENT',
          true,
          !this.store.getState().isEditable,
        ) as FormControl<FileUpload[]>,

        totalEmissions: new FormControl(null as string, [
          GovukValidators.required('Enter the total emissions reduction claimed'),
          GovukValidators.positiveNumber('You must enter a positive number'),
          GovukValidators.maxDecimalsValidator(3),
        ]),

        noDoubleCountingDeclarationFiles: this.requestTaskFileService.buildFormControl(
          this.store.requestTaskId,
          [],
          this.store.aerDelegate.payload.aerAttachments,
          'AVIATION_AER_UPLOAD_SECTION_ATTACHMENT',
          true,
          !this.store.getState().isEditable,
        ) as FormControl<FileUpload[]>,
      },
      { updateOn: 'change', validators },
    );
    return formgroup;
  }
}
