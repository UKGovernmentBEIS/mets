import { FormControl, FormGroup } from '@angular/forms';

import { FuelConsumptionMeasuringMethod } from '@aviation/shared/components/emp/emission-sources/aircraft-type/fuel-consumption-measuring-methods';
import { FuelTypes } from '@aviation/shared/components/emp/emission-sources/aircraft-type/fuel-types';

import { AircraftTypeInfo } from 'pmrv-api';

export type AircraftTypeDetailsFormModel = FormGroup<{
  aircraftTypeInfo: FormControl<AircraftTypeInfo>;
  subtype: FormControl<string>;
  numberOfAircrafts: FormControl<number>;
  fuelTypes: FormControl<FuelTypes[]>;
  isCurrentlyUsed: FormControl<boolean>;
  fuelConsumptionMeasuringMethod?: FormControl<FuelConsumptionMeasuringMethod>;
}>;
