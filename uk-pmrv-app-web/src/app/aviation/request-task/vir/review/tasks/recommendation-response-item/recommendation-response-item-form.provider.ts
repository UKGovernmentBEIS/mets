import { Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';

import { GovukValidators } from 'govuk-components';

import { RegulatorImprovementResponse } from 'pmrv-api';

export interface RecommendationResponseItemFormModel {
  improvementRequired: FormControl<boolean | null>;
  improvementDeadline: FormControl<string | null>;
  improvementComments: FormControl<string | null>;
  operatorActions: FormControl<string | null>;
}

@Injectable()
export class RecommendationResponseItemFormProvider
  implements TaskFormProvider<RegulatorImprovementResponse, RecommendationResponseItemFormModel>
{
  private _form: FormGroup<RecommendationResponseItemFormModel>;
  private destroy$ = new Subject<void>();

  constructor(private fb: FormBuilder) {}

  destroyForm(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  get form(): FormGroup<RecommendationResponseItemFormModel> {
    if (!this._form) {
      this.buildForm();
    }

    return this._form;
  }

  getFormValue(): RegulatorImprovementResponse {
    return this.form.getRawValue();
  }

  setFormValue(formValue: RegulatorImprovementResponse): void {
    this.form.setValue({
      improvementRequired: formValue?.improvementRequired ?? null,
      improvementDeadline: formValue?.improvementDeadline
        ? formValue?.improvementDeadline
          ? (new Date(formValue.improvementDeadline) as any)
          : null
        : null,
      improvementComments: formValue?.improvementComments ?? null,
      operatorActions: formValue?.operatorActions ?? null,
    });
  }

  private buildForm() {
    this._form = this.fb.group(
      {
        improvementRequired: new FormControl<boolean>(null, {
          validators: [GovukValidators.required('Select if the improvement is required')],
        }),
        improvementDeadline: new FormControl<string>(null, {}),
        improvementComments: new FormControl<string>(null, {
          validators: [GovukValidators.maxLength(10000, 'Enter up to 10000 characters')],
        }),
        operatorActions: new FormControl<string>(null, {
          validators: [
            GovukValidators.required('Enter your actions for the operator'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        }),
      },
      { updateOn: 'change' },
    );

    this._form
      .get('improvementRequired')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        if (value) {
          this._form.get('improvementDeadline').enable();
        } else {
          this._form.get('improvementDeadline').disable();
          this._form.get('improvementDeadline').setValue(null);
        }
      });
  }
}
