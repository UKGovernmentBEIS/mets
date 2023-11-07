import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';

import { combineLatest, Subject, takeUntil } from 'rxjs';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';
import {
  EmissionSmallEmittersSupportFacilityFormValues,
  EmissionsMonitoringApproach,
} from '@aviation/shared/components/aer/monitoring-approach-summary-template/monitoring-approach.interfaces';

import { GovukValidators } from 'govuk-components';

import { AviationAerEmissionsMonitoringApproach, AviationAerSupportFacilityMonitoringApproach } from 'pmrv-api';

export interface AviationAerSmallEmittersMonitoringApproachFormModel {
  numOfFlightsJanApr: FormControl<number | null>;
  numOfFlightsMayAug: FormControl<number | null>;
  numOfFlightsSepDec: FormControl<number | null>;
  totalEmissions: FormControl<string | null>;
}
export interface AviationAerSupportFacilityMonitoringApproachFormModel {
  totalEmissionsType: FormControl<AviationAerSupportFacilityMonitoringApproach['totalEmissionsType'] | null>;
  fullScopeTotalEmissions?: FormControl<string | null>;
  aviationActivityTotalEmissions?: FormControl<string | null>;
}
export interface MonitoringApproachFormModel {
  monitoringApproachType: FormControl<EmissionsMonitoringApproach['monitoringApproachType'] | null>;
  aviationAerSupportFacilityMonitoringApproach?: FormGroup<AviationAerSupportFacilityMonitoringApproachFormModel>;
  aviationAerSmallEmittersMonitoringApproach?: FormGroup<AviationAerSmallEmittersMonitoringApproachFormModel>;
}

interface EmissionSmallEmittersSupportFacilityFormModel {
  monitoringApproachType: EmissionsMonitoringApproach['monitoringApproachType'] | null;
  aviationAerSupportFacilityMonitoringApproach?: {
    totalEmissionsType: AviationAerSupportFacilityMonitoringApproach['totalEmissionsType'] | null;
    fullScopeTotalEmissions: string | null;
    aviationActivityTotalEmissions: string | null;
  };
  aviationAerSmallEmittersMonitoringApproach?: {
    numOfFlightsJanApr: number | null;
    numOfFlightsMayAug: number | null;
    numOfFlightsSepDec: number | null;
    totalEmissions: string | null;
  };
}

