import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'pfcCalculationMethod',
  standalone: false,
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
