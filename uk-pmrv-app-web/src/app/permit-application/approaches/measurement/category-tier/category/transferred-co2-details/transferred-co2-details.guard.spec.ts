import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { mockState, mockStateBuild } from '@permit-application/testing/mock-state';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { ActivatedRouteSnapshotStub, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { TransferredCO2DetailsGuard } from './transferred-co2-details.guard';

describe('TransferredCo2Guard', () => {
  let guard: TransferredCO2DetailsGuard;
  let store: PermitApplicationStore<PermitApplicationState>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        TransferredCO2DetailsGuard,
        { provide: TasksService, useValue: mockClass(TasksService) },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    });
    store = TestBed.inject(PermitApplicationStore);
    guard = TestBed.inject(TransferredCO2DetailsGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should return true', async () => {
    store.setState(mockState);

    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          MEASUREMENT_CO2: {
            type: 'MEASUREMENT_CO2',
            emissionPointCategoryAppliedTiers: [
              {
                emissionPointCategory: {
                  transfer: {
                    entryAccountingForTransfer: true,
                  },
                },
              },
            ],
          },
        },
      }),
    });
    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237', index: '0' }))),
    ).resolves.toEqual(true);
  });

  it('should redirect to category page', async () => {
    store.setState(mockState);

    store.setState({
      ...mockStateBuild({
        monitoringApproaches: {
          MEASUREMENT_CO2: {
            type: 'MEASUREMENT_CO2',
            emissionPointCategoryAppliedTiers: [
              {
                emissionPointCategory: {
                  transfer: {
                    entryAccountingForTransfer: false,
                  },
                },
              },
            ],
          },
        },
      }),
    });
    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '237', index: '0' }))),
    ).resolves.toEqual(TestBed.inject(Router).parseUrl('/permit-issuance/237/measurement/category-tier/0/category'));
  });
});
