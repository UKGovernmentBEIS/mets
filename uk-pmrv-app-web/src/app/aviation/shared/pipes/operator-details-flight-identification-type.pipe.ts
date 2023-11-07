import { Pipe, PipeTransform } from '@angular/core';

import { FlightIdentification } from 'pmrv-api';

const FLIGHT_IDENTIFICATION_TYPE: Record<FlightIdentification['flightIdentificationType'], string> = {
  INTERNATIONAL_CIVIL_AVIATION_ORGANISATION: 'International Civil Aviation Organisation (ICAO) designators',
  AIRCRAFT_REGISTRATION_MARKINGS: 'Aircraft registration markings',
};

const FLIGHT_IDENTIFICATION_TYPE_CORSIA: Record<FlightIdentification['flightIdentificationType'], string> = {
  INTERNATIONAL_CIVIL_AVIATION_ORGANISATION:
    'International Civil Aviation Organisation (ICAO) designators used in item 7 of the flight plan',
  AIRCRAFT_REGISTRATION_MARKINGS:
    'Nationality or common mark, registration mark of an aeroplane or any other code used in item 7 of the flight plan',
};
@Pipe({
  name: 'operatorDetailsFlightIdentificationType',
  pure: true,
  standalone: true,
})
export class OperatorDetailsFlightIdentificationTypePipe implements PipeTransform {
  transform(value: FlightIdentification['flightIdentificationType'], isCorsia = false): string | null {
    const idTypeRecord = isCorsia ? FLIGHT_IDENTIFICATION_TYPE_CORSIA : FLIGHT_IDENTIFICATION_TYPE;
    return idTypeRecord[value] || null;
  }
}