@Injectable()
export class AerMonitoringApproachFormProvider
  implements TaskFormProvider<EmissionsMonitoringApproach, MonitoringApproachFormModel>
{
  private fb = inject(FormBuilder);
  private _form: FormGroup<MonitoringApproachFormModel>;

  private destroy$ = new Subject<void>();

  get form(): FormGroup<MonitoringApproachFormModel> {
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

  setFormValue(monitoringApproach: EmissionSmallEmittersSupportFacilityFormValues): void {
    if (monitoringApproach) {
      let value: EmissionSmallEmittersSupportFacilityFormModel = {
        monitoringApproachType: monitoringApproach.monitoringApproachType ?? null,
      };

      if (monitoringApproach.monitoringApproachType === 'EUROCONTROL_SUPPORT_FACILITY') {
        value = {
          ...value,
          aviationAerSupportFacilityMonitoringApproach: {
            totalEmissionsType: monitoringApproach?.totalEmissionsType ?? null,
            fullScopeTotalEmissions: monitoringApproach?.fullScopeTotalEmissions ?? null,
            aviationActivityTotalEmissions: monitoringApproach?.aviationActivityTotalEmissions ?? null,
          },
        };
        this.removeAviationAerSmallEmittersMonitoringApproach();
        this.addAviationAerSupportFacilityMonitoringApproach();
      } else if (monitoringApproach.monitoringApproachType === 'EUROCONTROL_SMALL_EMITTERS') {
        value = {
          ...value,
          aviationAerSmallEmittersMonitoringApproach: {
            numOfFlightsJanApr: monitoringApproach?.numOfFlightsJanApr ?? null,
            numOfFlightsMayAug: monitoringApproach?.numOfFlightsMayAug ?? null,
            numOfFlightsSepDec: monitoringApproach?.numOfFlightsSepDec ?? null,
            totalEmissions: monitoringApproach?.totalEmissions ?? null,
          },
        };
        this.removeAviationAerSupportFacilityMonitoringApproach();
        this.addAviationAerSmallEmittersMonitoringApproach();
      } else {
        this.removeAviationAerSmallEmittersMonitoringApproach();
        this.removeAviationAerSupportFacilityMonitoringApproach();
      }

      this.form.setValue(value as any);
    }
  }

  get monitoringApproachTypeCtrl(): FormControl {
    return this.form.get('monitoringApproachType') as FormControl;
  }

  get aviationAerSupportFacilityMonitoringApproachCtrl(): FormGroup<AviationAerSupportFacilityMonitoringApproachFormModel> {
    return this.form.get('aviationAerSupportFacilityMonitoringApproach') as FormGroup;
  }

  get aviationAerSmallEmittersMonitoringApproachCtrl(): FormGroup<AviationAerSmallEmittersMonitoringApproachFormModel> {
    return this.form.get('aviationAerSmallEmittersMonitoringApproach') as FormGroup;
  }

  addAviationAerSupportFacilityMonitoringApproach() {
    if (!this.form.contains('aviationAerSupportFacilityMonitoringApproach')) {
      this.form.addControl(
        'aviationAerSupportFacilityMonitoringApproach',
        this.fb.group<AviationAerSupportFacilityMonitoringApproachFormModel>(
          {
            totalEmissionsType: new FormControl<
              AviationAerSupportFacilityMonitoringApproach['totalEmissionsType'] | null
            >(null, {
              updateOn: 'change',
              validators: GovukValidators.required('Select either full-scope flights or aviation activity'),
            }),
            fullScopeTotalEmissions: new FormControl<string | null>(null, {
              updateOn: 'change',
            }),
            aviationActivityTotalEmissions: new FormControl<string | null>(null, {
              updateOn: 'change',
            }),
          },
          { validators: Validators.required },
        ),
      );

      const totalEmissionsType = this.form
        .get('aviationAerSupportFacilityMonitoringApproach')
        .get('totalEmissionsType');
      const fullScopeTotalEmissions = this.form
        .get('aviationAerSupportFacilityMonitoringApproach')
        .get('fullScopeTotalEmissions');
      const aviationActivityTotalEmissions = this.form
        .get('aviationAerSupportFacilityMonitoringApproach')
        .get('aviationActivityTotalEmissions');

      totalEmissionsType.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((value) => {
        if (value === 'FULL_SCOPE_FLIGHTS') {
          fullScopeTotalEmissions.setValidators([
            GovukValidators.required('Enter the total amount of emissions from full-scope flights'),
            GovukValidators.positiveNumber('Enter a number greater than 0, up to 24999.999'),
            GovukValidators.maxDecimalsValidator(3),
            GovukValidators.max(24999.999, 'Enter a number greater than 0, up to 24999.999'),
          ]);
          aviationActivityTotalEmissions.clearValidators();
          aviationActivityTotalEmissions.setValue(null);
        } else if (value === 'AVIATION_ACTIVITY') {
          aviationActivityTotalEmissions.setValidators([
            GovukValidators.required('Enter the total amount of emissions from aviation activity'),
            GovukValidators.positiveNumber('Enter a number greater than 0, up to 2999.999'),
            GovukValidators.maxDecimalsValidator(3),
            GovukValidators.max(2999.999, 'Enter a number greater than 0, up to 2999.999'),
          ]);
          fullScopeTotalEmissions.clearValidators();
          fullScopeTotalEmissions.setValue(null);
        }
        fullScopeTotalEmissions.updateValueAndValidity();
        aviationActivityTotalEmissions.updateValueAndValidity();
      });
    }
  }

  removeAviationAerSupportFacilityMonitoringApproach() {
    if (this.form.contains('aviationAerSupportFacilityMonitoringApproach')) {
      this.form.removeControl('aviationAerSupportFacilityMonitoringApproach');
    }
  }

  integerValidator(control) {
    if (control.value % 1 !== 0) {
      return { integer: 'The value must be an integer' };
    }
    return null;
  }

  addAviationAerSmallEmittersMonitoringApproach() {
    if (!this.form.contains('aviationAerSmallEmittersMonitoringApproach')) {
      this.form.addControl(
        'aviationAerSmallEmittersMonitoringApproach',
        this.fb.group<AviationAerSmallEmittersMonitoringApproachFormModel>(
          {
            numOfFlightsJanApr: new FormControl<number | null>(null, {
              updateOn: 'change',
            }),
            numOfFlightsMayAug: new FormControl<number | null>(null, {
              updateOn: 'change',
            }),
            numOfFlightsSepDec: new FormControl<number | null>(null, {
              updateOn: 'change',
            }),
            totalEmissions: new FormControl<string | null>(null, {
              updateOn: 'change',
            }),
          },
          { validators: Validators.required },
        ),
      );

      const numOfFlightsJanApr = this.form.get('aviationAerSmallEmittersMonitoringApproach').get('numOfFlightsJanApr');
      const numOfFlightsMayAug = this.form.get('aviationAerSmallEmittersMonitoringApproach').get('numOfFlightsMayAug');
      const numOfFlightsSepDec = this.form.get('aviationAerSmallEmittersMonitoringApproach').get('numOfFlightsSepDec');
      const totalEmissions = this.form.get('aviationAerSmallEmittersMonitoringApproach').get('totalEmissions');

      combineLatest([
        numOfFlightsJanApr.valueChanges,
        numOfFlightsMayAug.valueChanges,
        numOfFlightsSepDec.valueChanges,
        totalEmissions.valueChanges,
      ])
        .pipe(takeUntil(this.destroy$))
        .subscribe(([valueJanApr, valueMayAug, valueSepDec, valueTotalEmissions]) => {
          if (
            (valueJanApr >= 243 || valueMayAug >= 243 || valueSepDec >= 243) &&
            Number(valueTotalEmissions) >= 25000
          ) {
            totalEmissions.addValidators([
              GovukValidators.max(
                24999.999,
                'You must operate fewer than 243 full-scope flights for each of the 4-month periods, or emit less than 25,000 tCO2 from full-scope flights',
              ),
            ]);
            this.addMaxValidatorForNumberOfFlights(numOfFlightsJanApr);
            this.addMaxValidatorForNumberOfFlights(numOfFlightsMayAug);
            this.addMaxValidatorForNumberOfFlights(numOfFlightsSepDec);
          } else {
            this.initValidatorsForTotalEmissions(totalEmissions);
            this.initValidatorsForNumFlightsJanApr(numOfFlightsJanApr);
            this.initValidatorsForNumFlightsMayAug(numOfFlightsMayAug);
            this.initValidatorsForNumFlightsSepDec(numOfFlightsSepDec);
          }
          totalEmissions.updateValueAndValidity({ emitEvent: false });
          numOfFlightsJanApr.updateValueAndValidity({ emitEvent: false });
          numOfFlightsMayAug.updateValueAndValidity({ emitEvent: false });
          numOfFlightsSepDec.updateValueAndValidity({ emitEvent: false });
        });
    }
  }

  private initValidatorsForTotalEmissions(totalEmissions) {
    totalEmissions.setValidators([
      GovukValidators.required('Enter the total amount of emissions from full-scope flights'),
      GovukValidators.positiveNumber('Enter a number greater than 0'),
      GovukValidators.maxDecimalsValidator(3),
    ]);
  }

  private initValidatorsForNumFlightsJanApr(numOfFlightsJanApr) {
    numOfFlightsJanApr.setValidators([
      GovukValidators.required('Enter the number of flights from 1 January to 30 April'),
      GovukValidators.min(0, 'Must be integer greater than or equal to 0'),
      this.integerValidator,
    ]);
  }

  private initValidatorsForNumFlightsMayAug(numOfFlightsMayAug) {
    numOfFlightsMayAug.setValidators([
      GovukValidators.required('Enter the number of flights from 1 May to 31 August'),
      GovukValidators.min(0, 'Must be integer greater than or equal to 0'),
      this.integerValidator,
    ]);
  }

  private initValidatorsForNumFlightsSepDec(numOfFlightsSepDec) {
    numOfFlightsSepDec.setValidators([
      GovukValidators.required('Enter the number of flights from 1 September to 31 December'),
      GovukValidators.min(0, 'Must be integer greater than or equal to 0'),
      this.integerValidator,
    ]);
  }

  private addMaxValidatorForNumberOfFlights(numOfFlights) {
    numOfFlights.addValidators([
      GovukValidators.max(
        242,
        'You must operate fewer than 243 full-scope flights for each of the 4-month periods, or emit less than 25,000 tCO2 from full-scope flights',
      ),
    ]);
  }

  removeAviationAerSmallEmittersMonitoringApproach() {
    if (this.form.contains('aviationAerSmallEmittersMonitoringApproach')) {
      this.form.removeControl('aviationAerSmallEmittersMonitoringApproach');
    }
  }

  getFormValue(): EmissionSmallEmittersSupportFacilityFormValues {
    const value = this.form.value;
    const ret: any = {
      monitoringApproachType: value.monitoringApproachType,
    };

    if (value?.aviationAerSupportFacilityMonitoringApproach?.totalEmissionsType) {
      ret.totalEmissionsType = value.aviationAerSupportFacilityMonitoringApproach.totalEmissionsType;
      if (value.aviationAerSupportFacilityMonitoringApproach.fullScopeTotalEmissions) {
        ret.fullScopeTotalEmissions = value.aviationAerSupportFacilityMonitoringApproach.fullScopeTotalEmissions;
      } else {
        ret.aviationActivityTotalEmissions =
          value.aviationAerSupportFacilityMonitoringApproach.aviationActivityTotalEmissions;
      }
    }

    if (value?.aviationAerSmallEmittersMonitoringApproach?.totalEmissions) {
      ret.numOfFlightsJanApr = value.aviationAerSmallEmittersMonitoringApproach.numOfFlightsJanApr;
      ret.numOfFlightsMayAug = value.aviationAerSmallEmittersMonitoringApproach.numOfFlightsMayAug;
      ret.numOfFlightsSepDec = value.aviationAerSmallEmittersMonitoringApproach.numOfFlightsSepDec;
      ret.totalEmissions = value.aviationAerSmallEmittersMonitoringApproach.totalEmissions;
    }

    return ret;
  }

  private buildForm() {
    this._form = this.fb.group<MonitoringApproachFormModel>(
      {
        monitoringApproachType: new FormControl<
          AviationAerEmissionsMonitoringApproach['monitoringApproachType'] | null
        >(null, {
          updateOn: 'change',
          validators: GovukValidators.required('You must select one approach'),
        }),
      },
      { updateOn: 'change' },
    );
  }
}
