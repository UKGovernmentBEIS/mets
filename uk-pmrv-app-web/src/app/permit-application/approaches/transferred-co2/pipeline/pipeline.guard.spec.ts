import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { TasksService, TransferredCO2AndN2OMonitoringApproach } from 'pmrv-api';

import { mockClass } from '../../../../../testing';
import { PermitIssuanceStore } from '../../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockState, mockStateBuild } from '../../../testing/mock-state';
import { PipelineGuard } from './pipeline.guard';

describe('PipelineGuard', () => {
  let router: Router;
  let guard: PipelineGuard;
  let store: PermitApplicationStore<PermitApplicationState>;

  const tasksService = mockClass(TasksService);
  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('pipeline', null)];
  const routerStateSnapshot = {
    url: '/permit-issuance/276/transferred-co2/pipeline',
  } as RouterStateSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        PipelineGuard,
        { provide: TasksService, useValue: tasksService },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    });
    guard = TestBed.inject(PipelineGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitApplicationStore);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to summary if status is completed', async () => {
    store.setState(mockStateBuild(undefined, { TRANSFERRED_CO2_N2O_Pipeline: [true] }));

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/permit-issuance/276/transferred-co2/pipeline/summary'));
  });

  it('should redirect to answers if status is not completed and form is full filled', async () => {
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

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/permit-issuance/276/transferred-co2/pipeline/answers'));
  });

  it('should redirect to wizard step if form is empty', async () => {
    const mockTransfer = mockState.permit.monitoringApproaches
      .TRANSFERRED_CO2_N2O as TransferredCO2AndN2OMonitoringApproach;

    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          TRANSFERRED_CO2_N2O: {
            ...mockTransfer,
            transportCO2AndN2OPipelineSystems: {},
          } as TransferredCO2AndN2OMonitoringApproach,
        },
        permitSectionsCompleted: {
          TRANSFERRED_CO2_N2O_Pipeline: [false],
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });
});
