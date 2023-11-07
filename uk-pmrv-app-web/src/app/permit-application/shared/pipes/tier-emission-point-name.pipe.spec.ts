import { TestBed } from '@angular/core/testing';

import { CategoryTypeNamePipe } from '@shared/pipes/category-type-name.pipe';
import { mockClass } from '@testing';

import { MeasurementOfN2OMonitoringApproach } from 'pmrv-api';

import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { TierEmissionPointNamePipe } from './tier-emission-point-name.pipe';

describe('TierEmissionPointNamePipe', () => {
  let pipe: TierEmissionPointNamePipe;

  const categoryTypeNamePipe = mockClass(CategoryTypeNamePipe);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TierEmissionPointNamePipe],
      providers: [{ provide: CategoryTypeNamePipe, useValue: categoryTypeNamePipe }],
    });

    pipe = new TierEmissionPointNamePipe(categoryTypeNamePipe);
  });

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform tier source stream and category', () => {
    categoryTypeNamePipe.transform.mockReturnValueOnce('category_name');

    const mockedSousreStream = mockPermitApplyPayload.permit.emissionPoints[0];
    const mockedTierCategory = (
      mockPermitApplyPayload.permit.monitoringApproaches.MEASUREMENT_N2O as MeasurementOfN2OMonitoringApproach
    ).emissionPointCategoryAppliedTiers[0].emissionPointCategory;

    expect(pipe.transform(mockedSousreStream, mockedTierCategory)).toEqual(
      `${mockedSousreStream.reference} ${mockedSousreStream.description}: category_name`,
    );

    expect(categoryTypeNamePipe.transform).toHaveBeenCalledTimes(1);
    expect(categoryTypeNamePipe.transform).toHaveBeenCalledWith(mockedTierCategory.categoryType);
  });
});
