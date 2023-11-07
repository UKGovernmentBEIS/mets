import { Pipe, PipeTransform } from '@angular/core';

import { AirImprovement, MeasurementOfCO2EmissionPointCategory, MeasurementOfN2OEmissionPointCategory } from 'pmrv-api';

@Pipe({ name: 'categoryTypeName' })
export class CategoryTypeNamePipe implements PipeTransform {
  transform(
    value:
      | MeasurementOfN2OEmissionPointCategory['categoryType']
      | MeasurementOfCO2EmissionPointCategory['categoryType']
      | AirImprovement['categoryType'],
  ): string {
    switch (value) {
      case 'DE_MINIMIS':
        return 'De-minimis';
      case 'MAJOR':
        return 'Major';
      case 'MARGINAL':
        return 'Marginal';
      case 'MINOR':
        return 'Minor';
      default:
        return '';
    }
  }
}
