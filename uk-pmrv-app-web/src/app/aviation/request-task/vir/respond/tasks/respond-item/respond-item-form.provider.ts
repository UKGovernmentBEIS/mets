import { Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';

import { GovukValidators } from 'govuk-components';

import { OperatorImprovementFollowUpResponse } from 'pmrv-api';

export interface RespondItemFormModel {
  improvementCompleted: FormControl<boolean | null>;
  dateCompleted: FormControl<string | null>;
  reason: FormControl<string | null>;
}

@Injectable()
export class RespondItemFormProvider
  implements TaskFormProvider<OperatorImprovementFollowUpResponse, RespondItemFormModel>
{
  private _form: FormGroup<RespondItemFormModel>;
  private destroy$ = new Subject<void>();

  constructor(private fb: FormBuilder) {}

  destroyForm(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  get form(): FormGroup<RespondItemFormModel> {
    if (!this._form) {
      this.buildForm();
    }

    return this._form;
  }

  getFormValue(): OperatorImprovementFollowUpResponse {
    return this.form.value;
  }

  setFormValue(formValue: OperatorImprovementFollowUpResponse): void {
    this.form.setValue({
      improvementCompleted: formValue?.improvementCompleted ?? null,
      dateCompleted: formValue?.improvementCompleted
        ? formValue?.dateCompleted
          ? (new Date(formValue.dateCompleted) as any)
          : null
        : null,
      reason: !formValue?.improvementCompleted ? formValue?.reason ?? null : null,
    });
  }

  private buildForm() {
    this._form = this.fb.group(
      {
        improvementCompleted: new FormControl<boolean>(null, {
          validators: [GovukValidators.required('Select if the required improvement is complete')],
        }),
        dateCompleted: new FormControl<string>(null, {}),
        reason: new FormControl<string>(null, {
          validators: [
            GovukValidators.required('State why you have not addressed the recommendation'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        }),
      },
      { updateOn: 'change' },
    );

    this._form
      .get('improvementCompleted')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        if (value) {
          this._form.get('dateCompleted').enable();
          this._form.get('reason').disable();
          this._form.get('reason').setValue(null);
        } else {
          this._form.get('reason').enable();
          this._form.get('dateCompleted').disable();
          this._form.get('dateCompleted').setValue(null);
        }
      });
  }
}
