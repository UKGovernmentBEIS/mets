import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { EmpApplicationTimeframeInfo } from 'pmrv-api';

import { TaskFormProvider } from '../../../../task-form.provider';

export interface EmpApplicationTimeframeFormModel {
  dateOfStart: FormControl<string | null>;
  submittedOnTime: FormControl<boolean | null>;
  reasonForLateSubmission?: FormControl<string | null>;
}

@Injectable()
export class ApplicationTimeframeFormProvider
  implements TaskFormProvider<EmpApplicationTimeframeInfo, EmpApplicationTimeframeFormModel>
{
  private fb = inject(FormBuilder);
  private _form: FormGroup;

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

  setFormValue(applicationTimeframeInfo: EmpApplicationTimeframeInfo | undefined): void {
    if (applicationTimeframeInfo) {
      this.form.patchValue({
        ...applicationTimeframeInfo,
        dateOfStart: applicationTimeframeInfo?.dateOfStart
          ? (new Date(applicationTimeframeInfo.dateOfStart) as any)
          : null,
      });
    }
  }

  private buildForm() {
    const formGroup = this.fb.group(
      {
        dateOfStart: new FormControl<string | Date | null>(null, {
          validators: GovukValidators.required('Enter the date when you became a UK ETS aircraft operator'),
        }),
        submittedOnTime: new FormControl<boolean | null>(null, {
          validators: GovukValidators.required(
            'Say if you are submitting your application within 42 days of the date you became a UK ETS aircraft operator',
          ),
        }),
        reasonForLateSubmission: new FormControl<string | null>(null),
      },
      { updateOn: 'change' },
    );

    const submittedOnTime = formGroup.get('submittedOnTime');
    const reasonForLateSubmission = formGroup.get('reasonForLateSubmission');

    submittedOnTime.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((value) => {
      if (value) {
        reasonForLateSubmission.clearValidators();
        reasonForLateSubmission.setValue(null);
      } else {
        reasonForLateSubmission.setValidators([
          GovukValidators.required('Enter a reason why your application was not submitted within 42 days'),
          GovukValidators.maxLength(2000, 'Enter up to 2000 characters'),
        ]);
      }
      reasonForLateSubmission.updateValueAndValidity();
    });

    return (this._form = formGroup);
  }
}
