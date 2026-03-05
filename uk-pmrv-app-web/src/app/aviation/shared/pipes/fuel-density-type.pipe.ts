import { Pipe, PipeTransform } from '@angular/core';

import { AviationAerCorsiaFuelUseMonitoringDetails } from 'pmrv-api';

@Pipe({
  name: 'fuelDensityType',
  pure: true,
  standalone: true,
})
export class FuelDensityTypePipe implements PipeTransform {
  transform(value: AviationAerCorsiaFuelUseMonitoringDetails['fuelDensityType']): string | null {
    switch (value) {
      case 'ACTUAL_DENSITY':
        return 'Actual density';
      case 'ACTUAL_STANDARD_DENSITY':
        return 'Actual and standard density';
      case 'STANDARD_DENSITY':
        return 'Standard density';
      case 'NOT_APPLICABLE':
        return 'Not applicable - we only use the block-off/block-on method';
      default:
        return null;
    }
  }
}
