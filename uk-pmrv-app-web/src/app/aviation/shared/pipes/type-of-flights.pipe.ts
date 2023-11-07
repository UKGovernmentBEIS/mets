import { Pipe, PipeTransform } from '@angular/core';

import { SubsidiaryCompanyCorsia } from 'pmrv-api';

@Pipe({
  name: 'typeOfFlights',
  pure: true,
  standalone: true,
})
export class TypeOfFlightsPipe implements PipeTransform {
  transform(value: SubsidiaryCompanyCorsia['flightTypes']): string | null {
    if (value.length === 0) {
      return null;
    }
    const type = value[0];
    switch (type) {
      case 'SCHEDULED':
        return 'Scheduled flights';
      case 'NON_SCHEDULED':
        return 'Non-scheduled flights';
      default:
        return null;
    }
  }
}
