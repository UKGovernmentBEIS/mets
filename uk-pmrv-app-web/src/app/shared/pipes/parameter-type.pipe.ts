import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'parameterType' })
export class ParameterTypePipe implements PipeTransform {
  transform(value): string {
    switch (value) {
      case 'ACTIVITY_DATA':
        return 'Activity data';
      case 'EMISSION_FACTOR':
        return 'Emission factor';
      case 'NET_CALORIFIC_VALUE':
        return 'Net calorific value';
      case 'OXIDATION_FACTOR':
        return 'Oxidation factor';
      case 'BIOMASS_FRACTION':
        return 'Biomass';
      case 'CARBON_CONTENT':
        return 'Carbon content';
      case 'CONVERSION_FACTOR':
        return 'Conversion factor';
      default:
        return '';
    }
  }
}
