import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';
import { cloneDeep } from 'lodash-es';

import { GovukValidators } from 'govuk-components';

import {
  AviationAerNotVerifiedDecision,
  AviationAerVerificationDecision,
  AviationAerVerifiedSatisfactoryWithCommentsDecision,
} from 'pmrv-api';

export interface OverallDecisionFormModel {
  type: FormGroup<'VERIFIED_AS_SATISFACTORY' | 'VERIFIED_AS_SATISFACTORY_WITH_COMMENTS' | 'NOT_VERIFIED' | null>;
  reasons?: FormControl<Array<string>>;
  notVerifiedReasons?: FormControl<NotVerifiedFormModel>;
}

export interface NotVerifiedFormModel {
  type: FormControl<
    Array<
      | 'UNCORRECTED_MATERIAL_MISSTATEMENT'
      | 'UNCORRECTED_MATERIAL_NON_CONFORMITY'
      | 'VERIFICATION_DATA_OR_INFORMATION_LIMITATIONS'
      | 'SCOPE_LIMITATIONS_DUE_TO_LACK_OF_CLARITY'
      | 'SCOPE_LIMITATIONS_OF_APPROVED_MONITORING_PLAN'
      | 'NOT_APPROVED_MONITORING_PLAN_BY_REGULATOR'
      | 'ANOTHER_REASON'
      | undefined
    >
  >;
  otherDetails?: FormControl<string>;
  verificationDetails?: FormControl<string>;
  clarityDetails?: FormControl<string>;
  empLimitationDetails?: FormControl<string>;
  empNotApprovedDetails?: FormControl<string>;
}

export interface NotVerifiedValue {
  type: Array<
    | 'UNCORRECTED_MATERIAL_MISSTATEMENT'
    | 'UNCORRECTED_MATERIAL_NON_CONFORMITY'
    | 'VERIFICATION_DATA_OR_INFORMATION_LIMITATIONS'
    | 'SCOPE_LIMITATIONS_DUE_TO_LACK_OF_CLARITY'
    | 'SCOPE_LIMITATIONS_OF_APPROVED_MONITORING_PLAN'
    | 'NOT_APPROVED_MONITORING_PLAN_BY_REGULATOR'
    | 'ANOTHER_REASON'
    | undefined
  >;
  otherDetails?: string | null;
  verificationDetails?: string | null;
  clarityDetails?: string | null;
  empLimitationDetails?: string | null;
  empNotApprovedDetails?: string | null;
}

