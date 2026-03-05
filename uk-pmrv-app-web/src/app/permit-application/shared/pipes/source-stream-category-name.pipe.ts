import { Pipe, PipeTransform } from '@angular/core';

import { map, Observable, switchMap } from 'rxjs';

import { CategoryTypeNamePipe } from '@shared/pipes/category-type-name.pipe';
import { SourceStreamDescriptionPipe } from '@shared/pipes/source-streams-description.pipe';

import {
  CalculationSourceStreamCategoryAppliedTier,
  FallbackSourceStreamCategoryAppliedTier,
  PFCSourceStreamCategoryAppliedTier,
} from 'pmrv-api';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { FindSourceStreamPipe } from './find-source-stream.pipe';

@Pipe({ name: 'sourceStreamCategoryName' })
export class SourceStreamCategoryNamePipe implements PipeTransform {
  constructor(
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private findSourceStreamPipe: FindSourceStreamPipe,
    private categoryTypeNamePipe: CategoryTypeNamePipe,
    private sourceStreamDescriptionPipe: SourceStreamDescriptionPipe,
  ) {}

  transform(
    target:
      | number
      | FallbackSourceStreamCategoryAppliedTier
      | PFCSourceStreamCategoryAppliedTier
      | CalculationSourceStreamCategoryAppliedTier,
    path?: 'FALLBACK' | 'CALCULATION_PFC' | 'CALCULATION_CO2',
  ): Observable<string> {
    return typeof target === 'number'
      ? this.store
          .findTask<
            | FallbackSourceStreamCategoryAppliedTier[]
            | PFCSourceStreamCategoryAppliedTier[]
            | CalculationSourceStreamCategoryAppliedTier[]
          >(`monitoringApproaches.${path}.sourceStreamCategoryAppliedTiers`)
          .pipe(switchMap((tiers) => this.transformName(tiers?.[target])))
      : this.transformName(target);
  }

  private transformName(
    tier:
      | FallbackSourceStreamCategoryAppliedTier
      | PFCSourceStreamCategoryAppliedTier
      | CalculationSourceStreamCategoryAppliedTier,
  ): Observable<string> {
    return this.findSourceStreamPipe.transform(tier?.sourceStreamCategory?.sourceStream).pipe(
      map((sourceStream) => {
        if (sourceStream) {
          return `${sourceStream.reference} ${this.sourceStreamDescriptionPipe.transform(
            sourceStream,
          )}: ${this.categoryTypeNamePipe.transform(tier?.sourceStreamCategory?.categoryType)}`;
        } else {
          return (tier?.sourceStreamCategory?.categoryType ?? false)
            ? 'UNDEFINED: ' + this.categoryTypeNamePipe.transform(tier.sourceStreamCategory.categoryType)
            : 'Add a source stream category';
        }
      }),
    );
  }
}
