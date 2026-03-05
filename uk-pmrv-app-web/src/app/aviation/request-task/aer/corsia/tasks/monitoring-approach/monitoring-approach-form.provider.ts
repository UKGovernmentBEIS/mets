import { inject, Injectable } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import {
  atLeastOneMonitoringDetailsValidator,
  csvFieldMaxLengthValidator,
  csvFieldPositiveMaxDecimalsValidator,
  csvFieldRequiredValidator,
} from '@aviation/request-task/aer/corsia/tasks/monitoring-approach/validators';
import { TaskFormProvider } from '@aviation/request-task/task-form.provider';

import { GovukValidators } from 'govuk-components';

import {
  AviationAerCorsiaAircraftTypeDetails,
  AviationAerCorsiaCertDetails,
  AviationAerCorsiaFuelUseMonitoringDetails,
  AviationAerCorsiaMonitoringApproach,
} from 'pmrv-api';

import { DateInputValidators } from '../../../../../../../../projects/govuk-components/src/lib/date-input/date-input.validators';

export interface AviationAerCorsiaCertDetailsFormModel {
  flightType: FormControl<AviationAerCorsiaCertDetails['flightType'] | null>;
  publicationYear: FormControl<number | null>;
}

export interface AviationAerCorsiaAircraftTypeDetailsFormModel {
  designator: FormControl<string | null>;
  subtype?: FormControl<string | null>;
  fuelBurnRatio: FormControl<string | null>;
}

export type AircraftTypeDetailsFormArray = FormArray<FormGroup<AviationAerCorsiaAircraftTypeDetailsFormModel>>;

export interface AviationAerCorsiaFuelUseMonitoringDetailsFormModel {
  fuelDensityType: FormControl<AviationAerCorsiaFuelUseMonitoringDetails['fuelDensityType'] | null>;
  identicalToProcedure?: FormControl<boolean | null>;
  blockHourUsed?: FormControl<boolean | null>;
  aircraftTypeDetails?: AircraftTypeDetailsFormArray;
}

export interface AviationAerCorsiaMonitoringApproachFormModel {
  certUsed: FormControl<boolean | null>;
  certDetails?: FormGroup<AviationAerCorsiaCertDetailsFormModel>;
  fuelUseMonitoringDetails?: FormGroup<AviationAerCorsiaFuelUseMonitoringDetailsFormModel>;
}

