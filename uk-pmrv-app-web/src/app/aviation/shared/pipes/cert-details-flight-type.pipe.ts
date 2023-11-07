import { Pipe, PipeTransform } from '@angular/core';

import { AviationAerCorsiaCertDetails } from 'pmrv-api';

@Pipe({
  name: 'certDetailsFlightType',
  pure: true,
  standalone: true,
})
export class CertDetailsFlightTypePipe implements PipeTransform {
  transform(value: AviationAerCorsiaCertDetails['flightType']): string | null {
    switch (value) {
      case 'ALL_INTERNATIONAL_FLIGHTS':
        return 'All international flights';
      case 'INTERNATIONAL_FLIGHTS_WITH_NO_OFFSETTING_OBLIGATIONS':
        return 'Only for international flights with no offsetting obligations';
      case 'INTERNATIONAL_FLIGHTS_WITH_OFFSETTING_OBLIGATIONS':
        return 'Only for international flights with offsetting obligations';
      default:
        return null;
    }
  }
}
