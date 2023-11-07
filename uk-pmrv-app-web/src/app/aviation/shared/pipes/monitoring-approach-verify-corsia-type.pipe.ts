import { Pipe, PipeTransform } from '@angular/core';

import { AviationAerCorsiaOpinionStatement } from 'pmrv-api';

@Pipe({
  name: 'monitoringApproachVerifyCorsiaType',
  pure: true,
  standalone: true,
})
export class MonitoringApproachVerifyCorsiaTypePipe implements PipeTransform {
  transform(value: AviationAerCorsiaOpinionStatement['monitoringApproachType']): string | null {
    switch (value) {
      case 'CERT_MONITORING':
        return 'CERT only';
      case 'FUEL_USE_MONITORING':
        return 'Fuel use monitoring only';
      case 'CERT_AND_FUEL_USE_MONITORING':
        return 'CERT and fuel use monitoring';
      default:
        return null;
    }
  }
}
