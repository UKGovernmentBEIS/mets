import { Injectable } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup } from '@angular/forms';

import { Subject } from 'rxjs';

import { GovukValidators } from 'govuk-components';

@Injectable()
export class AerVerificationReviewDecisionGroupFormProvider {
  private _form: FormGroup;

  constructor(private fb: FormBuilder) {}

  private destroy$ = new Subject<void>();

  get form(): FormGroup {
    if (!this._form) {
      this.buildForm();
    }

    return this._form;
  }

  setFormValue(reviewDecision: any) {
    this.form.setValue({
      type: reviewDecision?.type ?? null,
      notes: reviewDecision?.details?.notes ?? null,
    });
  }

  destroyForm() {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  private buildForm() {
    this._form = this.fb.group(
      {
        type: [null as string, [GovukValidators.required('Select a decision for this review group')]],
        notes: [null as string, [GovukValidators.maxLength(10000, 'Enter up to 10000 characters')]],
      },
      { updateOn: 'change' },
    );
  }

  private get typeCtrl(): AbstractControl {
    return this.form.get('type');
  }
}
