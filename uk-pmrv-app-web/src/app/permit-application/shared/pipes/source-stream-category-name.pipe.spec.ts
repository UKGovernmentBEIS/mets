import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, of } from 'rxjs';

import { CategoryTypeNamePipe } from '@shared/pipes/category-type-name.pipe';
import { SourceStreamDescriptionPipe } from '@shared/pipes/source-streams-description.pipe';
import { mockClass } from '@testing';

import { CalculationOfCO2MonitoringApproach } from 'pmrv-api';

import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState, mockStateBuild } from '../../testing/mock-state';
import { FindSourceStreamPipe } from './find-source-stream.pipe';
import { SourceStreamCategoryNamePipe } from './source-stream-category-name.pipe';

describe('SourceStreamCategoryNamePipe', () => {
  let pipe: SourceStreamCategoryNamePipe;
  let store: PermitApplicationStore<PermitApplicationState>;

  const findSourceStreamPipe = mockClass(FindSourceStreamPipe);
  const categoryTypeNamePipe = mockClass(CategoryTypeNamePipe);
  const sourceStreamDescriptionPipe = mockClass(SourceStreamDescriptionPipe);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SourceStreamCategoryNamePipe],
      imports: [HttpClientModule, RouterTestingModule],
      providers: [
        { provide: FindSourceStreamPipe, useValue: findSourceStreamPipe },
        { provide: CategoryTypeNamePipe, useValue: categoryTypeNamePipe },
        { provide: SourceStreamDescriptionPipe, useValue: sourceStreamDescriptionPipe },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    });
    store = TestBed.inject(PermitApplicationStore);
  });

  beforeEach(
    () =>
      (pipe = new SourceStreamCategoryNamePipe(
        store,
        findSourceStreamPipe,
        categoryTypeNamePipe,
        sourceStreamDescriptionPipe,
      )),
  );

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform tier source stream and category', async () => {
    store.setState(mockState);

    findSourceStreamPipe.transform.mockReturnValueOnce(of(mockState.permit.sourceStreams[0]));
    sourceStreamDescriptionPipe.transform.mockReturnValueOnce('White Spirit & SBP');
    categoryTypeNamePipe.transform.mockReturnValueOnce('Major');
    await expect(
      firstValueFrom(
        pipe.transform(
          (mockState.permit.monitoringApproaches.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach)
            .sourceStreamCategoryAppliedTiers[0],
        ),
      ),
    ).resolves.toEqual('13123124 White Spirit & SBP: Major');

    findSourceStreamPipe.transform.mockReturnValueOnce(of(null));
    categoryTypeNamePipe.transform.mockReturnValueOnce('Major');
    await expect(
      firstValueFrom(
        pipe.transform(
          (mockState.permit.monitoringApproaches.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach)
            .sourceStreamCategoryAppliedTiers[0],
        ),
      ),
    ).resolves.toEqual('UNDEFINED: Major');

    findSourceStreamPipe.transform.mockReturnValueOnce(of(null));
    await expect(firstValueFrom(pipe.transform(undefined))).resolves.toEqual('Add a source stream category');

    findSourceStreamPipe.transform.mockReturnValueOnce(of(mockState.permit.sourceStreams[0]));
    sourceStreamDescriptionPipe.transform.mockReturnValueOnce('White Spirit & SBP');
    categoryTypeNamePipe.transform.mockReturnValueOnce('Major');
    await expect(firstValueFrom(pipe.transform(0, 'CALCULATION_CO2'))).resolves.toEqual(
      '13123124 White Spirit & SBP: Major',
    );

    findSourceStreamPipe.transform.mockReturnValueOnce(of(null));
    categoryTypeNamePipe.transform.mockReturnValueOnce('Major');
    await expect(firstValueFrom(pipe.transform(0, 'CALCULATION_CO2'))).resolves.toEqual('UNDEFINED: Major');
  });

  it('should transform tier source stream and category when no data', async () => {
    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          CALCULATION_CO2: {
            type: 'CALCULATION_CO2',
          },
        },
      }),
    );

    findSourceStreamPipe.transform.mockReturnValueOnce(of(null));
    await expect(firstValueFrom(pipe.transform(0, 'CALCULATION_CO2'))).resolves.toEqual('Add a source stream category');

    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          CALCULATION_CO2: {
            type: 'CALCULATION_CO2',
            sourceStreamCategoryAppliedTiers: [],
          },
        },
      }),
    );

    findSourceStreamPipe.transform.mockReturnValueOnce(of(null));
    await expect(firstValueFrom(pipe.transform(0, 'CALCULATION_CO2'))).resolves.toEqual('Add a source stream category');

    store.setState(
      mockStateBuild({
        monitoringApproaches: {},
      }),
    );

    findSourceStreamPipe.transform.mockReturnValueOnce(of(null));
    await expect(firstValueFrom(pipe.transform(0, 'CALCULATION_CO2'))).resolves.toEqual('Add a source stream category');
  });
});
