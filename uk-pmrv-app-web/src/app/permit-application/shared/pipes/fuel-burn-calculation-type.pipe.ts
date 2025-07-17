import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'fuelBurnCalculationType',
})
export class FuelBurnCalculationTypePipe implements PipeTransform {
  transform(
    value:
      | 'MEASURABLE_HEAT_IMPORTED'
      | 'MEASURABLE_HEAT_FROM_PULP'
      | 'MEASURABLE_HEAT_FROM_NITRIC_ACID'
      | 'MEASURABLE_HEAT_EXPORTED'
      | 'NO_MEASURABLE_HEAT',
  ): string {
    switch (value) {
      case 'MEASURABLE_HEAT_IMPORTED':
        return 'Measurable heat imported';
      case 'MEASURABLE_HEAT_FROM_PULP':
        return 'Measurable heat from pulp';
      case 'MEASURABLE_HEAT_FROM_NITRIC_ACID':
        return 'Measurable heat from nitric acid';
      case 'MEASURABLE_HEAT_EXPORTED':
        return 'Measurable heat exported';
      case 'NO_MEASURABLE_HEAT':
        return 'No, measurable heat is not imported to or exported from this sub-installation';
      default:
        return '';
    }
  }
}
