import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TaskFormProvider } from '@aviation/request-task/task-form.provider';
import { FileUpload } from '@shared/file-input/file-upload-event';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { dueDateMinValidatorToday } from '@tasks/permit-notification/follow-up/factory/form-provider';

import { GovukValidators } from 'govuk-components';

import { AviationDre, AviationDreDeterminationReason, AviationDreEmissionsCalculationApproach } from 'pmrv-api';
export interface DeterminationReasonFormModel {
  type: FormControl<AviationDreDeterminationReason['type'] | null>;
  furtherDetails?: FormControl<string | null>;
}

export interface CalculationApproachFormModel {
  totalReportableEmissions?: FormControl<string | null>;
  type?: FormControl<AviationDreEmissionsCalculationApproach['type'] | null>;
  otherDataSourceExplanation?: FormControl<string | null>;
  supportingDocuments?: FormControl<FileUpload[]>;
}
export interface AviationDreFeeDetailsFormModel {
  totalBillableHours: FormControl<string | null>;
  hourlyRate: FormControl<string | null>;
  dueDate: FormControl<string | null>;
  comments?: FormControl<string | null>;
}

export interface AviationDreFormModel {
  determinationReason: FormGroup<DeterminationReasonFormModel | null>;
  calculationApproach?: FormGroup<CalculationApproachFormModel>;
  chargeOperator?: FormGroup<{ chargeOperator: FormControl<boolean | null> }>;
  fee?: FormGroup<AviationDreFeeDetailsFormModel>;
}

export interface SupportingDocumentsViewModel {
  form: FormGroup;
  submitHidden: boolean;
  downloadUrl: string;
}

@Injectable()
export class AviationEmissionsFormProvider implements TaskFormProvider<AviationDre, AviationDreFormModel> {
  private fb = inject(FormBuilder);
  private _form: FormGroup<AviationDreFormModel>;
  private store = inject(RequestTaskStore);
  private requestTaskFileService = inject(RequestTaskFileService);
  private destroy$ = new Subject<void>();

