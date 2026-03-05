import { Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';

import { GovukValidators } from 'govuk-components';

import { AviationAerCorsiaGeneralInformation } from 'pmrv-api';

export interface AviationAerCorsiaGeneralInformationFormModel {
  verificationCriteria: FormControl<string | null>;
  operatorData: FormControl<string | null>;
}

@Injectable()
export class GeneralInformationFormProvider
  implements TaskFormProvider<AviationAerCorsiaGeneralInformation, AviationAerCorsiaGeneralInformationFormModel>
{
  private _form: FormGroup<AviationAerCorsiaGeneralInformationFormModel>;

  constructor(private fb: FormBuilder) {}

  destroyForm(): void {
    this._form = null;
  }

  get form(): FormGroup<AviationAerCorsiaGeneralInformationFormModel> {
    if (!this._form) {
      this.buildForm();
    }

    return this._form;
  }

  getFormValue(): AviationAerCorsiaGeneralInformation {
    return this.form.getRawValue();
  }

  setFormValue(formValue: AviationAerCorsiaGeneralInformation): void {
    this.form.setValue({
      verificationCriteria: formValue?.verificationCriteria ?? null,
      operatorData: formValue?.operatorData ?? null,
    });
  }

  private buildForm() {
    this._form = this.fb.group(
      {
        verificationCriteria: new FormControl<string>(null, {
          validators: [
            GovukValidators.required('Enter the criteria that the emissions report was verified against'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        }),
        operatorData: new FormControl<string>(null, {
          validators: [
            GovukValidators.required('State which operator data you used to carry out the verification activities'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        }),
      },
      { updateOn: 'change' },
    );
  }
}
