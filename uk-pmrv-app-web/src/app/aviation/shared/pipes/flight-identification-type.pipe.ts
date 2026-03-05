import { Pipe, PipeTransform } from '@angular/core';

import { FlightIdentification } from 'pmrv-api';

const FLIGHT_IDENTIFICATION_TYPE_SELECTION = {
  INTERNATIONAL_CIVIL_AVIATION_ORGANISATION:
    'International Civil Aviation Organisation (ICAO) designators used in item 7 of the flight plan',
  AIRCRAFT_REGISTRATION_MARKINGS:
    'Nationality or common mark, registration mark of an aeroplane or any other code used in item 7 of the flight plan',
};

@Pipe({
  name: 'flightIdentificationType',
  pure: true,
  standalone: true,
})
export class FlightIdentificationTypePipe implements PipeTransform {
  transform(value: FlightIdentification['flightIdentificationType']): string | null {
    if (value == null) {
      return null;
    }

    return FLIGHT_IDENTIFICATION_TYPE_SELECTION[value] ?? null;
  }
}