@Injectable()
export class OverallDecisionFormProvider
  implements
    TaskFormProvider<
      | AviationAerVerificationDecision
      | AviationAerVerifiedSatisfactoryWithCommentsDecision
      | AviationAerNotVerifiedDecision,
      OverallDecisionFormModel
    >
{
  private fb = inject(FormBuilder);
  private _form: FormGroup;
  private destroy$ = new Subject<void>();

  get typeCtrl(): FormControl {
    return this.form.get('type') as FormControl;
  }

  get notVerifiedCtrl(): FormGroup {
    return this.form.get('notVerifiedReasons') as FormGroup;
  }

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

  private buildForm() {
    this._form = this.fb.group(
      {
        type: new FormControl<
          'VERIFIED_AS_SATISFACTORY' | 'VERIFIED_AS_SATISFACTORY_WITH_COMMENTS' | 'NOT_VERIFIED' | null
        >(null, [GovukValidators.required('Select one option for your assessment of the report')]),
      },
      { updateOn: 'change' },
    );

    this.typeCtrl.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((type) => {
      if (type === 'VERIFIED_AS_SATISFACTORY') {
        this.form.removeControl('reasons');
        this.form.removeControl('notVerifiedReasons');
      } else if (type === 'VERIFIED_AS_SATISFACTORY_WITH_COMMENTS') {
        this.form.removeControl('notVerifiedReasons');
        this.addReasonsCtrl();
      } else if (type === 'NOT_VERIFIED') {
        this.form.removeControl('reasons');
        this.addNotVerifiedReasonsCtrl();
      }
    });
  }

  addReasonsCtrl() {
    this.form.addControl('reasons', this.fb.array([], [GovukValidators.required('')]));
  }

  addNotVerifiedReasonsCtrl() {
    this.form.addControl('notVerifiedReasons', this.createNotVerifiedReasonsFormControl({ type: [] }));
    this.notVerifiedSub();
  }

  notVerifiedSub() {
    this.notVerifiedCtrl
      .get('type')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        if (value.includes('VERIFICATION_DATA_OR_INFORMATION_LIMITATIONS')) {
          this.notVerifiedCtrl
            .get('verificationDetails')
            .setValidators([
              GovukValidators.required(
                'Please provide more details why you cannot verify the report due to limitations in the data or information made available',
              ),
              GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
            ]);
          this.notVerifiedCtrl.get('verificationDetails').updateValueAndValidity();
        } else {
          this.notVerifiedCtrl.get('verificationDetails').clearValidators();
          this.notVerifiedCtrl.get('verificationDetails').setValue(null);
        }

        if (value.includes('SCOPE_LIMITATIONS_DUE_TO_LACK_OF_CLARITY')) {
          this.notVerifiedCtrl
            .get('clarityDetails')
            .setValidators([
              GovukValidators.required(
                'Provide more details why you cannot verify the report due to limitations of scope because of lack of clarity',
              ),
              GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
            ]);
          this.notVerifiedCtrl.get('clarityDetails').updateValueAndValidity();
        } else {
          this.notVerifiedCtrl.get('clarityDetails').clearValidators();
          this.notVerifiedCtrl.get('clarityDetails').setValue(null);
        }

        if (value.includes('SCOPE_LIMITATIONS_OF_APPROVED_MONITORING_PLAN')) {
          this.notVerifiedCtrl
            .get('empLimitationDetails')
            .setValidators([
              GovukValidators.required(
                'Provide more details why you cannot verify the report due to limitations of scope in the approved emissions monitoring plan',
              ),
              GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
            ]);
          this.notVerifiedCtrl.get('empLimitationDetails').updateValueAndValidity();
        } else {
          this.notVerifiedCtrl.get('empLimitationDetails').clearValidators();
          this.notVerifiedCtrl.get('empLimitationDetails').setValue(null);
        }

        if (value.includes('NOT_APPROVED_MONITORING_PLAN_BY_REGULATOR')) {
          this.notVerifiedCtrl
            .get('empNotApprovedDetails')
            .setValidators([
              GovukValidators.required(
                'Provide more details why you cannot verify the report due to the emissions monitoring plan not being approved by the regulator',
              ),
              GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
            ]);
          this.notVerifiedCtrl.get('empNotApprovedDetails').updateValueAndValidity();
        } else {
          this.notVerifiedCtrl.get('empNotApprovedDetails').clearValidators();
          this.notVerifiedCtrl.get('empNotApprovedDetails').setValue(null);
        }

        if (value.includes('ANOTHER_REASON')) {
          this.notVerifiedCtrl
            .get('otherDetails')
            .setValidators([
              GovukValidators.required('Provide a reason why you cannot verify the report'),
              GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
            ]);
          this.notVerifiedCtrl.get('otherDetails').updateValueAndValidity();
        } else {
          this.notVerifiedCtrl.get('otherDetails').clearValidators();
          this.notVerifiedCtrl.get('otherDetails').setValue(null);
        }
        this.notVerifiedCtrl.updateValueAndValidity();
      });
  }

  createNotVerifiedReasonsFormControl(notVerifiedValue: NotVerifiedValue) {
    return this.fb.group(
      {
        type: [
          notVerifiedValue.type,
          {
            validators: [
              GovukValidators.required(
                'Select at least one option from the list of reasons why you cannot verify the report',
              ),
            ],
          },
        ],
        otherDetails: [notVerifiedValue?.otherDetails],
        verificationDetails: [notVerifiedValue?.verificationDetails],
        clarityDetails: [notVerifiedValue?.clarityDetails],
        empLimitationDetails: [notVerifiedValue?.empLimitationDetails],
        empNotApprovedDetails: [notVerifiedValue?.empNotApprovedDetails],
      },
      { updateOn: 'change' },
    ) as FormGroup;
  }

  setFormValue(
    overallDecision:
      | AviationAerVerificationDecision
      | AviationAerVerifiedSatisfactoryWithCommentsDecision
      | AviationAerNotVerifiedDecision
      | undefined,
  ): void {
    const value: any = cloneDeep(overallDecision);
    if (value) {
      if (value?.reasons?.length) {
        this.form.setControl('reasons', this.fb.array(value?.reasons ?? [], [GovukValidators.required('')]));
      }

      const reasons =
        (overallDecision as AviationAerNotVerifiedDecision)?.notVerifiedReasons?.map((reason) => reason.type) ?? [];

      const otherDetails = (overallDecision as AviationAerNotVerifiedDecision)?.notVerifiedReasons?.find(
        (reason) => reason?.type === 'ANOTHER_REASON',
      ) ?? {
        details: null,
      };
      const verificationDetails = (overallDecision as AviationAerNotVerifiedDecision)?.notVerifiedReasons?.find(
        (reason) => reason?.type === 'VERIFICATION_DATA_OR_INFORMATION_LIMITATIONS',
      ) ?? {
        details: null,
      };
      const clarityDetails = (overallDecision as AviationAerNotVerifiedDecision)?.notVerifiedReasons?.find(
        (reason) => reason?.type === 'SCOPE_LIMITATIONS_DUE_TO_LACK_OF_CLARITY',
      ) ?? {
        details: null,
      };
      const empLimitationDetails = (overallDecision as AviationAerNotVerifiedDecision)?.notVerifiedReasons?.find(
        (reason) => reason?.type === 'SCOPE_LIMITATIONS_OF_APPROVED_MONITORING_PLAN',
      ) ?? {
        details: null,
      };
      const empNotApprovedDetails = (overallDecision as AviationAerNotVerifiedDecision)?.notVerifiedReasons?.find(
        (reason) => reason?.type === 'NOT_APPROVED_MONITORING_PLAN_BY_REGULATOR',
      ) ?? {
        details: null,
      };

      if (value?.notVerifiedReasons?.length) {
        this.form.setControl(
          'notVerifiedReasons',
          this.createNotVerifiedReasonsFormControl({
            type: reasons,
            otherDetails: otherDetails.details,
            verificationDetails: verificationDetails.details,
            clarityDetails: clarityDetails.details,
            empLimitationDetails: empLimitationDetails.details,
            empNotApprovedDetails: empNotApprovedDetails.details,
          }),
        );

        this.notVerifiedSub();
      }

      this.form.get('type').setValue(value.type);
    }
  }

  getFormValue():
    | AviationAerVerificationDecision
    | AviationAerVerifiedSatisfactoryWithCommentsDecision
    | AviationAerNotVerifiedDecision {
    return this.form.value;
  }
}
