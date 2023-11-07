import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { Subject } from 'rxjs';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';

import { GovukValidators } from 'govuk-components';

import { EmpIssuanceDetermination, EmpVariationDetermination } from 'pmrv-api';

interface OverallDecisionFormModel {
  reason: FormControl<EmpIssuanceDetermination['reason'] | null>;
  type: FormControl<EmpIssuanceDetermination['type'] | EmpVariationDetermination['type'] | null>;
}

@Injectable()
export class OverallDecisionFormProvider
  implements TaskFormProvider<EmpIssuanceDetermination | EmpVariationDetermination, OverallDecisionFormModel>
{
  private fb = inject(FormBuilder);
  private _form: FormGroup;

  private destroy$ = new Subject<void>();

  get form(): FormGroup<OverallDecisionFormModel> {
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

  setFormValue(decision: EmpIssuanceDetermination | undefined | EmpVariationDetermination): void {
    if (decision) {
      this.form.patchValue(decision);
    }
  }

  private buildForm() {
    this._form = this.fb.group<OverallDecisionFormModel>(
      {
        type: new FormControl(null, { validators: GovukValidators.required('Type must not be empy') }),
        reason: new FormControl(null, { validators: GovukValidators.maxLength(10000, 'Enter up to 10000 characters') }),
      },
      { updateOn: 'change' },
    );
  }
}