  get form(): FormGroup<AviationDreFormModel> {
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

  setFormValue(aviationEmissions: AviationDre | undefined): void {
    if (aviationEmissions) {
      if (aviationEmissions?.fee?.chargeOperator) {
        this.addFee();
      } else {
        this.removeFee();
      }

      this.form.patchValue({
        determinationReason: aviationEmissions?.determinationReason ?? {},
        calculationApproach: {
          totalReportableEmissions: aviationEmissions?.totalReportableEmissions ?? null,
          type: aviationEmissions?.calculationApproach?.type ?? null,
          otherDataSourceExplanation: aviationEmissions?.calculationApproach?.otherDataSourceExplanation ?? null,
          supportingDocuments:
            aviationEmissions?.supportingDocuments?.map((uuid) => ({
              file: { name: this.store.dreDelegate.payload?.dreAttachments?.[uuid] } as File,
              uuid,
            })) ?? [],
        },
        chargeOperator: {
          chargeOperator: aviationEmissions?.fee?.chargeOperator ?? null,
        },
        fee: {
          totalBillableHours: aviationEmissions?.fee?.feeDetails?.totalBillableHours ?? null,
          hourlyRate: aviationEmissions?.fee?.feeDetails?.hourlyRate ?? null,
          dueDate: aviationEmissions?.fee?.feeDetails?.dueDate
            ? (new Date(aviationEmissions.fee.feeDetails.dueDate) as any)
            : null,
          comments: aviationEmissions?.fee?.feeDetails?.comments ?? null,
        },
      });
    }
  }

  get determinationReasonCtrl(): FormGroup {
    return this.form.get('determinationReason') as FormGroup;
  }

  get calculationApproachCtrl(): FormGroup {
    return this.form.get('calculationApproach') as FormGroup;
  }

  get chargeOperatorCtrl(): FormGroup {
    return this.form.get('chargeOperator') as FormGroup;
  }

  get feeCtrl(): FormGroup {
    return this.form.get('fee') as FormGroup;
  }

  getFormValue(): AviationDre {
    const value = this.form.value;

    const ret: any = {
      determinationReason: value.determinationReason,
    };

    if (value?.calculationApproach) {
      ret.totalReportableEmissions = value.calculationApproach.totalReportableEmissions;
      ret.calculationApproach = {
        type: value.calculationApproach.type,
        otherDataSourceExplanation: value.calculationApproach?.otherDataSourceExplanation,
      };

      if (
        value.calculationApproach.type !== 'OTHER_DATASOURCE' &&
        value.calculationApproach?.otherDataSourceExplanation
      ) {
        delete ret.calculationApproach.otherDataSourceExplanation;
      }

      ret.supportingDocuments = (value.calculationApproach?.supportingDocuments || [])
        ?.map((fu) => fu.uuid)
        .filter((uuid) => uuid);
    }

    if (typeof value.chargeOperator.chargeOperator === 'boolean') {
      ret.fee = {
        chargeOperator: value.chargeOperator.chargeOperator,
      };
    }

    value?.chargeOperator?.chargeOperator ? this.addFee() : this.removeFee();

    if (value?.fee?.totalBillableHours && value?.chargeOperator?.chargeOperator) {
      ret.fee.feeDetails = {
        totalBillableHours: value.fee.totalBillableHours,
        hourlyRate: value.fee.hourlyRate,
        dueDate: value.fee.dueDate,
        comments: value.fee.comments,
      };
    }

    return ret;
  }

  addSupportingDocuments() {
    this.form.getRawValue()?.calculationApproach?.supportingDocuments?.forEach((doc) => {
      this.store.dreDelegate.addDreAttachment({ [doc.uuid]: doc.file.name });
    });
  }

  private addFee() {
    if (!this.form.contains('fee')) {
      const commonMessage = 'Enter a whole number with up to 2 decimal places';
      this.form.addControl(
        'fee',
        this.fb.group<AviationDreFeeDetailsFormModel>({
          totalBillableHours: new FormControl<string | null>(null, {
            updateOn: 'change',
            validators: [
              GovukValidators.required('Enter the total billable hours'),
              GovukValidators.pattern('^(?!0\\.00)([1-9][0-9]*(\\.[0-9]{1,2})?|0\\.[0-9]{1,2})$', commonMessage),
              GovukValidators.notNaN(commonMessage),
            ],
          }),
          hourlyRate: new FormControl<string | null>(null, {
            updateOn: 'change',
            validators: [
              GovukValidators.required('Enter the hourly rate'),
              GovukValidators.min(0, commonMessage),
              GovukValidators.pattern('^(?!0\\.00)([1-9][0-9]*(\\.[0-9]{1,2})?|0\\.[0-9]{1,2})$', commonMessage),
              GovukValidators.notNaN(commonMessage),
            ],
          }),
          dueDate: new FormControl<string | null>(null, {
            updateOn: 'change',
            validators: [GovukValidators.required('Enter a full date'), dueDateMinValidatorToday()],
          }),
          comments: new FormControl<string | null>(null),
        }),
      );
    }
  }

  private removeFee() {
    if (this.form.contains('fee')) {
      this.form.removeControl('fee');
    }
  }

  private buildForm() {
    const formGroup = this.fb.group<AviationDreFormModel>(
      {
        determinationReason: new FormGroup<DeterminationReasonFormModel | null>({
          type: new FormControl<AviationDreDeterminationReason['type'] | null>(null, {
            updateOn: 'change',
            validators: [GovukValidators.required('Select why you determined the aviation emissions')],
          }),
          furtherDetails: new FormControl<string | null>(null),
        }),
        calculationApproach: new FormGroup<CalculationApproachFormModel | null>({
          totalReportableEmissions: new FormControl<string | null>(null, {
            validators: [
              GovukValidators.required('Enter the total aviation emissions'),
              GovukValidators.integerNumber('Enter a whole number without decimal places (you can use zero)'),
            ],
          }),
          type: new FormControl<AviationDreEmissionsCalculationApproach['type'] | null>(null, {
            validators: [GovukValidators.required('Select an option for how you calculated the emissions')],
          }),
          otherDataSourceExplanation: new FormControl<string | null>(null),
          supportingDocuments: this.store.dreDelegate?.payload?.dreAttachments
            ? (this.requestTaskFileService.buildFormControl(
                this.store.requestTaskId,
                [],
                this.store.dreDelegate.payload.dreAttachments,
                'AVIATION_DRE_UPLOAD_ATTACHMENT',
                false,
                !this.store.getState().isEditable,
              ) as FormControl<FileUpload[]>)
            : new FormControl<FileUpload[]>([]),
        }),
        chargeOperator: new FormGroup<{ chargeOperator: FormControl<boolean | null> }>({
          chargeOperator: new FormControl<boolean | null>(null, {
            validators: [GovukValidators.required('Select yes if you need to charge the operator a fee')],
          }),
        }),
      },
      { updateOn: 'change' },
    );

    const calculationApproach = formGroup.get('calculationApproach');
    const calculationApproachType = formGroup.get('calculationApproach').get('type');
    const otherDataSourceExplanation = formGroup.get('calculationApproach').get('otherDataSourceExplanation');

    calculationApproachType.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((value) => {
      if (value !== 'OTHER_DATASOURCE') {
        otherDataSourceExplanation.clearValidators();
        otherDataSourceExplanation.setValue(null);
      } else {
        otherDataSourceExplanation.setValidators([GovukValidators.required('Enter how you calculated the emissions')]);
      }
      otherDataSourceExplanation.updateValueAndValidity();
      calculationApproach.updateValueAndValidity();
    });

    return (this._form = formGroup);
  }
}