@Injectable()
export class AviationAerCorsiaMonitoringApproachFormProvider
  implements TaskFormProvider<AviationAerCorsiaMonitoringApproach, AviationAerCorsiaMonitoringApproachFormModel>
{
  private fb = inject(FormBuilder);
  private _form: FormGroup<AviationAerCorsiaMonitoringApproachFormModel>;
  private destroy$ = new Subject<void>();

  get form(): FormGroup<AviationAerCorsiaMonitoringApproachFormModel> {
    if (!this._form) {
      this.buildForm();
    }

    return this._form;
  }

  getFormAsGroup(section: keyof AviationAerCorsiaMonitoringApproachFormModel | 'aircraftTypeDetails') {
    switch (section) {
      case 'certUsed':
        return this.fb.group({
          certUsed: this.form.controls[section] as FormControl<boolean | null>,
        });
      case 'fuelUseMonitoringDetails': {
        const fuelUseMonitoringDetailsFormGroup = this.form.controls?.fuelUseMonitoringDetails;
        return this.getFuelUseMonitoringDetailsFormGroup({
          fuelDensityType: fuelUseMonitoringDetailsFormGroup?.controls?.fuelDensityType.value,
          identicalToProcedure: fuelUseMonitoringDetailsFormGroup?.controls?.identicalToProcedure.value,
          blockHourUsed: fuelUseMonitoringDetailsFormGroup?.controls?.blockHourUsed.value,
        });
      }
      case 'aircraftTypeDetails': {
        return this.fb.group({
          aircraftTypeDetails: this.form.controls.fuelUseMonitoringDetails.controls.aircraftTypeDetails,
        });
      }
      default:
        return this.form.controls[section];
    }
  }

  setFormValue(monitoringApproach: AviationAerCorsiaMonitoringApproach | undefined): void {
    if (monitoringApproach) {
      this.enableOptionalFormGroups(
        monitoringApproach?.certUsed,
        monitoringApproach?.fuelUseMonitoringDetails?.aircraftTypeDetails,
      );
      this.form.patchValue(monitoringApproach as any);
      this.form.updateValueAndValidity();
    }
  }

  destroyForm() {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  enableOptionalFormGroups(certUsed?: boolean, aircraftTypeDetails?: AviationAerCorsiaAircraftTypeDetails[]) {
    if (certUsed === true) {
      this.addCertDetailsCtrl();
      this.addFuelUseMonitoringDetailsCtrl(aircraftTypeDetails);
    } else if (certUsed === false) {
      this.addFuelUseMonitoringDetailsCtrl(aircraftTypeDetails);
      this.removeCertDetailsCtrl();
    }
  }

  private addCertDetailsCtrl(): void {
    const certDetailsFormGroup = this.fb.group(
      {
        publicationYear: this.fb.control<number | null>(null, {
          validators: [
            GovukValidators.required('Enter the year of the CERT version you used'),
            GovukValidators.pattern('[0-9]{4}', 'Enter a full date'),
            GovukValidators.builder(
              `Enter a real date`,
              DateInputValidators.dateFieldValidator('publicationYear', 1900, 2100),
            ),
          ],
        }),
        flightType: this.fb.control<AviationAerCorsiaCertDetails['flightType'] | null>(null, {
          validators: GovukValidators.required('Select which type of flights you have used the CERT for'),
        }),
      },
      {
        updateOn: 'change',
      },
    );
    certDetailsFormGroup.controls.flightType.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((value) => {
      if (value === 'ALL_INTERNATIONAL_FLIGHTS') {
        this.removeFuelUseMonitoringDetailsCtrl();
      }
    });
    if (!this.form.contains('certDetails')) {
      this.form.addControl('certDetails', certDetailsFormGroup);
    }
  }

  private removeCertDetailsCtrl() {
    if (this.form.contains('certDetails')) {
      this.form.removeControl('certDetails');
    }
  }

  private getFuelUseMonitoringDetailsFormGroup(
    aviationAerCorsiaFuelUseMonitoringDetails?: Omit<AviationAerCorsiaFuelUseMonitoringDetails, 'aircraftTypeDetails'>,
  ): FormGroup<Omit<AviationAerCorsiaFuelUseMonitoringDetailsFormModel, 'aircraftTypeDetails'>> {
    const fuelUseMonitoringDetailsFormGroup = this.fb.group<AviationAerCorsiaFuelUseMonitoringDetailsFormModel>(
      {
        fuelDensityType: this.fb.control<AviationAerCorsiaFuelUseMonitoringDetails['fuelDensityType'] | null>(
          aviationAerCorsiaFuelUseMonitoringDetails?.fuelDensityType ?? null,
          {
            validators: GovukValidators.required(
              'Select which fuel density type was used to determine fuel uplift in the reporting year',
            ),
          },
        ),
        identicalToProcedure: this.fb.control<boolean | null>(
          aviationAerCorsiaFuelUseMonitoringDetails?.identicalToProcedure ?? null,
        ),
        blockHourUsed: this.fb.control<boolean | null>(
          aviationAerCorsiaFuelUseMonitoringDetails?.blockHourUsed ?? null,
        ),
      },
      { updateOn: 'change' },
    );

    fuelUseMonitoringDetailsFormGroup.controls.fuelDensityType.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        if (value === 'ACTUAL_DENSITY' || value === 'ACTUAL_STANDARD_DENSITY' || value === 'STANDARD_DENSITY') {
          fuelUseMonitoringDetailsFormGroup.controls.identicalToProcedure.setValidators(
            GovukValidators.required(
              'Select if the application of density data is identical to the EMP procedure used for operational and safety reasons',
            ),
          );
          fuelUseMonitoringDetailsFormGroup.controls.blockHourUsed.setValidators(
            GovukValidators.required('Select if fuel allocation by block hour was used during the reporting year'),
          );
          fuelUseMonitoringDetailsFormGroup.controls.identicalToProcedure.updateValueAndValidity();
          fuelUseMonitoringDetailsFormGroup.controls.blockHourUsed.updateValueAndValidity();
        } else {
          fuelUseMonitoringDetailsFormGroup.controls.identicalToProcedure.clearValidators();
          fuelUseMonitoringDetailsFormGroup.controls.identicalToProcedure.setValue(null);
          fuelUseMonitoringDetailsFormGroup.controls.blockHourUsed.clearValidators();
          fuelUseMonitoringDetailsFormGroup.controls.blockHourUsed.setValue(null);
        }
      });

    return fuelUseMonitoringDetailsFormGroup;
  }

  private addFuelUseMonitoringDetailsCtrl(aircraftTypeDetails?: AviationAerCorsiaAircraftTypeDetails[]) {
    const fuelUseMonitoringDetailsFormGroup = this.getFuelUseMonitoringDetailsFormGroup();
    fuelUseMonitoringDetailsFormGroup.controls?.blockHourUsed.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe((blockHourUsed) => {
        if (blockHourUsed) {
          this.addAircraftTypeDetailsFormArrayCtrl(aircraftTypeDetails);
        } else {
          this.removeAircraftTypeDetailsCtrl();
        }
        this.form.controls.fuelUseMonitoringDetails.updateValueAndValidity();
      });
    if (!this.form.contains('fuelUseMonitoringDetails')) {
      this.form.addControl('fuelUseMonitoringDetails', fuelUseMonitoringDetailsFormGroup);
    }
  }

  public addAircraftTypeDetailsGroup(
    aircraftDetail?: AviationAerCorsiaAircraftTypeDetails,
  ): FormGroup<AviationAerCorsiaAircraftTypeDetailsFormModel> {
    return this.fb.group(
      {
        designator: this.fb.control<string | null>(aircraftDetail.designator ?? null),
        subtype: this.fb.control<string | null>(aircraftDetail.subtype ?? null),
        fuelBurnRatio: this.fb.control<string | null>(aircraftDetail.fuelBurnRatio ?? null),
      },
      { updateOn: 'change' },
    ) as FormGroup<AviationAerCorsiaAircraftTypeDetailsFormModel>;
  }

  private addAircraftTypeDetailsFormArrayCtrl(aircraftTypeDetails?: AviationAerCorsiaAircraftTypeDetails[]) {
    const aircraftTypeDetailsFormGroups = aircraftTypeDetails
      ? aircraftTypeDetails?.map((aircraftDetail) => this.addAircraftTypeDetailsGroup(aircraftDetail))
      : [];

    const aircraftTypeDetailsFormArray = this.fb.array(aircraftTypeDetailsFormGroups, {
      updateOn: 'change',
      validators: [
        GovukValidators.required('Upload a CSV file'),
        csvFieldRequiredValidator('designator', 'Enter an aircraft designator'),
        csvFieldRequiredValidator('fuelBurnRatio', 'Enter a fuel burn ratio'),
        csvFieldMaxLengthValidator('designator', 255, 'Enter up to 255 characters'),
        csvFieldMaxLengthValidator('subtype', 255, 'Enter up to 255 characters'),
        csvFieldPositiveMaxDecimalsValidator('fuelBurnRatio', 3, 'Enter a positive number, up to 3 decimals'),
      ],
    });

    if (!this.form.controls?.fuelUseMonitoringDetails.contains('aircraftTypeDetails')) {
      this.form.controls.fuelUseMonitoringDetails.addControl('aircraftTypeDetails', aircraftTypeDetailsFormArray);
    } else {
      this.form.controls.fuelUseMonitoringDetails.controls.aircraftTypeDetails.patchValue(aircraftTypeDetails);
    }
    this.form.controls.fuelUseMonitoringDetails.controls.aircraftTypeDetails.updateValueAndValidity();
  }

  private removeAircraftTypeDetailsCtrl() {
    if (this.form.controls?.fuelUseMonitoringDetails.contains('aircraftTypeDetails')) {
      this.form.controls.fuelUseMonitoringDetails.removeControl('aircraftTypeDetails');
    }
  }

  private removeFuelUseMonitoringDetailsCtrl() {
    if (this.form.contains('fuelUseMonitoringDetails')) {
      this.form.removeControl('fuelUseMonitoringDetails');
    }
  }

  private buildForm() {
    this._form = this.fb.group<AviationAerCorsiaMonitoringApproachFormModel>(
      {
        certUsed: this.fb.control<boolean | null>(null, {
          validators: GovukValidators.required('Select a monitoring approach'),
        }),
      },
      {
        updateOn: 'change',
        validators: [atLeastOneMonitoringDetailsValidator()],
      },
    );

    this.form.controls.certUsed.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((certUsed) => {
      this.enableOptionalFormGroups(certUsed);
    });
  }
}
