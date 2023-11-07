import { Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';

import { GovukValidators } from 'govuk-components';

export interface CreateSummaryFormModel {
  reportSummary: FormControl<string | null>;
}

@Injectable()
export class CreateSummaryFormProvider implements TaskFormProvider<string, CreateSummaryFormModel> {
  private _form: FormGroup<CreateSummaryFormModel>;

  constructor(private fb: FormBuilder) {}

  destroyForm(): void {
    this._form = null;
  }

  get form(): FormGroup<CreateSummaryFormModel> {
    if (!this._form) {
      this.buildForm();
    }

    return this._form;
  }

  getFormValue(): string {
    return this.form.get('reportSummary').value;
  }

  setFormValue(formValue: string): void {
    this.form.setValue({
      reportSummary: formValue,
    });
  }

  private buildForm() {
    this._form = this.fb.group(
      {
        reportSummary: new FormControl<string>(null, {
          validators: [
            GovukValidators.required('Enter your summary response for the operator'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        }),
      },
      { updateOn: 'change' },
    );
  }
}
