import { Pipe, PipeTransform } from '@angular/core';

import { AircraftTypeDetails, AircraftTypeDetailsCorsia } from 'pmrv-api';

import { FUEL_TYPES, FUEL_TYPES_CORSIA } from '../components/emp/emission-sources/aircraft-type/fuel-types';

@Pipe({
  name: 'aircraftTypeFuelTypes',
  standalone: true,
})
export class AircraftTypeFuelTypesPipe implements PipeTransform {
  transform(atd: AircraftTypeDetails | AircraftTypeDetailsCorsia, isCorsia = false): string[] {
    if (!atd.fuelTypes.length) return [];
    return atd.fuelTypes.map((ft) =>
      isCorsia
        ? FUEL_TYPES_CORSIA.find((t) => t.value === ft).summaryDescription
        : FUEL_TYPES.find((t) => t.value === ft).summaryDescription,
    );
  }
}
