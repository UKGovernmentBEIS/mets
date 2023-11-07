import { Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';

import { GovukValidators } from 'govuk-components';

import { AviationAerCorsiaTimeAllocationScope } from 'pmrv-api';

export interface AviationAerCorsiaTimeAllocationScopeFormModel {
  verificationTotalTime: FormControl<string | null>;
  verificationScope: FormControl<string | null>;
}

@Injectable()
export class TimeAllocationFormProvider
  implements TaskFormProvider<AviationAerCorsiaTimeAllocationScope, AviationAerCorsiaTimeAllocationScopeFormModel>
{
  private _form: FormGroup<AviationAerCorsiaTimeAllocationScopeFormModel>;

  constructor(private fb: FormBuilder) {}

  destroyForm(): void {
    this._form = null;
  }

  get form(): FormGroup<AviationAerCorsiaTimeAllocationScopeFormModel> {
    if (!this._form) {
      this.buildForm();
    }

    return this._form;
  }

  getFormValue(): AviationAerCorsiaTimeAllocationScope {
    return this.form.getRawValue();
  }

  setFormValue(formValue: AviationAerCorsiaTimeAllocationScope): void {
    this.form.setValue({
      verificationTotalTime: formValue?.verificationTotalTime ?? null,
      verificationScope: formValue?.verificationScope ?? null,
    });
  }

  private buildForm() {
    this._form = this.fb.group(
      {
        verificationTotalTime: new FormControl<string>(null, {
          validators: [
            GovukValidators.required(
              'Enter the total time required to carry out the verification, including any revisions',
            ),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        }),
        verificationScope: new FormControl<string>(null, {
          validators: [
            GovukValidators.required('Enter the scope of the verification'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        }),
      },
      { updateOn: 'change' },
    );
  }
}
