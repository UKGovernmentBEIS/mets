import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';

import { Subject } from 'rxjs';

import { FileUpload } from '@shared/file-input/file-upload-event';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { GovukValidators } from 'govuk-components';

import { EmpEmissionsMonitoringApproach } from 'pmrv-api';

import { RequestTaskStore } from '../../../../store';
import { TaskFormProvider } from '../../../../task-form.provider';
import { getRequestTaskAttachmentTypeForRequestTaskType } from '../../../../util';
import { EmissionsMonitoringApproach } from './monitoring-approach-types.interface';

export interface SimplifiedApproachFormModel {
  explanation: FormControl<string | null>;
  supportingEvidenceFiles?: FormControl<FileUpload[]>;
}

export interface MonitoringApproachFormModel {
  monitoringApproachType: FormControl<EmissionsMonitoringApproach['monitoringApproachType'] | null>;
  simplifiedApproach?: FormGroup<SimplifiedApproachFormModel>;
}

@Injectable()
export class MonitoringApproachFormProvider
  implements TaskFormProvider<EmissionsMonitoringApproach, MonitoringApproachFormModel>
{
  private fb = inject(FormBuilder);
  private _form: FormGroup<MonitoringApproachFormModel>;
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

  setFormValue(emissionsMonitoringApproach: EmissionsMonitoringApproach | undefined): void {
    if (emissionsMonitoringApproach) {
      let value: FormGroup<MonitoringApproachFormModel>['value'] = {
        monitoringApproachType: emissionsMonitoringApproach.monitoringApproachType ?? null,
      };

      if (emissionsMonitoringApproach.monitoringApproachType !== 'FUEL_USE_MONITORING') {
        value = {
          ...value,
          simplifiedApproach: {
            explanation: emissionsMonitoringApproach.explanation ?? null,
            supportingEvidenceFiles:
              emissionsMonitoringApproach.supportingEvidenceFiles?.map((uuid) => ({
                file: { name: this.store.empUkEtsDelegate.payload.empAttachments[uuid] } as File,
                uuid,
              })) ?? [],
          },
        };
        this.addSimplifiedApproach();
      } else {
        this.removeSimplifiedApproach();
      }

      this.form.setValue(value as any);
    }
  }

  getFormValue(): EmissionsMonitoringApproach {
    const value = this.form.value;
    const ret: any = {
      monitoringApproachType: value.monitoringApproachType,
    };

    if (value.simplifiedApproach?.explanation) {
      ret.explanation = value.simplifiedApproach.explanation;
    }

    if (value.simplifiedApproach?.supportingEvidenceFiles) {
      ret.supportingEvidenceFiles = value.simplifiedApproach.supportingEvidenceFiles.map((fu) => fu.uuid);
    }

    return ret;
  }

  addSimplifiedApproach() {
    if (!this.form.contains('simplifiedApproach')) {
      this.form.addControl(
        'simplifiedApproach',
        this.fb.group<SimplifiedApproachFormModel>(
          {
            explanation: new FormControl<string | null>(null, {
              validators: GovukValidators.required(
                'Please provide information to support your eligibility for the simplified calculation procedures',
              ),
            }),
            supportingEvidenceFiles: this.requestTaskFileService.buildFormControl(
              this.store.requestTaskId,
              [],
              this.store.empUkEtsDelegate.payload.empAttachments,
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
    }
  }

  get monitoringApproachTypeCtrl(): FormControl {
    return this.form.get('monitoringApproachType') as FormControl;
  }

  get simplifiedApproachForm(): FormGroup<SimplifiedApproachFormModel> {
    return this.form.get('simplifiedApproach') as FormGroup;
  }

  private buildForm() {
    this._form = this.fb.group<MonitoringApproachFormModel>(
      {
        monitoringApproachType: new FormControl<EmpEmissionsMonitoringApproach['monitoringApproachType'] | null>(null, {
          updateOn: 'change',
          validators: GovukValidators.required('You must select one approach'),
        }),
      },
      { updateOn: 'change' },
    );
  }
}
