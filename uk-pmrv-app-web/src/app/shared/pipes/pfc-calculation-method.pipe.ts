import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'pfcCalculationMethod',
})
export class PfcCalculationMethodPipe implements PipeTransform {
  transform(value): string {
    switch (value) {
      case 'SLOPE':
        return 'Slope';
      case 'OVERVOLTAGE':
        return 'Overvoltage';
      default:
        return '';
    }
  }
}
