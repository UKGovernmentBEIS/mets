import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'fuelMeteringConditionType' })
export class FuelMeteringConditionTypePipe implements PipeTransform {
  transform(value): string {
    switch (value) {
      case 'CELSIUS_0':
        return '0º celsius (standard conditions)';
      case 'CELSIUS_15':
        return '15º celsius (metering condition)';
      default:
        return '';
    }
  }
}
