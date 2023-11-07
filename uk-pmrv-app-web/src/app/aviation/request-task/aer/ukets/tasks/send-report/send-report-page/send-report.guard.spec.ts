import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { RequestTaskState, RequestTaskStore } from '@aviation/request-task/store';
import { KeycloakService } from 'keycloak-angular';

import { mockState } from '../testing/mock-state';
import { SendReportGuard } from './send-report.guard';

describe('SendReportGuard', () => {
  let store: RequestTaskStore;
  let guard: SendReportGuard;
  let router: Router;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 1 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientModule],
      providers: [KeycloakService],
    });
    guard = TestBed.inject(SendReportGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(RequestTaskStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate', async () => {
    store.setState({
      ...mockState,
    } as RequestTaskState);

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should navigate to regulator', async () => {
    store.setState({
      ...mockState,
      requestTaskItem: {
        ...mockState.requestTaskItem,
        requestTask: {
          ...mockState.requestTaskItem.requestTask,
          payload: {
            ...mockState.requestTaskItem.requestTask.payload,
            verificationPerformed: true,
            aer: {
              ...mockState.requestTaskItem.requestTask.payload['aer'],
              monitoringApproach: {
                monitoringApproachType: 'EUROCONTROL_SMALL_EMITTERS',
                totalEmissionsType: 'FULL_SCOPE_FLIGHTS',
                fullScopeTotalEmissions: '1',
              },
            },
          },
        },
      },
    } as RequestTaskState);

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(`aviation/tasks/${activatedRouteSnapshot.params.taskId}/aer/send-report/regulator`),
    );
  });

  it('should navigate to verification', async () => {
    store.setState({
      ...mockState,
      requestTaskItem: {
        ...mockState.requestTaskItem,
        requestTask: {
          ...mockState.requestTaskItem.requestTask,
          payload: {
            ...mockState.requestTaskItem.requestTask.payload,
            aer: {
              ...mockState.requestTaskItem.requestTask.payload['aer'],
              monitoringApproach: {
                monitoringApproachType: 'EUROCONTROL_SMALL_EMITTERS',
                totalEmissionsType: 'FULL_SCOPE_FLIGHTS',
                fullScopeTotalEmissions: '1',
              },
            },
          },
        },
      },
    } as RequestTaskState);

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(`aviation/tasks/${activatedRouteSnapshot.params.taskId}/aer/send-report/verification`),
    );
  });
});
