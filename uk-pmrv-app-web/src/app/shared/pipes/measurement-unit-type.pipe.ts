import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'measurementUnitType' })
export class MeasurementUnitTypePipe implements PipeTransform {
  transform(value): string {
    switch (value) {
      case 'TONNES':
        return 'tonnes';
      case 'NM3':
        return 'normal cubic meter (Nm3)';
      case 'GJ_PER_NM3':
        return 'GJ/Nm3';
      case 'GJ_PER_TONNE':
        return 'GJ/Tonne';
      case 'TONNES_OF_CO2_PER_NM3':
        return 'tCO2/Nm3';
      case 'TONNES_OF_CO2_PER_TJ':
        return 'tCO2/TJ';
      case 'TONNES_OF_CO2_PER_TONNE':
        return 'tCO2/Tonne';
      case 'TONNES_OF_CARBON_PER_TONNE':
        return 'tC/Tonne';
      case 'TONNES_OF_CARBON_PER_NM3':
        return 'tC/Nm3';
      default:
        return '';
    }
  }
}
