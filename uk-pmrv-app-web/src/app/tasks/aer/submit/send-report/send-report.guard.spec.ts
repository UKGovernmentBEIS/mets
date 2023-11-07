import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { getAvailableSections } from '@tasks/aer/core/aer-task-statuses';
import { SendReportGuard } from '@tasks/aer/submit/send-report/send-report.guard';
import { mockAerApplyPayload, mockState } from '@tasks/aer/submit/testing/mock-aer-apply-action';
import { mockStateBuild } from '@tasks/aer/submit/testing/mock-state';
import { CommonTasksState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { KeycloakService } from 'keycloak-angular';

describe('SendReportGuard', () => {
  let store: CommonTasksStore;
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
    store = TestBed.inject(CommonTasksStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate', async () => {
    store.setState({
      ...mockState,
      requestTaskItem: {
        ...mockState.requestTaskItem,
        requestTask: {
          ...mockState.requestTaskItem.requestTask,
          payload: {
            ...mockAerApplyPayload,
            permitType: 'HSE',
            aerSectionsCompleted: {
              ...getAvailableSections(mockAerApplyPayload).reduce((res, key) => ({ ...res, [key]: [true] }), {}),
            },
          },
        },
      },
    } as CommonTasksState);

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);

    store.setState(mockStateBuild({}, { abbreviations: [false] }));

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
            ...mockAerApplyPayload,
            permitType: 'GHGE',
            verificationPerformed: true,
            aerSectionsCompleted: {
              ...getAvailableSections(mockAerApplyPayload).reduce((res, key) => ({ ...res, [key]: [true] }), {}),
            },
          },
        },
      },
    } as CommonTasksState);

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(`tasks/${activatedRouteSnapshot.params.taskId}/aer/submit/send-report/regulator`),
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
            ...mockAerApplyPayload,
            aerSectionsCompleted: {
              ...getAvailableSections(mockAerApplyPayload).reduce((res, key) => ({ ...res, [key]: [true] }), {}),
            },
          },
        },
      },
    } as CommonTasksState);

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(`tasks/${activatedRouteSnapshot.params.taskId}/aer/submit/send-report/verification`),
    );
  });
});
