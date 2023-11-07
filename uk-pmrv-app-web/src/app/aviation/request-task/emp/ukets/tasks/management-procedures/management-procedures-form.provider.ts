import { inject, Injectable } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, ValidatorFn, Validators } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { FileUpload } from '@shared/file-input/file-upload-event';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { GovukValidators } from 'govuk-components';

import { EmpManagementProcedures, EmpMonitoringReportingRole } from 'pmrv-api';

import { EmpRequestTaskPayloadUkEts, RequestTaskStore } from '../../../../store';
import { TaskFormProvider } from '../../../../task-form.provider';
import { getRequestTaskAttachmentTypeForRequestTaskType } from '../../../../util';
import { ProcedureFormBuilder, ProcedureFormModel } from '../../../shared/procedure-form-step';
import { emissionSourcesQuery } from '../emission-sources/store/emission-sources.selectors';
import { monitoringApproachQuery } from '../monitoring-approach/store/monitoring-approach.selectors';

export interface DataFlowFormModel extends ProcedureFormModel {
  diagramReference: FormControl<string>;
  otherStandardsApplied: FormControl<string>;
  primaryDataSources: FormControl<string>;
  processingSteps: FormControl<string>;
  diagramAttachmentId: FormControl<FileUpload>;
}

export interface EnvironmentalManagementSystemFormModel {
  exist: FormControl<boolean>;
  certified: FormControl<boolean>;
  certificationStandard: FormControl<string>;
}

export interface ManagementProceduresFormModel {
  monitoringReportingRoles: FormArray<
    FormGroup<{
      jobTitle: FormControl<string | null>;
      mainDuties: FormControl<string | null>;
    }>
  >;
  recordKeepingAndDocumentation: FormGroup<ProcedureFormModel>;
  assignmentOfResponsibilities?: FormGroup<ProcedureFormModel>;
  monitoringPlanAppropriateness?: FormGroup<ProcedureFormModel>;
  dataFlowActivities?: FormGroup<DataFlowFormModel>;
  qaMeteringAndMeasuringEquipment?: FormGroup<ProcedureFormModel>;
  dataValidation?: FormGroup<ProcedureFormModel>;
  correctionsAndCorrectiveActions?: FormGroup<ProcedureFormModel>;
  controlOfOutsourcedActivities?: FormGroup<ProcedureFormModel>;
  assessAndControlRisks?: FormGroup<ProcedureFormModel>;
  riskAssessmentFile?: FormControl<FileUpload>;
  upliftQuantityCrossChecks?: FormGroup<ProcedureFormModel>;
  environmentalManagementSystem?: FormGroup<EnvironmentalManagementSystemFormModel>;
}

