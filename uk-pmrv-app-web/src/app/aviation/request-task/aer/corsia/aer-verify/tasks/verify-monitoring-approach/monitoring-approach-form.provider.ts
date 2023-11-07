import { Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';

import { GovukValidators } from 'govuk-components';

import { AviationAerCorsiaOpinionStatement } from 'pmrv-api';

export interface AviationAerCorsiaOpinionStatementFormModel {
  fuelTypes: FormControl<AviationAerCorsiaOpinionStatement['fuelTypes'] | null>;
  monitoringApproachType: FormControl<AviationAerCorsiaOpinionStatement['monitoringApproachType'] | null>;
  emissionsCorrect: FormControl<boolean | null>;
  manuallyInternationalFlightsProvidedEmissions: FormControl<
    AviationAerCorsiaOpinionStatement['manuallyInternationalFlightsProvidedEmissions'] | null
  >;
  manuallyOffsettingFlightsProvidedEmissions: FormControl<
    AviationAerCorsiaOpinionStatement['manuallyOffsettingFlightsProvidedEmissions'] | null
  >;
}

@Injectable()
export class MonitoringApproachFormProvider
  implements TaskFormProvider<AviationAerCorsiaOpinionStatement, AviationAerCorsiaOpinionStatementFormModel>
{
  private _form: FormGroup<AviationAerCorsiaOpinionStatementFormModel>;
  private destroy$ = new Subject<void>();

  constructor(private fb: FormBuilder) {}

  destroyForm(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  get form(): FormGroup<AviationAerCorsiaOpinionStatementFormModel> {
    if (!this._form) {
      this.buildForm();
    }

    return this._form;
  }

  getFormValue(): AviationAerCorsiaOpinionStatement {
    return this.form.getRawValue();
  }

  setFormValue(formValue: AviationAerCorsiaOpinionStatement): void {
    this.form.setValue({
      fuelTypes: formValue?.fuelTypes ?? null,
      monitoringApproachType: formValue?.monitoringApproachType ?? null,
      emissionsCorrect: formValue?.emissionsCorrect ?? null,
      manuallyInternationalFlightsProvidedEmissions: formValue?.manuallyInternationalFlightsProvidedEmissions ?? null,
      manuallyOffsettingFlightsProvidedEmissions: formValue?.manuallyOffsettingFlightsProvidedEmissions ?? null,
    });
  }

  private buildForm() {
    this._form = this.fb.group(
      {
        fuelTypes: new FormControl<AviationAerCorsiaOpinionStatement['fuelTypes']>(null, {
          validators: [GovukValidators.required('Select at least one fuel type')],
        }),
        monitoringApproachType: new FormControl<AviationAerCorsiaOpinionStatement['monitoringApproachType']>(null, {
          validators: [GovukValidators.required('Select one emissions monitoring approach')],
        }),
        emissionsCorrect: new FormControl<boolean>(null, {
          validators: [GovukValidators.required('Select if the reported emissions are correct')],
        }),
        manuallyInternationalFlightsProvidedEmissions: new FormControl<
          AviationAerCorsiaOpinionStatement['manuallyInternationalFlightsProvidedEmissions']
        >(null, {
          validators: [
            GovukValidators.required(
              'Enter the total verified emissions from all international flights for the scheme year',
            ),
            GovukValidators.naturalNumber('Enter a whole number without decimal places (you can use zero)'),
          ],
        }),
        manuallyOffsettingFlightsProvidedEmissions: new FormControl<
          AviationAerCorsiaOpinionStatement['manuallyOffsettingFlightsProvidedEmissions']
        >(null, {
          validators: [
            GovukValidators.required(
              'Enter the total verified emissions from flights with offsetting requirements for the scheme year',
            ),
            GovukValidators.naturalNumber('Enter a whole number without decimal places (you can use zero)'),
          ],
        }),
      },
      { updateOn: 'change' },
    );

    this._form
      .get('emissionsCorrect')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        if (value) {
          this._form.get('manuallyInternationalFlightsProvidedEmissions').disable();
          this._form.get('manuallyInternationalFlightsProvidedEmissions').setValue(null);
          this._form.get('manuallyOffsettingFlightsProvidedEmissions').disable();
          this._form.get('manuallyOffsettingFlightsProvidedEmissions').setValue(null);
        } else {
          this._form.get('manuallyInternationalFlightsProvidedEmissions').enable();
          this._form.get('manuallyOffsettingFlightsProvidedEmissions').enable();
        }
      });
  }
}
