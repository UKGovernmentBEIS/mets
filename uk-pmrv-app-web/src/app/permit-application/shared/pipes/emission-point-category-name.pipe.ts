import { Pipe, PipeTransform } from '@angular/core';

import { map, Observable, switchMap } from 'rxjs';

import { CategoryTypeNamePipe } from '@shared/pipes/category-type-name.pipe';

import {
  MeasurementOfCO2EmissionPointCategoryAppliedTier,
  MeasurementOfN2OEmissionPointCategoryAppliedTier,
} from 'pmrv-api';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { FindEmissionPointPipe } from './find-emission-point.pipe';

@Pipe({ name: 'emissionPointCategoryName' })
export class EmissionPointCategoryNamePipe implements PipeTransform {
  constructor(
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private findEmissionPointPipe: FindEmissionPointPipe,
    private categoryTypeNamePipe: CategoryTypeNamePipe,
  ) {}

  transform(
    target:
      | number
      | MeasurementOfN2OEmissionPointCategoryAppliedTier
      | MeasurementOfCO2EmissionPointCategoryAppliedTier,
    path?: 'MEASUREMENT_N2O' | 'MEASUREMENT_CO2',
  ): Observable<string> {
    return typeof target === 'number'
      ? this.store
          .findTask<
            MeasurementOfN2OEmissionPointCategoryAppliedTier[] | MeasurementOfCO2EmissionPointCategoryAppliedTier[]
          >(`monitoringApproaches.${path}.emissionPointCategoryAppliedTiers`)
          .pipe(switchMap((tiers) => this.transformName(tiers?.[target])))
      : this.transformName(target);
  }

  private transformName(
    tier: MeasurementOfN2OEmissionPointCategoryAppliedTier | MeasurementOfCO2EmissionPointCategoryAppliedTier,
  ): Observable<string> {
    return this.findEmissionPointPipe.transform(tier?.emissionPointCategory?.emissionPoint).pipe(
      map((emissionPoint) => {
        if (emissionPoint) {
          return `${emissionPoint.reference} ${emissionPoint.description}: ${this.categoryTypeNamePipe.transform(
            tier?.emissionPointCategory?.categoryType,
          )}`;
        } else {
          return tier?.emissionPointCategory?.categoryType ?? false
            ? 'UNDEFINED: ' + this.categoryTypeNamePipe.transform(tier.emissionPointCategory.categoryType)
            : 'Add an emission point category';
        }
      }),
    );
  }
}