@Injectable()
export class ManagementProceduresFormProvider
  implements TaskFormProvider<EmpManagementProcedures, ManagementProceduresFormModel>
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

  get monitoringReportingRolesCtrl(): FormControl {
    return this.form.get('monitoringReportingRoles') as FormControl;
  }
  get recordKeepingAndDocumentationCtrl(): FormGroup {
    return this.form.get('recordKeepingAndDocumentation') as FormGroup;
  }
  get assignmentOfResponsibilitiesCtrl(): FormGroup {
    return this.form.get('assignmentOfResponsibilities') as FormGroup;
  }
  get monitoringPlanAppropriatenessCtrl(): FormGroup {
    return this.form.get('monitoringPlanAppropriateness') as FormGroup;
  }
  get dataFlowActivitiesCtrl(): FormGroup {
    return this.form.get('dataFlowActivities') as FormGroup;
  }
  get qaMeteringAndMeasuringEquipmentCtrl(): FormGroup {
    return this.form.get('qaMeteringAndMeasuringEquipment') as FormGroup;
  }
  get dataValidationCtrl(): FormGroup {
    return this.form.get('dataValidation') as FormGroup;
  }
  get correctionsAndCorrectiveActionsCtrl(): FormGroup {
    return this.form.get('correctionsAndCorrectiveActions') as FormGroup;
  }
  get controlOfOutsourcedActivitiesCtrl(): FormGroup {
    return this.form.get('controlOfOutsourcedActivities') as FormGroup;
  }
  get assessAndControlRisksCtrl(): FormGroup {
    return this.form.get('assessAndControlRisks') as FormGroup;
  }
  get riskAssessmentFileCtrl(): FormGroup {
    return this.form.get('riskAssessmentFile') as FormGroup;
  }
  get upliftQuantityCrossChecksCtrl(): FormGroup {
    return this.form.get('upliftQuantityCrossChecks') as FormGroup;
  }
  get environmentalManagementSystemCtrl(): FormGroup {
    return this.form.get('environmentalManagementSystem') as FormGroup;
  }

  setFormValue(managementProcedures: EmpManagementProcedures | undefined) {
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
    this.store.pipe(monitoringApproachQuery.selectMonitoringApproach, takeUntil(this.destroy$)).subscribe((ma) => {
      if (
        ma?.monitoringApproachType === 'EUROCONTROL_SMALL_EMITTERS' ||
        ma?.monitoringApproachType === 'FUEL_USE_MONITORING'
      ) {
        this.addSmallEmittersForms();
      } else {
        this.removeSmallEmittersForms();
      }
      if (ma?.monitoringApproachType === 'FUEL_USE_MONITORING') {
        this.addRiskAssessmentForm();
        this.store.pipe(emissionSourcesQuery.selectEmissionSources, takeUntil(this.destroy$)).subscribe((es) => {
          if (
            es?.aircraftTypes?.length &&
            es.aircraftTypes.every(
              (aircraftType) => aircraftType.fuelConsumptionMeasuringMethod === 'BLOCK_ON_BLOCK_OFF',
            )
          ) {
            this.removeUpliftQuantityCrossChecksForm();
          } else {
            this.addUpliftQuantityCrossChecksForm();
          }
        });
      } else {
        this.removeRiskAssessmentForm();
        this.removeUpliftQuantityCrossChecksForm();
      }
    });

    this.form.patchValue({
      recordKeepingAndDocumentation: managementProcedures?.recordKeepingAndDocumentation,
      assignmentOfResponsibilities: managementProcedures?.assignmentOfResponsibilities,
      monitoringPlanAppropriateness: managementProcedures?.monitoringPlanAppropriateness,
      dataFlowActivities: {
        ...managementProcedures?.dataFlowActivities,
        diagramAttachmentId: managementProcedures?.dataFlowActivities?.diagramAttachmentId
          ? {
              file: {
                name: this.store.empUkEtsDelegate.payload.empAttachments[
                  managementProcedures?.dataFlowActivities.diagramAttachmentId
                ],
              } as File,
              uuid: managementProcedures?.dataFlowActivities?.diagramAttachmentId,
            }
          : null,
      },
      qaMeteringAndMeasuringEquipment: managementProcedures?.qaMeteringAndMeasuringEquipment,
      dataValidation: managementProcedures?.dataValidation,
      correctionsAndCorrectiveActions: managementProcedures?.correctionsAndCorrectiveActions,
      controlOfOutsourcedActivities: managementProcedures?.controlOfOutsourcedActivities,
      assessAndControlRisks: managementProcedures?.assessAndControlRisks,
      riskAssessmentFile: managementProcedures?.riskAssessmentFile
        ? {
            file: {
              name: this.store.empUkEtsDelegate.payload.empAttachments[managementProcedures?.riskAssessmentFile],
            } as File,
            uuid: managementProcedures?.riskAssessmentFile,
          }
        : null,
      upliftQuantityCrossChecks: managementProcedures?.upliftQuantityCrossChecks,
      environmentalManagementSystem: managementProcedures?.environmentalManagementSystem,
    });
  }

  getFormValue(): EmpManagementProcedures {
    return {
      monitoringReportingRoles: {
        monitoringReportingRoles: this.form.get('monitoringReportingRoles').value,
      },
      recordKeepingAndDocumentation: this.form.get('recordKeepingAndDocumentation').value,
      assignmentOfResponsibilities: this.form.get('assignmentOfResponsibilities')?.value,
      monitoringPlanAppropriateness: this.form.get('monitoringPlanAppropriateness')?.value,
      dataFlowActivities: this.form.get('dataFlowActivities')
        ? {
            ...this.form.get('dataFlowActivities').value,
            diagramAttachmentId: this.form.get('dataFlowActivities').getRawValue()?.diagramAttachmentId?.uuid ?? null,
          }
        : null,
      qaMeteringAndMeasuringEquipment: this.form.get('qaMeteringAndMeasuringEquipment')?.value,
      dataValidation: this.form.get('dataValidation')?.value,
      correctionsAndCorrectiveActions: this.form.get('correctionsAndCorrectiveActions')?.value,
      controlOfOutsourcedActivities: this.form.get('controlOfOutsourcedActivities')?.value,
      assessAndControlRisks: this.form.get('assessAndControlRisks')?.value,
      riskAssessmentFile: this.form.get('riskAssessmentFile')?.getRawValue()?.uuid ?? null,
      upliftQuantityCrossChecks: this.form.get('upliftQuantityCrossChecks')?.value,
      environmentalManagementSystem: this.form.get('environmentalManagementSystem')?.value,
    } as EmpManagementProcedures;
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
    this._form = this.fb.group(
      {
        monitoringReportingRoles: this.fb.array([this.createRoleGroup(null, [Validators.required])]),
        recordKeepingAndDocumentation: ProcedureFormBuilder.createProcedureForm([Validators.required]),
      },
      { updateOn: 'change' },
    );
  }

  addSmallEmittersForms() {
    if (!this.form.contains('assignmentOfResponsibilities')) {
      this.form.addControl(
        'assignmentOfResponsibilities',
        ProcedureFormBuilder.createProcedureForm([Validators.required]),
      );
    }
    if (!this.form.contains('monitoringPlanAppropriateness')) {
      this.form.addControl(
        'monitoringPlanAppropriateness',
        ProcedureFormBuilder.createProcedureForm([Validators.required]),
      );
    }
    if (!this.form.contains('dataFlowActivities')) {
      this.form.addControl('dataFlowActivities', this.createDataFlowForm([Validators.required]));
    }
    if (!this.form.contains('qaMeteringAndMeasuringEquipment')) {
      this.form.addControl(
        'qaMeteringAndMeasuringEquipment',
        ProcedureFormBuilder.createProcedureForm([Validators.required]),
      );
    }
    if (!this.form.contains('dataValidation')) {
      this.form.addControl('dataValidation', ProcedureFormBuilder.createProcedureForm([Validators.required]));
    }
    if (!this.form.contains('correctionsAndCorrectiveActions')) {
      this.form.addControl(
        'correctionsAndCorrectiveActions',
        ProcedureFormBuilder.createProcedureForm([Validators.required]),
      );
    }
    if (!this.form.contains('controlOfOutsourcedActivities')) {
      this.form.addControl(
        'controlOfOutsourcedActivities',
        ProcedureFormBuilder.createProcedureForm([Validators.required]),
      );
    }
    if (!this.form.contains('assessAndControlRisks')) {
      this.form.addControl('assessAndControlRisks', ProcedureFormBuilder.createProcedureForm([Validators.required]));
    }
    if (!this.form.contains('environmentalManagementSystem')) {
      this.form.addControl(
        'environmentalManagementSystem',
        this.createEnvironmentalManagementForm([Validators.required]),
      );
    }
  }

  createDataFlowForm(validators: ValidatorFn[] = []): FormGroup<DataFlowFormModel> {
    const state = this.store.getState();
    const payload = state.requestTaskItem?.requestTask?.payload as EmpRequestTaskPayloadUkEts;
    const dataFlowActivities = payload?.emissionsMonitoringPlan?.managementProcedures?.dataFlowActivities;

    return new FormGroup<DataFlowFormModel>(
      {
        procedureDescription: new FormControl(null as string, [
          GovukValidators.required('Enter a description for the procedure'),
          GovukValidators.maxLength(10000, 'The procedure description should not be more than 10000 characters'),
        ]),
        procedureDocumentName: new FormControl(null as string, [
          GovukValidators.required('Enter the name of the procedure document'),
          GovukValidators.maxLength(500, 'The name of the procedure document should not be more than 500 characters'),
        ]),
        procedureReference: new FormControl(null as string, [
          GovukValidators.required('Enter a procedure reference'),
          GovukValidators.maxLength(500, 'The procedure reference should not be more than 500 characters'),
        ]),
        responsibleDepartmentOrRole: new FormControl(null as string, [
          GovukValidators.required('Enter the name of the department or role responsible'),
          GovukValidators.maxLength(
            500,
            'The name of the department or role responsible should not be more than 500 characters',
          ),
        ]),
        locationOfRecords: new FormControl(null as string, [
          GovukValidators.required('Enter the physical location of the records'),
          GovukValidators.maxLength(500, 'The physical location of the records should not be more than 500 characters'),
        ]),
        itSystemUsed: new FormControl(null as string, [
          GovukValidators.maxLength(500, 'The IT system used should not be more than 500 characters'),
        ]),
        diagramReference: new FormControl(null as string, [
          GovukValidators.maxLength(250, 'The diagram reference should not be more than 250 characters'),
        ]),
        otherStandardsApplied: new FormControl(null as string, [
          GovukValidators.maxLength(250, 'The standards applied should not be more than 250 characters'),
        ]),
        primaryDataSources: new FormControl(null as string, [
          GovukValidators.required('Enter the primary data sources'),
          GovukValidators.maxLength(1000, 'The primary data sources should not be more than 1000 characters'),
        ]),
        processingSteps: new FormControl(null as string, [
          GovukValidators.required('Enter a description of the processing steps for each data flow activity'),
          GovukValidators.maxLength(10000, 'The description should not be more than 10000 characters'),
        ]),
        diagramAttachmentId: this.requestTaskFileService.buildFormControl(
          this.store.requestTaskId,
          dataFlowActivities?.diagramAttachmentId,
          payload?.empAttachments,
          getRequestTaskAttachmentTypeForRequestTaskType(state.requestTaskItem?.requestTask?.type),
          true,
          !state.isEditable,
        ) as FormControl<FileUpload>,
      },
      { updateOn: 'change', validators },
    );
  }

  createEnvironmentalManagementForm(validators: ValidatorFn[] = []): FormGroup<EnvironmentalManagementSystemFormModel> {
    const formGroup = new FormGroup<EnvironmentalManagementSystemFormModel>(
      {
        exist: new FormControl<boolean | null>(null, {
          validators: [GovukValidators.required('Select yes or no')],
        }),
        certified: new FormControl<boolean | null>(null),
        certificationStandard: new FormControl<string | null>(null),
      },
      { updateOn: 'change', validators },
    );

    const exist = formGroup.get('exist');
    const certified = formGroup.get('certified');
    const certificationStandard = formGroup.get('certificationStandard');

    exist.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((exist) => {
      if (exist) {
        certified.setValidators(GovukValidators.required('Select yes or no'));
        certified.updateValueAndValidity();
        certified.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((certified) => {
          if (certified) {
            certificationStandard.setValidators([
              GovukValidators.required('Please identify a standard which the system is certified against'),
              GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
            ]);
            certificationStandard.updateValueAndValidity();
          } else {
            certificationStandard.clearValidators();
            certificationStandard.setValue(null);
          }
        });
      } else {
        certified.clearValidators();
        certified.setValue(null);
      }
    });

    return formGroup;
  }

  private createRoleGroup(role?: EmpMonitoringReportingRole, validators: ValidatorFn[] = []): FormGroup {
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

  removeSmallEmittersForms() {
    if (this.form.contains('assignmentOfResponsibilities')) {
      this.form.removeControl('assignmentOfResponsibilities');
    }
    if (this.form.contains('monitoringPlanAppropriateness')) {
      this.form.removeControl('monitoringPlanAppropriateness');
    }
    if (this.form.contains('dataFlowActivities')) {
      this.form.removeControl('dataFlowActivities');
    }
    if (this.form.contains('qaMeteringAndMeasuringEquipment')) {
      this.form.removeControl('qaMeteringAndMeasuringEquipment');
    }
    if (this.form.contains('dataValidation')) {
      this.form.removeControl('dataValidation');
    }
    if (this.form.contains('correctionsAndCorrectiveActions')) {
      this.form.removeControl('correctionsAndCorrectiveActions');
    }
    if (this.form.contains('controlOfOutsourcedActivities')) {
      this.form.removeControl('controlOfOutsourcedActivities');
    }
    if (this.form.contains('assessAndControlRisks')) {
      this.form.removeControl('assessAndControlRisks');
    }
    if (this.form.contains('environmentalManagementSystem')) {
      this.form.removeControl('environmentalManagementSystem');
    }
  }

  addRiskAssessmentForm() {
    if (!this.form.contains('riskAssessmentFile')) {
      const state = this.store.getState();
      const payload = state.requestTaskItem?.requestTask?.payload as EmpRequestTaskPayloadUkEts;
      const riskAssessmentFile = payload?.emissionsMonitoringPlan?.managementProcedures?.riskAssessmentFile;
      this.form.addControl(
        'riskAssessmentFile',
        this.requestTaskFileService.buildFormControl(
          this.store.requestTaskId,
          riskAssessmentFile,
          payload?.empAttachments,
          getRequestTaskAttachmentTypeForRequestTaskType(state.requestTaskItem?.requestTask?.type),
          true,
          !state.isEditable,
        ),
      );
    }
  }

  removeRiskAssessmentForm() {
    if (this.form.contains('riskAssessmentFile')) {
      this.form.removeControl('riskAssessmentFile');
    }
  }

  addUpliftQuantityCrossChecksForm() {
    if (!this.form.contains('upliftQuantityCrossChecks')) {
      this.form.addControl(
        'upliftQuantityCrossChecks',
        ProcedureFormBuilder.createProcedureForm([Validators.required]),
      );
    }
  }

  removeUpliftQuantityCrossChecksForm() {
    if (this.form.contains('upliftQuantityCrossChecks')) {
      this.form.removeControl('upliftQuantityCrossChecks');
    }
  }
}
