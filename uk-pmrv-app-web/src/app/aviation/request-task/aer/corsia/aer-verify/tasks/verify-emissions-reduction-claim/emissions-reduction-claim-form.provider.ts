import { Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';

import { GovukValidators } from 'govuk-components';

import { AviationAerCorsiaEmissionsReductionClaimVerification } from 'pmrv-api';

export interface AviationAerCorsiaEmissionsReductionClaimVerificationFormModel {
  reviewResults: FormControl<string | null>;
}

@Injectable()
export class EmissionsReductionClaimFormProvider
  implements
    TaskFormProvider<
      AviationAerCorsiaEmissionsReductionClaimVerification,
      AviationAerCorsiaEmissionsReductionClaimVerificationFormModel
    >
{
  private _form: FormGroup<AviationAerCorsiaEmissionsReductionClaimVerificationFormModel>;

  constructor(private fb: FormBuilder) {}

  destroyForm(): void {
    this._form = null;
  }

  get form(): FormGroup<AviationAerCorsiaEmissionsReductionClaimVerificationFormModel> {
    if (!this._form) {
      this.buildForm();
    }

    return this._form;
  }

  getFormValue(): AviationAerCorsiaEmissionsReductionClaimVerification {
    return this.form.getRawValue();
  }

  setFormValue(formValue: AviationAerCorsiaEmissionsReductionClaimVerification): void {
    this.form.setValue(formValue);
  }

  private buildForm() {
    this._form = this.fb.group(
      {
        reviewResults: new FormControl<string>(null, {
          validators: [
            GovukValidators.required(
              "Enter the results of your verification of the operator's CORSIA eligible fuels claim",
            ),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        }),
      },
      { updateOn: 'change' },
    );
  }
}
