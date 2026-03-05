import { Pipe, PipeTransform } from '@angular/core';

import { CategoryTypeNamePipe } from '@shared/pipes/category-type-name.pipe';

import { EmissionPoint, MeasurementOfCO2EmissionPointCategory, MeasurementOfN2OEmissionPointCategory } from 'pmrv-api';

@Pipe({
  name: 'tierEmissionPointName',
})
export class TierEmissionPointNamePipe implements PipeTransform {
  constructor(private categoryTypeNamePipe: CategoryTypeNamePipe) {}

  transform(
    emissionPoint: EmissionPoint,
    tierCategory: MeasurementOfN2OEmissionPointCategory | MeasurementOfCO2EmissionPointCategory,
  ): string {
    return `${emissionPoint?.reference} ${emissionPoint?.description}: ${this.categoryTypeNamePipe.transform(
      tierCategory?.categoryType,
    )}`;
  }
}
