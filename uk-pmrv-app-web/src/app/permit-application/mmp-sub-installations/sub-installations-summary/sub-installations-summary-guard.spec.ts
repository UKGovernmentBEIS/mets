import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { mockState, mockStateBuild } from '@permit-application/testing/mock-state';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, ActivatedRouteStub } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockDigitizedPlanImportedExportedMeasurableHeat, mockDigitizedPlanWasteGasBalance } from '../testing/mock';
import { MMPSubInstallationsSummaryGuard } from './sub-installations-summary-guard';
describe('MMPSubInstallationsSummaryGuard', () => {
  let guard: MMPSubInstallationsSummaryGuard;
  let router: Router;
  let store: PermitApplicationStore<PermitApplicationState>;
  const route = new ActivatedRouteStub({ subInstallationNo: '0' }, {}, { permitTask: 'monitoringMethodologyPlans' });

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        MMPSubInstallationsSummaryGuard,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: {} },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    });

    guard = TestBed.inject(MMPSubInstallationsSummaryGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitIssuanceStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should not activate if data model is complete', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            digitizedPlan: mockDigitizedPlanWasteGasBalance,
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          monitoringMethodologyPlans: [true],
          MMP_SUB_INSTALLATION_Product_Benchmark: [false],
        },
      ),
    );

    await expect(
      firstValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({ taskId: 1 }, null, {
            permitTask: 'monitoringMethodologyPlans',
          }),
        ),
      ),
    ).resolves.toEqual(true);
  });

  it('should activate if  data model is wrong', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: {
            exist: true,
            plans: ['e227ea8a-778b-4208-9545-e108ea66c113'],
            digitizedPlan: mockDigitizedPlanImportedExportedMeasurableHeat,
          },
        },
        {
          ...mockState.permitSectionsCompleted,
          monitoringMethodologyPlans: [true],
          MMP_SUB_INSTALLATION_Product_Benchmark: [false],
        },
      ),
    );

    await expect(firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: 1 })))).resolves.toEqual(
      router.parseUrl('/permit-issuance/1/mmp-sub-installations/0'),
    );
  });
});
