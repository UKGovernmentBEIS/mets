import { Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';

import { GovukValidators } from 'govuk-components';

import { AviationAerCorsiaIndependentReview } from 'pmrv-api';

export interface AviationAerCorsiaIndependentReviewFormModel {
  reviewResults: FormControl<string | null>;
  name: FormControl<string | null>;
  position: FormControl<string | null>;
  email: FormControl<string | null>;
  line1: FormControl<string | null>;
  line2: FormControl<string | null>;
  city: FormControl<string | null>;
  state: FormControl<string | null>;
  postcode: FormControl<string | null>;
  country: FormControl<string | null>;
}

@Injectable()
export class IndependentReviewFormProvider
  implements TaskFormProvider<AviationAerCorsiaIndependentReview, AviationAerCorsiaIndependentReviewFormModel>
{
  private _form: FormGroup<AviationAerCorsiaIndependentReviewFormModel>;

  constructor(private fb: FormBuilder) {}

  destroyForm(): void {
    this._form = null;
  }

  get form(): FormGroup<AviationAerCorsiaIndependentReviewFormModel> {
    if (!this._form) {
      this.buildForm();
    }

    return this._form;
  }

  getFormValue(): AviationAerCorsiaIndependentReview {
    return this.form.getRawValue();
  }

  setFormValue(formValue: AviationAerCorsiaIndependentReview): void {
    this.form.setValue({
      reviewResults: formValue?.reviewResults ?? null,
      name: formValue?.name ?? null,
      position: formValue?.position ?? null,
      email: formValue?.email ?? null,
      line1: formValue?.line1 ?? null,
      line2: formValue?.line2 ?? null,
      city: formValue?.city ?? null,
      state: formValue?.state ?? null,
      postcode: formValue?.postcode ?? null,
      country: formValue?.country ?? null,
    });
  }

  private buildForm() {
    this._form = this.fb.group(
      {
        reviewResults: new FormControl<string>(null, {
          validators: [
            GovukValidators.required('Provide the results of your independent review'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        }),
        name: new FormControl<string>(null, {
          validators: [
            GovukValidators.required('Enter the name of the independent reviewer'),
            GovukValidators.maxLength(500, 'Name of the independent reviewer should not be more than 500 characters'),
          ],
        }),
        position: new FormControl<string>(null, {
          validators: [
            GovukValidators.required('Enter the position of the independent reviewer'),
            GovukValidators.maxLength(
              500,
              'Position of the independent reviewer should not be more than 500 characters',
            ),
          ],
        }),
        email: new FormControl<string>(null, {
          validators: [
            GovukValidators.required('Enter the email address of the independent reviewer'),
            GovukValidators.email('Enter an email address in the correct format, like name@example.com'),
            GovukValidators.maxLength(255, 'Email should not be more than 255 characters'),
          ],
        }),
        line1: new FormControl<string>(null, {
          validators: [
            GovukValidators.required('Enter the first line of your address'),
            GovukValidators.maxLength(255, 'First line of address should not be more than 255 characters'),
          ],
        }),
        line2: new FormControl<string>(null, {
          validators: [GovukValidators.maxLength(255, 'Second line of address should not be more than 255 characters')],
        }),
        city: new FormControl<string>(null, {
          validators: [
            GovukValidators.required('Enter your town or city'),
            GovukValidators.maxLength(255, 'Town or city should not be more than 255 characters'),
          ],
        }),
        state: new FormControl<string>(null, {
          validators: [GovukValidators.maxLength(255, 'State should not be more than 255 characters')],
        }),
        postcode: new FormControl<string>(null, {
          validators: [GovukValidators.maxLength(64, 'Postal code should not be more than 64 characters')],
        }),
        country: new FormControl<string>(null, {
          validators: [GovukValidators.required('Enter your country')],
        }),
      },
      { updateOn: 'change' },
    );
  }
}
