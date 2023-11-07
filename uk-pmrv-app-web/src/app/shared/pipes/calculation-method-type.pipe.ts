import { Pipe, PipeTransform } from '@angular/core';

import { CalculationParameterCalculationMethod } from 'pmrv-api';

@Pipe({ name: 'calculationMethodType' })
export class CalculationMethodTypePipe implements PipeTransform {
  transform(value: CalculationParameterCalculationMethod['type']): string {
    switch (value) {
      case 'MANUAL':
        return 'Calculate the values manually';
      case 'NATIONAL_INVENTORY_DATA':
        return 'Use national inventory data';
      case 'REGIONAL_DATA':
        return 'Use regional data for natural gas';
      default:
        return '';
    }
  }
}
