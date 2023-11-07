import { inject, Injectable } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, ValidatorFn, Validators } from '@angular/forms';

import { Subject } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TaskFormProvider } from '@aviation/request-task/task-form.provider';
import { getRequestTaskAttachmentTypeForRequestTaskType } from '@aviation/request-task/util';
import { FileUpload } from '@shared/file-input/file-upload-event';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { GovukValidators } from 'govuk-components';

import { EmpManagementProceduresCorsia, EmpMonitoringReportingRoleCorsia } from 'pmrv-api';

export interface DataManagementFormModel {
  description: FormControl<string | null>;
  dataFlowDiagram: FormControl<FileUpload>;
}
export interface DescriptionFormModel {
  description: FormControl<string | null>;
}
export interface ManagementProceduresFormModel {
  monitoringReportingRoles: FormArray<
    FormGroup<{
      jobTitle: FormControl<string | null>;
      mainDuties: FormControl<string | null>;
    }>
  >;
  dataManagement: FormGroup<DataManagementFormModel>;
  recordKeepingAndDocumentation: FormGroup<DescriptionFormModel>;
  riskExplanation: FormGroup<DescriptionFormModel>;
  empRevisions: FormGroup<DescriptionFormModel>;
}

@Injectable()
export class ManagementProceduresCorsiaFormProvider
  implements TaskFormProvider<EmpManagementProceduresCorsia, ManagementProceduresFormModel>
{
  private fb = inject(FormBuilder);
  private requestTaskFileService = inject(RequestTaskFileService);
  private store = inject(RequestTaskStore);
  private _form: FormGroup<ManagementProceduresFormModel>;
  private destroy$ = new Subject<void>();

  get form(): FormGroup<ManagementProceduresFormModel> {
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

  setFormValue(managementProcedures: EmpManagementProceduresCorsia | undefined) {
    if (managementProcedures?.monitoringReportingRoles?.monitoringReportingRoles?.length) {
      this.form.setControl(
        'monitoringReportingRoles',
        this.fb.array(
          managementProcedures.monitoringReportingRoles.monitoringReportingRoles.map((role) =>
            this.createRoleGroup(role, [Validators.required]),
          ),
        ),
      );
    }

    this.form.patchValue({
      dataManagement: {
        description: managementProcedures?.dataManagement?.description ?? null,
      },
      recordKeepingAndDocumentation: {
        description: managementProcedures?.recordKeepingAndDocumentation?.description ?? null,
      },
      riskExplanation: {
        description: managementProcedures?.riskExplanation?.description ?? null,
      },
      empRevisions: {
        description: managementProcedures?.empRevisions?.description ?? null,
      },
    });
  }

  get monitoringReportingRolesCtrl(): FormControl {
    return this.form.get('monitoringReportingRoles') as FormControl;
  }

  get dataManagementCtrl(): FormGroup {
    return this.form.get('dataManagement') as FormGroup;
  }

  get recordKeepingAndDocumentationCtrl(): FormGroup {
    return this.form.get('recordKeepingAndDocumentation') as FormGroup;
  }

  get riskExplanationCtrl(): FormGroup {
    return this.form.get('riskExplanation') as FormGroup;
  }

  get empRevisionsCtrl(): FormGroup {
    return this.form.get('empRevisions') as FormGroup;
  }

  addManagementProceduresRole() {
    this.rolesCtrl.push(this.createRoleGroup());
  }

  removeManagementProceduresRole(index: number) {
    if (this.rolesCtrl.length > 1) {
      this.rolesCtrl.removeAt(index);
    }
  }

  private get rolesCtrl(): FormArray {
    return this.form.get('monitoringReportingRoles') as FormArray;
  }

  private buildForm() {
    const state = this.store.getState();
    const payload = state.requestTaskItem?.requestTask?.payload as any;
    const dataManagement = payload?.emissionsMonitoringPlan?.managementProcedures?.dataManagement;
    this._form = this.fb.group(
      {
        monitoringReportingRoles: this.fb.array([this.createRoleGroup(null, [Validators.required])]),
        dataManagement: new FormGroup<DataManagementFormModel>({
          description: new FormControl(null as string, [
            GovukValidators.required('Enter a description for the procedure'),
            GovukValidators.maxLength(10000, 'The description should not be more than 10000 characters'),
          ]),
          dataFlowDiagram: this.requestTaskFileService.buildFormControl(
            this.store.requestTaskId,
            dataManagement?.dataFlowDiagram,
            payload?.empAttachments,
            getRequestTaskAttachmentTypeForRequestTaskType(state.requestTaskItem?.requestTask?.type),
            true,
            !state.isEditable,
          ) as FormControl<FileUpload>,
        }),
        recordKeepingAndDocumentation: new FormGroup<DescriptionFormModel>({
          description: new FormControl(null as string, [
            GovukValidators.required('Enter a description for the procedure'),
            GovukValidators.maxLength(10000, 'The description should not be more than 10000 characters'),
          ]),
        }),
        riskExplanation: new FormGroup<DescriptionFormModel>({
          description: new FormControl(null as string, [
            GovukValidators.required('Enter a description for the procedure'),
            GovukValidators.maxLength(10000, 'The description should not be more than 10000 characters'),
          ]),
        }),
        empRevisions: new FormGroup<DescriptionFormModel>({
          description: new FormControl(null as string, [
            GovukValidators.required('Enter a description for the procedure'),
            GovukValidators.maxLength(10000, 'The description should not be more than 10000 characters'),
          ]),
        }),
      },
      { updateOn: 'change' },
    );
  }

  getFormValue(): EmpManagementProceduresCorsia {
    return {
      monitoringReportingRoles: {
        monitoringReportingRoles: this.form.get('monitoringReportingRoles').value,
      },
      dataManagement: this.form.get('dataManagement').value
        ? {
            ...this.form.get('dataManagement').value,
            dataFlowDiagram: this.form.get('dataManagement').getRawValue()?.dataFlowDiagram?.uuid,
          }
        : null,
      recordKeepingAndDocumentation: this.form.get('recordKeepingAndDocumentation')?.value,
      riskExplanation: this.form.get('riskExplanation')?.value,
      empRevisions: this.form.get('empRevisions')?.value,
    } as any;
  }

  private createRoleGroup(role?: EmpMonitoringReportingRoleCorsia, validators: ValidatorFn[] = []): FormGroup {
    return this.fb.group(
      {
        jobTitle: [
          role?.jobTitle ?? null,
          [GovukValidators.required('Enter a job title'), GovukValidators.maxLength(250, 'Enter up to 250 characters')],
        ],
        mainDuties: [
          role?.mainDuties ?? null,
          [
            GovukValidators.required('Enter the main duties of the role'),
            GovukValidators.maxLength(500, 'Enter up to 500 characters'),
          ],
        ],
      },
      { updateOn: 'change', validators },
    );
  }
}
