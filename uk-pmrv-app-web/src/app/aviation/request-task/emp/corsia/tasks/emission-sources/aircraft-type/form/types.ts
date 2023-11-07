import { FormControl, FormGroup } from '@angular/forms';

import { FuelConsumptionMeasuringMethod } from '@aviation/shared/components/emp/emission-sources/aircraft-type/fuel-consumption-measuring-methods';
import { FuelTypesCorsia } from '@aviation/shared/components/emp/emission-sources/aircraft-type/fuel-types';

import { AircraftTypeInfo } from 'pmrv-api';

export type AircraftTypeDetailsFormModelCorsia = FormGroup<{
  aircraftTypeInfo: FormControl<AircraftTypeInfo>;
  subtype: FormControl<string>;
  numberOfAircrafts: FormControl<number>;
  fuelTypes: FormControl<FuelTypesCorsia[]>;
  fuelConsumptionMeasuringMethod?: FormControl<FuelConsumptionMeasuringMethod>;
}>;
