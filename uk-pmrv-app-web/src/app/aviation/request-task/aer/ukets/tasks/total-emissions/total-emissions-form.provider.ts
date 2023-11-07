import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { Subject } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { AviationAerTotalEmissionsConfidentiality } from 'pmrv-api';

import { TaskFormProvider } from '../../../../task-form.provider';

export interface TotalEmissionsFormModel {
  confidential: FormControl<boolean>;
}

@Injectable()
export class TotalEmissionsFormProvider
  implements TaskFormProvider<AviationAerTotalEmissionsConfidentiality, TotalEmissionsFormModel>
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

  setFormValue(emissionsConfidentiality: AviationAerTotalEmissionsConfidentiality | undefined): void {
    if (emissionsConfidentiality) {
      this.form.patchValue(emissionsConfidentiality);
    }
  }

  getFormValue(): AviationAerTotalEmissionsConfidentiality {
    return this.form.value as AviationAerTotalEmissionsConfidentiality;
  }

  private buildForm() {
    const formGroup = this.fb.group(
      {
        confidential: new FormControl<boolean | null>(null, {
          validators: [GovukValidators.required('Select yes or no')],
        }),
      },
      { updateOn: 'change' },
    );
    return (this._form = formGroup);
  }
}
