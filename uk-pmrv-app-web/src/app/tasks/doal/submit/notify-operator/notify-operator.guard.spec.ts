import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { initialState } from '../../../store/common-tasks.state';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import {
  mockDoalApplicationSubmitRequestTaskItem,
  updateMockDoalApplicationSubmitRequestTaskItem,
} from '../../test/mock';
import { NotifyOperatorGuard } from './notify-operator.guard';

describe('NotifyOperatorGuard', () => {
  let guard: NotifyOperatorGuard;
  let router: Router;
  let store: CommonTasksStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 1 };

  const routerStateSnapshot = {
    url: '/tasks/1/doal/submit/notify-operator',
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
      requestTaskItem: mockDoalApplicationSubmitRequestTaskItem,
    });
    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should navigate to parent level if allowedRequestTaskActions has not the right value', async () => {
    store.setState({
      ...initialState,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: {
        ...mockDoalApplicationSubmitRequestTaskItem,
        allowedRequestTaskActions: [],
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/1/doal/submit`));
  });

  it('should navigate to submit component if not all the sections are completed', async () => {
    store.setState({
      ...initialState,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
        {},
        {
          operatorActivityLevelReport: true,
          verificationReportOfTheActivityLevelReport: true,
          additionalDocuments: true,
          activityLevelChangeInformation: true,
          determination: false,
        },
      ),
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/1/doal/submit`));
  });
});
