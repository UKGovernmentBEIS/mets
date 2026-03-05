import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ValidatorFn } from '@angular/forms';

import { Subject } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import {
  AviationAerCorsia3YearPeriodOffsetting,
  AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData,
} from 'pmrv-api';

import { TaskFormProvider } from '../task-form.provider';

export interface ThreeYearOffsettingRequirementsFormModel {
  offsettingRequirements: FormGroup<Record<keyof AviationAerCorsia3YearPeriodOffsetting, FormControl | FormGroup>>;
}

interface YearlyOffsettingDataValidator {
  calculatedAnnualOffsetting?: Array<ValidatorFn>;
  cefEmissionsReductions?: Array<ValidatorFn>;
}

@Injectable()
export class ThreeYearOffsettingRequirementsFormProvider
  implements TaskFormProvider<AviationAerCorsia3YearPeriodOffsetting, ThreeYearOffsettingRequirementsFormModel>
{
  private readonly fb = inject(FormBuilder);
  private _form: FormGroup<ThreeYearOffsettingRequirementsFormModel>;

  private readonly destroy$ = new Subject<void>();
  private schemeYears: AviationAerCorsia3YearPeriodOffsetting['schemeYears'];

  get form(): FormGroup<ThreeYearOffsettingRequirementsFormModel> {
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

  setFormValue(formValues: AviationAerCorsia3YearPeriodOffsetting | undefined): void {
    if (formValues) {
      this.schemeYears = formValues.schemeYears;
      this.form.patchValue({ offsettingRequirements: formValues });
    }
  }

  getFormValue(): AviationAerCorsia3YearPeriodOffsetting {
    return this.form.value as any;
  }

  private createYearlyOffsettingDataFormGroup(yearlyOffsettingDataValidator = {} as YearlyOffsettingDataValidator) {
    return new FormGroup<Record<keyof AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData, FormControl<number>>>(
      {
        calculatedAnnualOffsetting: new FormControl(
          null as number,
          yearlyOffsettingDataValidator?.calculatedAnnualOffsetting,
        ),
        cefEmissionsReductions: new FormControl(null as number, yearlyOffsettingDataValidator?.cefEmissionsReductions),
      },
    );
  }

  private readonly yearlyOffsettingDataValidators = (): YearlyOffsettingDataValidator => {
    return {
      calculatedAnnualOffsetting: [
        GovukValidators.required('Enter calculated annual offsetting requirement'),
        GovukValidators.integerNumber('Enter positive whole number'),
      ],
      cefEmissionsReductions: [
        GovukValidators.required('Enter CEF emissions reductions'),
        GovukValidators.integerNumber('Enter positive whole number'),
      ],
    };
  };

  private _buildForm() {
    this._form = this.fb.group(
      {
        offsettingRequirements: new FormGroup<
          Record<keyof AviationAerCorsia3YearPeriodOffsetting, FormControl | FormGroup>
        >({
          schemeYears: new FormControl([] as AviationAerCorsia3YearPeriodOffsetting['schemeYears']),
          yearlyOffsettingData: new FormGroup<
            Record<keyof AviationAerCorsia3YearPeriodOffsetting['yearlyOffsettingData'], FormGroup>
          >({
            [this.schemeYears[0]]: this.createYearlyOffsettingDataFormGroup(this.yearlyOffsettingDataValidators()),
            [this.schemeYears[1]]: this.createYearlyOffsettingDataFormGroup(this.yearlyOffsettingDataValidators()),
            [this.schemeYears[2]]: this.createYearlyOffsettingDataFormGroup(this.yearlyOffsettingDataValidators()),
          }),
          totalYearlyOffsettingData: this.createYearlyOffsettingDataFormGroup(),
          periodOffsettingRequirements: new FormControl(
            null as number,
            GovukValidators.required('Calculate Period offsetting requirements'),
          ),
          operatorHaveOffsettingRequirements: new FormControl(
            null as boolean,
            GovukValidators.required('Select Yes or No'),
          ),
        }),
      },
      { updateOn: 'change' },
    );
  }
}
