import { Pipe, PipeTransform } from '@angular/core';

import { AircraftTypeDetails } from 'pmrv-api';

import { FuelConsumptionMeasuringMethods } from '../components/emp/emission-sources/aircraft-type/fuel-consumption-measuring-methods';

@Pipe({
  name: 'aircraftTypeMethodTypes',
  standalone: true,
})
export class AircraftTypeFuelMethodPipe implements PipeTransform {
  transform(atd: AircraftTypeDetails, isFUMM: boolean): string {
    if (!isFUMM) return '';
    if (!atd.fuelConsumptionMeasuringMethod) return 'Missing';
    return FuelConsumptionMeasuringMethods.find((fcm) => fcm.value === atd.fuelConsumptionMeasuringMethod).label;
  }
}
