import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';

import { Subject } from 'rxjs';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';

import { GovukValidators } from 'govuk-components';

import { AviationAerDataGap, AviationAerDataGaps } from 'pmrv-api';

import { AviationAerDataGapsFormModel } from './data-gaps.interface';

@Injectable()
export class DataGapsFormProvider implements TaskFormProvider<AviationAerDataGaps, AviationAerDataGapsFormModel> {
  private fb = inject(FormBuilder);
  private _form: FormGroup<AviationAerDataGapsFormModel>;
  private destroy$ = new Subject<void>();

  get form() {
    if (!this._form) {
      this._buildForm();
    }

    return this._form;
  }

  get existGroup() {
    return this.form.get('existGroup') as FormGroup;
  }

  get existCtrl() {
    return this.existGroup.get('exist');
  }

  get dataGapsGroup() {
    return this.form.get('dataGapsGroup') as FormGroup;
  }

  get dataGapsCtrl() {
    return this.dataGapsGroup.get('dataGaps');
  }

  get affectedFlightsPercentageCtrl() {
    return this.form.get('affectedFlightsPercentage');
  }

  destroyForm() {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  setFormValue(aviationAerDataGaps: AviationAerDataGaps | undefined) {
    if (aviationAerDataGaps) {
      this.existCtrl.setValue(aviationAerDataGaps.exist);

      if (aviationAerDataGaps.exist) {
        this.addDataGaps();

        this.dataGapsCtrl.setValue(aviationAerDataGaps.dataGaps);
        this.affectedFlightsPercentageCtrl.setValue(aviationAerDataGaps.affectedFlightsPercentage);
      } else {
        this.removeDataGaps();
      }
    }
  }

  getFormValue(): AviationAerDataGaps {
    const value: AviationAerDataGaps = {
      exist: this.existCtrl.value,
    };

    if (this.existCtrl.value) {
      value.dataGaps = this.dataGapsCtrl.value;
      value.affectedFlightsPercentage = this.affectedFlightsPercentageCtrl.value;
    }

    return value;
  }

  private _buildForm() {
    this._form = this.fb.group(
      {
        existGroup: this.fb.group({
          exist: new FormControl<AviationAerDataGaps['exist'] | null>(null, {
            validators: [
              GovukValidators.required('Select yes or no if there have been any data gaps during the scheme year'),
            ],
            updateOn: 'change',
          }),
        }),
      },
      { updateOn: 'change' },
    );
  }

  addDataGaps() {
    if (!this.form.contains('dataGapsGroup')) {
      this.form.addControl(
        'dataGapsGroup',
        this.fb.group({
          dataGaps: this.fb.control([], [Validators.required, Validators.minLength(1)]),
        }),
      );
    }

    if (!this.form.contains('affectedFlightsPercentage')) {
      this.form.addControl('affectedFlightsPercentage', new FormControl<string | null>(null));
    }
  }

  removeDataGaps() {
    if (this.form.contains('dataGapsGroup')) {
      this.form.removeControl('dataGapsGroup');
    }

    if (this.form.contains('affectedFlightsPercentage')) {
      this.form.removeControl('affectedFlightsPercentage');
    }
  }

  createDataGapGroup(): FormGroup {
    return this.fb.group(
      {
        reason: new FormControl<AviationAerDataGap['reason'] | null>(null, [
          GovukValidators.required('Enter a reason for the data gap'),
          GovukValidators.maxLength(2000, 'The description should not be more than 2000 characters'),
        ]),
        type: new FormControl<AviationAerDataGap['type'] | null>(null, [
          GovukValidators.required('Enter the type of the data gap'),
          GovukValidators.maxLength(500, 'The description should not be more than 500 characters'),
        ]),
        replacementMethod: new FormControl<AviationAerDataGap['replacementMethod'] | null>(null, [
          GovukValidators.required('Enter the replacement method used for determining surrogate data'),
          GovukValidators.maxLength(2000, 'The description should not be more than 2000 characters'),
        ]),
        flightsAffected: new FormControl<AviationAerDataGap['flightsAffected'] | null>(null, [
          GovukValidators.required('Enter the number of flights affected by the data gap'),
          GovukValidators.positiveNumber('You must enter a positive number'),
          GovukValidators.integerNumber(),
        ]),
        totalEmissions: new FormControl<AviationAerDataGap['totalEmissions'] | null>(null, [
          GovukValidators.required('Enter the total emissions affected by the data gap'),
          GovukValidators.positiveNumber('You must enter a positive number'),
          GovukValidators.maxDecimalsValidator(3),
        ]),
      },
      { updateOn: 'change' },
    );
  }
}
