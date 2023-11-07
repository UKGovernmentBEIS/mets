import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';
import moment from 'moment';

import { DreApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { initialState } from '../../../store/common-tasks.state';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { mockCompletedDreApplicationSubmitRequestTaskItem } from '../../test/mock';
import { NotifyOperatorGuard } from './notify-operator.guard';

describe('NotifyOperatorGuard', () => {
  let guard: NotifyOperatorGuard;
  let router: Router;
  let store: CommonTasksStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 1 };

  const routerStateSnapshot = {
    url: '/tasks/1/dre/submit/notify-operator',
  } as RouterStateSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [KeycloakService],
    });
    guard = TestBed.inject(NotifyOperatorGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);

    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate if section completed and has allowedRequestTaskActions', async () => {
    store.setState({
      ...initialState,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: mockCompletedDreApplicationSubmitRequestTaskItem,
    });
    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should navigate to submit component if allowedRequestTaskActions has not the right value', async () => {
    store.setState({
      ...initialState,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: {
        ...mockCompletedDreApplicationSubmitRequestTaskItem,
        allowedRequestTaskActions: [],
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/1/dre/submit`));
  });

  it('should navigate to submit component if section is not completed', async () => {
    store.setState({
      ...initialState,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: {
        ...mockCompletedDreApplicationSubmitRequestTaskItem,
        requestTask: {
          payload: {
            sectionCompleted: false,
          } as DreApplicationSubmitRequestTaskPayload,
        },
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/1/dre/submit`));
  });

  it('should navigate to invalid page if data is invalid', async () => {
    store.setState({
      ...initialState,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: {
        ...mockCompletedDreApplicationSubmitRequestTaskItem,
        requestTask: {
          payload: {
            sectionCompleted: true,
            dre: {
              fee: {
                feeDetails: {
                  dueDate: moment().add(-1, 'day').format('YYYY-MM-DD'),
                },
              },
            },
          } as DreApplicationSubmitRequestTaskPayload,
        },
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/1/dre/submit/invalid-data`));
  });
});
