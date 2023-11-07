import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';

import { Subject } from 'rxjs';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';

import { GovukValidators } from 'govuk-components';

import { AviationAerCorsiaDataGaps, AviationAerCorsiaDataGapsDetails, AviationAerDataGap } from 'pmrv-api';

export interface AviationAerCorsiaDataGapsFormModel {
  exist: FormControl<boolean | null>;
  dataGapsDetails?: FormGroup<AviationAerCorsiaDataGapsDetailsFormModel | null>;
}

export interface AviationAerCorsiaDataGapsDetailsFormModel {
  dataGapsPercentageType: FormControl<AviationAerCorsiaDataGapsDetails['dataGapsPercentageType'] | null>;
  dataGapsPercentage?: FormControl<AviationAerCorsiaDataGapsDetails['dataGapsPercentage'] | null>;
  dataGaps?: FormGroup<AviationAerCorsiaDataGapFormModel | null>;
  affectedFlightsPercentage?: FormControl<string | null>;
}

export interface AviationAerCorsiaDataGapFormModel {
  reason: FormControl<AviationAerDataGap['reason'] | null>;
  type?: FormControl<AviationAerDataGap['type'] | null>;
  replacementMethod?: FormControl<AviationAerDataGap['replacementMethod'] | null>;
  flightsAffected?: FormControl<AviationAerDataGap['flightsAffected'] | null>;
  totalEmissions?: FormControl<AviationAerDataGap['totalEmissions'] | null>;
}

@Injectable()
export class DataGapsFormProvider
  implements TaskFormProvider<AviationAerCorsiaDataGaps, AviationAerCorsiaDataGapsFormModel>
{
  private fb = inject(FormBuilder);
  private _form: FormGroup<AviationAerCorsiaDataGapsFormModel>;
  private destroy$ = new Subject<void>();

  get form() {
    if (!this._form) {
      this._buildForm();
    }

    return this._form;
  }

  get existCtrl() {
    return this.form.get('exist');
  }

  get dataGapsDetailsCtrl(): FormGroup {
    return this.form.get('dataGapsDetails') as FormGroup;
  }

  destroyForm() {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  get dataGapsPercentageTypeCtrl(): FormControl {
    return this.form.controls.dataGapsDetails.controls?.dataGapsPercentageType;
  }

  get dataGapsPercentageCtrl(): FormControl {
    return this.form.controls.dataGapsDetails.controls?.dataGapsPercentage;
  }

  get dataGapsCtrl(): FormGroup {
    return this.form.controls.dataGapsDetails.controls?.dataGaps;
  }

  setFormValue(aviationAerDataGaps: AviationAerCorsiaDataGaps | undefined) {
    if (aviationAerDataGaps) {
      this.existCtrl.setValue(aviationAerDataGaps.exist);
      if (aviationAerDataGaps.exist && this.dataGapsDetailsCtrl) {
        if (aviationAerDataGaps?.dataGapsDetails?.dataGapsPercentageType)
          this.dataGapsDetailsCtrl.controls?.dataGapsPercentageType?.patchValue(
            aviationAerDataGaps.dataGapsDetails.dataGapsPercentageType,
          );

        if (aviationAerDataGaps?.dataGapsDetails?.dataGapsPercentageType === 'LESS_EQUAL_FIVE_PER_CENT') {
          this.dataGapsDetailsCtrl.controls?.dataGapsPercentage?.patchValue(
            aviationAerDataGaps.dataGapsDetails.dataGapsPercentage,
          );
          this.dataGapsDetailsCtrl.controls?.dataGaps.patchValue(null);
          this.dataGapsDetailsCtrl.controls?.affectedFlightsPercentage.patchValue(null);
        } else if (aviationAerDataGaps?.dataGapsDetails?.dataGapsPercentageType === 'MORE_THAN_FIVE_PER_CENT') {
          this.dataGapsDetailsCtrl.controls?.dataGaps.patchValue(aviationAerDataGaps.dataGapsDetails.dataGaps ?? []);
          this.dataGapsDetailsCtrl.controls?.affectedFlightsPercentage.patchValue(
            aviationAerDataGaps.dataGapsDetails.affectedFlightsPercentage,
          );
        }

        this.dataGapsDetailsCtrl.updateValueAndValidity();
      } else if (this.dataGapsDetailsCtrl) {
        this.removeDataGaps();
        this.dataGapsDetailsCtrl.updateValueAndValidity();
      }
    }
  }

  getFormValue(): AviationAerCorsiaDataGaps {
    return this.form.value as AviationAerCorsiaDataGaps;
  }

  private _buildForm() {
    this._form = this.fb.group(
      {
        exist: new FormControl<boolean | null>(null, {
          validators: [
            GovukValidators.required(
              'Select if there have been any data gaps for flights with offsetting obligations during the scheme year',
            ),
          ],
          updateOn: 'change',
        }),
      },
      { updateOn: 'change' },
    );

    this.form.controls.exist.valueChanges.subscribe((val) => {
      if (val) {
        this.form.addControl('dataGapsDetails', this.createDataGapsDetails());
      } else {
        this.form.removeControl('dataGapsDetails');
      }
    });
  }

  removeDataGaps() {
    if (this.form.contains('dataGapsDetails')) {
      this.form.removeControl('dataGapsDetails');
    }
  }

  createDataGapsDetails(): FormGroup {
    const detailsGroup = this.fb.group(
      {
        dataGapsPercentageType: new FormControl<AviationAerCorsiaDataGapsDetails['dataGapsPercentageType'] | null>(
          null,
          [GovukValidators.required('Select if more or less than 5% of these flights had data gaps')],
        ),
        dataGapsPercentage: new FormControl<AviationAerCorsiaDataGapsDetails['dataGapsPercentage'] | null>(null),
        dataGaps: new FormControl<AviationAerCorsiaDataGapsDetails['dataGaps'] | null>(null),
        affectedFlightsPercentage: new FormControl<
          AviationAerCorsiaDataGapsDetails['affectedFlightsPercentage'] | null
        >(null),
      },
      { updateOn: 'change' },
    );

    detailsGroup.controls.dataGapsPercentageType.valueChanges.subscribe((val) => {
      if (val === 'LESS_EQUAL_FIVE_PER_CENT') {
        detailsGroup.controls.dataGapsPercentage.setValidators([
          GovukValidators.required('Enter a percentage for the amount of data gaps'),
          GovukValidators.positiveNumber('Enter a number greater than 0 and up to 5'),
          GovukValidators.max(5, 'Enter a number greater than 0 and up to 5'),
          GovukValidators.maxDecimalsValidator(3),
        ]);
        detailsGroup.controls.dataGaps.clearValidators();
        detailsGroup.controls.dataGapsPercentage.updateValueAndValidity();
      } else {
        detailsGroup.controls.dataGapsPercentage.clearValidators();
        detailsGroup.setControl('dataGaps', this.fb.control([], [Validators.required, Validators.minLength(1)]));
      }
    });
    return detailsGroup;
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
