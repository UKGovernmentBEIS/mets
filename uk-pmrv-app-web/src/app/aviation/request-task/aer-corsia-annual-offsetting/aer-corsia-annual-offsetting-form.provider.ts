import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { Subject } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { AviationAerCorsiaAnnualOffsetting } from 'pmrv-api';

import { TaskFormProvider } from '../task-form.provider';

export interface AnnualOffsettingRequirementsFormModel {
  offsettingRequirements: FormGroup<Record<keyof AviationAerCorsiaAnnualOffsetting, FormControl>>;
}

@Injectable()
export class AnnualOffsettingRequirementsFormProvider
  implements TaskFormProvider<AviationAerCorsiaAnnualOffsetting, AnnualOffsettingRequirementsFormModel>
{
  private readonly fb = inject(FormBuilder);
  private _form: FormGroup<AnnualOffsettingRequirementsFormModel>;

  private readonly destroy$ = new Subject<void>();

  get form(): FormGroup<AnnualOffsettingRequirementsFormModel> {
    if (!this._form) {
      this._buildForm();
    }

    return this._form;
  }

  destroyForm() {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  setFormValue(formValues: AviationAerCorsiaAnnualOffsetting | undefined): void {
    if (formValues) {
      this.form.patchValue({ offsettingRequirements: formValues });
    }
  }

  getFormValue(): AviationAerCorsiaAnnualOffsetting {
    return this.form.value as any;
  }

  private _buildForm() {
    const maxValue = 2147483647;
    this._form = this.fb.group(
      {
        offsettingRequirements: new FormGroup<Record<keyof AviationAerCorsiaAnnualOffsetting, FormControl>>({
          schemeYear: new FormControl(null as number),
          totalChapter: new FormControl(null as number, [
            GovukValidators.required('Enter total chapter 3 emissions'),
            GovukValidators.positiveNumber('Enter a positive whole number'),
            GovukValidators.integerNumber(),
            GovukValidators.max(maxValue, `Total chapter 3 emissions must be less than ${maxValue}`),
          ]),
          sectorGrowth: new FormControl(null as number, [
            GovukValidators.required('Enter Sector growth value'),
            GovukValidators.maxDecimalsValidator(2),
            GovukValidators.max(maxValue, `Sector growth value must be less than ${maxValue}`),
          ]),
          calculatedAnnualOffsetting: new FormControl(null as number, [
            GovukValidators.required('Enter Calculate annual offsetting requirements'),
            GovukValidators.max(maxValue, `Calculate annual offsetting requirements must be less than ${maxValue}`),
          ]),
        }),
      },
      { updateOn: 'change' },
    );
  }
}
