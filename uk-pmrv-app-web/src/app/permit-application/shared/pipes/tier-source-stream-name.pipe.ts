import { Pipe, PipeTransform } from '@angular/core';

import { CategoryTypeNamePipe } from '@shared/pipes/category-type-name.pipe';
import { SourceStreamDescriptionPipe } from '@shared/pipes/source-streams-description.pipe';

import {
  CalculationSourceStreamCategory,
  FallbackSourceStreamCategory,
  MeasurementOfCO2EmissionPointCategory,
  MeasurementOfN2OEmissionPointCategory,
  PFCSourceStreamCategory,
  SourceStream,
} from 'pmrv-api';

@Pipe({
  name: 'tierSourceStreamName',
})
export class TierSourceStreamNamePipe implements PipeTransform {
  constructor(
    private sourceStreamDescriptionPipe: SourceStreamDescriptionPipe,
    private categoryTypeNamePipe: CategoryTypeNamePipe,
  ) {}

  transform(
    sourceStream: SourceStream,
    tierCategory:
      | PFCSourceStreamCategory
      | CalculationSourceStreamCategory
      | MeasurementOfN2OEmissionPointCategory
      | MeasurementOfCO2EmissionPointCategory
      | FallbackSourceStreamCategory,
  ): string {
    return `${sourceStream?.reference} ${this.sourceStreamDescriptionPipe.transform(
      sourceStream,
    )}: ${this.categoryTypeNamePipe.transform(tierCategory?.categoryType)}`;
  }
}
