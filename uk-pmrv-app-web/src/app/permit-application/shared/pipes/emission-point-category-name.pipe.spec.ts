import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, of } from 'rxjs';

import { CategoryTypeNamePipe } from '@shared/pipes/category-type-name.pipe';
import { mockClass } from '@testing';

import { MeasurementOfN2OMonitoringApproach } from 'pmrv-api';

import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState, mockStateBuild } from '../../testing/mock-state';
import { EmissionPointCategoryNamePipe } from './emission-point-category-name.pipe';
import { FindEmissionPointPipe } from './find-emission-point.pipe';

describe('EmissionPointCategoryNamePipe', () => {
  let pipe: EmissionPointCategoryNamePipe;
  let store: PermitApplicationStore<PermitApplicationState>;

  const findEmissionPointPipe = mockClass(FindEmissionPointPipe);
  const categoryTypeNamePipe = mockClass(CategoryTypeNamePipe);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EmissionPointCategoryNamePipe],
      imports: [HttpClientModule, RouterTestingModule],
      providers: [
        { provide: FindEmissionPointPipe, useValue: findEmissionPointPipe },
        { provide: CategoryTypeNamePipe, useValue: categoryTypeNamePipe },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    });
    store = TestBed.inject(PermitApplicationStore);
  });

  beforeEach(() => (pipe = new EmissionPointCategoryNamePipe(store, findEmissionPointPipe, categoryTypeNamePipe)));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform tier emission point and category', async () => {
    store.setState(mockState);

    findEmissionPointPipe.transform.mockReturnValueOnce(of(mockState.permit.emissionPoints[0]));
    categoryTypeNamePipe.transform.mockReturnValueOnce('Major');
    await expect(
      firstValueFrom(
        pipe.transform(
          (mockState.permit.monitoringApproaches.MEASUREMENT_N2O as MeasurementOfN2OMonitoringApproach)
            .emissionPointCategoryAppliedTiers[0],
        ),
      ),
    ).resolves.toEqual('The big Ref Emission point 1: Major');

    findEmissionPointPipe.transform.mockReturnValueOnce(of(null));
    categoryTypeNamePipe.transform.mockReturnValueOnce('Major');
    await expect(
      firstValueFrom(
        pipe.transform(
          (mockState.permit.monitoringApproaches.MEASUREMENT_N2O as MeasurementOfN2OMonitoringApproach)
            .emissionPointCategoryAppliedTiers[0],
        ),
      ),
    ).resolves.toEqual('UNDEFINED: Major');

    findEmissionPointPipe.transform.mockReturnValueOnce(of(null));
    await expect(firstValueFrom(pipe.transform(undefined))).resolves.toEqual('Add an emission point category');

    findEmissionPointPipe.transform.mockReturnValueOnce(of(mockState.permit.emissionPoints[0]));
    categoryTypeNamePipe.transform.mockReturnValueOnce('Major');
    await expect(firstValueFrom(pipe.transform(0, 'MEASUREMENT_N2O'))).resolves.toEqual(
      'The big Ref Emission point 1: Major',
    );

    findEmissionPointPipe.transform.mockReturnValueOnce(of(null));
    categoryTypeNamePipe.transform.mockReturnValueOnce('Major');
    await expect(firstValueFrom(pipe.transform(0, 'MEASUREMENT_N2O'))).resolves.toEqual('UNDEFINED: Major');
  });

  it('should transform tier emission point and category when no data', async () => {
    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          MEASUREMENT_N2O: {
            type: 'MEASUREMENT_N2O',
          },
        },
      }),
    );

    findEmissionPointPipe.transform.mockReturnValueOnce(of(null));
    await expect(firstValueFrom(pipe.transform(0, 'MEASUREMENT_N2O'))).resolves.toEqual(
      'Add an emission point category',
    );

    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          MEASUREMENT_N2O: {
            type: 'MEASUREMENT_N2O',
            emissionPointCategoryAppliedTiers: [],
          },
        },
      }),
    );

    findEmissionPointPipe.transform.mockReturnValueOnce(of(null));
    await expect(firstValueFrom(pipe.transform(0, 'MEASUREMENT_N2O'))).resolves.toEqual(
      'Add an emission point category',
    );

    store.setState(
      mockStateBuild({
        monitoringApproaches: {},
      }),
    );

    findEmissionPointPipe.transform.mockReturnValueOnce(of(null));
    await expect(firstValueFrom(pipe.transform(0, 'MEASUREMENT_N2O'))).resolves.toEqual(
      'Add an emission point category',
    );
  });
});
