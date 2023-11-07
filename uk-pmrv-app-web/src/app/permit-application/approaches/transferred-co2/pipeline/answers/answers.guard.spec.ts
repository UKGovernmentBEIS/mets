import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlSegment } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { TasksService, TransferredCO2AndN2OMonitoringApproach } from 'pmrv-api';

import { mockClass } from '../../../../../../testing';
import { PermitIssuanceStore } from '../../../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockState, mockStateBuild } from '../../../../testing/mock-state';
import { AnswersGuard } from './answers.guard';

describe('AnswersGuard', () => {
  let router: Router;
  let guard: AnswersGuard;
  let store: PermitApplicationStore<PermitApplicationState>;

  const tasksService = mockClass(TasksService);
  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('answers', null)];
  const routerStateSnapshot = {
    url: '/permit-issuance/276/transferred-co2/pipeline/answers',
  } as RouterStateSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        AnswersGuard,
        { provide: TasksService, useValue: tasksService },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    });
    guard = TestBed.inject(AnswersGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitApplicationStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to summary if status is completed', async () => {
    store.setState(mockStateBuild(undefined, { TRANSFERRED_CO2_N2O_Pipeline: [true] }));

    await expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot))).resolves.toEqual(
      router.parseUrl('/permit-issuance/276/transferred-co2/pipeline/summary'),
    );
  });

  it('should allow if all step are completed', async () => {
    const mockTransfer = mockState.permit.monitoringApproaches
      .TRANSFERRED_CO2_N2O as TransferredCO2AndN2OMonitoringApproach;

    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          TRANSFERRED_CO2_N2O: {
            ...mockTransfer,
            transportCO2AndN2OPipelineSystems: {
              exist: false,
            },
          } as TransferredCO2AndN2OMonitoringApproach,
        },
        permitSectionsCompleted: {
          TRANSFERRED_CO2_N2O_Pipeline: [false],
        },
      }),
    );

    await expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot))).resolves.toEqual(true);
  });

  it('should redirect to start if at least one step is not completed', async () => {
    const mockTransfer = mockState.permit.monitoringApproaches
      .TRANSFERRED_CO2_N2O as TransferredCO2AndN2OMonitoringApproach;

    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          TRANSFERRED_CO2_N2O: {
            type: 'TRANSFERRED_CO2_N2O',
          },
        },
      }),
    );

    await expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot))).resolves.toEqual(
      router.parseUrl('/permit-issuance/276/transferred-co2/pipeline'),
    );

    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          TRANSFERRED_CO2_N2O: {
            ...mockTransfer,
            transportCO2AndN2OPipelineSystems: {
              exist: false,
            },
          } as TransferredCO2AndN2OMonitoringApproach,
        },
      }),
    );

    await expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot))).resolves.toEqual(true);

    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          TRANSFERRED_CO2_N2O: {
            ...mockTransfer,
            transportCO2AndN2OPipelineSystems: {
              exist: true,
              procedureForLeakageEvents: {},
            },
          } as TransferredCO2AndN2OMonitoringApproach,
        },
      }),
    );

    await expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot))).resolves.toEqual(
      router.parseUrl('/permit-issuance/276/transferred-co2/pipeline'),
    );
  });
});
