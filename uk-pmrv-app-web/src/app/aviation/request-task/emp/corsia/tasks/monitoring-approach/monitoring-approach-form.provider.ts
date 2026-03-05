import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';

import { Subject } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TaskFormProvider } from '@aviation/request-task/task-form.provider';
import { getRequestTaskAttachmentTypeForRequestTaskType } from '@aviation/request-task/util';
import { FileUpload } from '@shared/file-input/file-upload-event';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { GovukValidators } from 'govuk-components';

import { CertMonitoringApproach, EmpEmissionsMonitoringApproachCorsia, FuelMonitoringApproachCorsia } from 'pmrv-api';

export interface MonitoringApproachFormModel {
  emissionsMonitoringApproach: FormGroup<MonitoringApproachTypeFormModel | null>;
  simplifiedApproach?: FormGroup<CertEmissionsTypeFormModel>;
}

export interface MonitoringApproachTypeFormModel {
  monitoringApproachType: FormControl<EmpEmissionsMonitoringApproachCorsia['monitoringApproachType'] | null>;
  certEmissionsType?: FormControl<CertMonitoringApproach['certEmissionsType']>;
}

export interface CertEmissionsTypeFormModel {
  explanation: FormControl<string | null>;
  supportingEvidenceFiles?: FormControl<FileUpload[]>;
}
export interface MonitoringApproachCorsiaValues {
  emissionsMonitoringApproach: {
    monitoringApproachType: EmpEmissionsMonitoringApproachCorsia['monitoringApproachType'] | null;
    certEmissionsType?: CertMonitoringApproach['certEmissionsType'];
  };
  simplifiedApproach?: {
    explanation: string;
    supportingEvidenceFiles?: Array<string>;
  };
}

@Injectable()
export class MonitoringApproachCorsiaFormProvider
  implements TaskFormProvider<CertMonitoringApproach | FuelMonitoringApproachCorsia, MonitoringApproachFormModel>
{
  private fb = inject(FormBuilder);
  private _form: FormGroup;
  private store = inject(RequestTaskStore);
  private requestTaskFileService = inject(RequestTaskFileService);
  private destroy$ = new Subject<void>();

  get form(): FormGroup<MonitoringApproachFormModel> {
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

  setFormValue(emissionsMonitoringApproach: CertMonitoringApproach | FuelMonitoringApproachCorsia): void {
    const isCertMonitoringType = emissionsMonitoringApproach?.monitoringApproachType === 'CERT_MONITORING';
    if (isCertMonitoringType) {
      this.addCertEmissionsType();
    }

    this.form.setValue({
      emissionsMonitoringApproach: {
        monitoringApproachType: emissionsMonitoringApproach?.monitoringApproachType ?? null,
        ...(isCertMonitoringType && {
          certEmissionsType: (emissionsMonitoringApproach as CertMonitoringApproach)?.certEmissionsType,
        }),
      },
      ...(isCertMonitoringType && {
        simplifiedApproach: {
          explanation: (emissionsMonitoringApproach as CertMonitoringApproach)?.explanation ?? null,
          supportingEvidenceFiles:
            (emissionsMonitoringApproach as CertMonitoringApproach)?.supportingEvidenceFiles?.map((uuid) => ({
              file: { name: this.store.empCorsiaDelegate.payload.empAttachments[uuid] } as File,
              uuid,
            })) ?? [],
        },
      }),
    });
    this.form.updateValueAndValidity();
  }

  getFormValue(): CertMonitoringApproach | FuelMonitoringApproachCorsia {
    const formValue = this.form.value;
    const isCertMonitoringType = formValue.emissionsMonitoringApproach?.monitoringApproachType === 'CERT_MONITORING';

    return {
      monitoringApproachType: formValue.emissionsMonitoringApproach.monitoringApproachType,
      ...(isCertMonitoringType && {
        certEmissionsType: formValue.emissionsMonitoringApproach.certEmissionsType,
        explanation: formValue?.simplifiedApproach?.explanation,
        supportingEvidenceFiles: formValue?.simplifiedApproach?.supportingEvidenceFiles.map((fu) => fu?.uuid),
      }),
    };
  }

  addSimplifiedApproach() {
    if (!this.form.contains('simplifiedApproach')) {
      this.form.addControl(
        'simplifiedApproach',
        this.fb.group<CertEmissionsTypeFormModel>(
          {
            explanation: new FormControl<string | null>(null, [
              GovukValidators.required(
                'Please provide information to support your eligibility for the simplified calculation procedures',
              ),
              GovukValidators.maxLength(10000, 'The description should not be more than 10000 characters'),
            ]),
            supportingEvidenceFiles: this.requestTaskFileService.buildFormControl(
              this.store.requestTaskId,
              [],
              this.store.empCorsiaDelegate.payload.empAttachments,
              getRequestTaskAttachmentTypeForRequestTaskType(this.store.getState().requestTaskItem?.requestTask?.type),
              false,
              !this.store.getState().isEditable,
            ) as FormControl<FileUpload[]>,
          },
          { validators: Validators.required },
        ),
      );
    }
  }

  removeSimplifiedApproach() {
    if (this.form.contains('simplifiedApproach')) {
      this.form.removeControl('simplifiedApproach');
      this.form.updateValueAndValidity();
    }
  }

  addCertEmissionsType() {
    if (!(this.form.get('emissionsMonitoringApproach') as FormGroup).contains('certEmissionsType')) {
      const monitoringApproachGroup = this.form.get('emissionsMonitoringApproach') as FormGroup;
      monitoringApproachGroup.addControl(
        'certEmissionsType',
        new FormControl(null, [GovukValidators.required("Select either 'Great circle distance' or 'Block time'")]),
      );
      this.form.updateValueAndValidity();
    }
  }

  get monitoringApproachTypeForm(): FormGroup<MonitoringApproachFormModel> {
    return this.form.get('emissionsMonitoringApproach') as FormGroup;
  }

  get simplifiedApproachForm(): FormGroup<CertEmissionsTypeFormModel> {
    return this.form.get('simplifiedApproach') as FormGroup;
  }

  private buildForm() {
    this._form = new FormGroup(
      {
        emissionsMonitoringApproach: this.fb.group(
          {
            monitoringApproachType: new FormControl(null, [GovukValidators.required('You must select one approach')]),
          },
          { updateOn: 'change' },
        ),
      },
      { updateOn: 'change' },
    );

    (this._form.get('emissionsMonitoringApproach') as FormGroup)
      .get('monitoringApproachType')
      .valueChanges.subscribe((monitoringApproachType) => {
        if (monitoringApproachType === 'CERT_MONITORING') {
          this.addSimplifiedApproach();
        } else {
          (this._form.get('emissionsMonitoringApproach') as FormGroup).get('certEmissionsType')?.setValue(null);
          this.removeSimplifiedApproach();
        }
        this._form.updateValueAndValidity();
      });
  }
}
