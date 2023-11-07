import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { initialState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { KeycloakService } from 'keycloak-angular';

import { updateMockDoalApplicationSubmitRequestTaskItem } from '../../../../test/mock';
import { PreliminaryAllocationGuard } from './preliminary-allocation.guard';

describe('PreliminaryAllocationGuard', () => {
  let guard: PreliminaryAllocationGuard;
  let router: Router;
  let store: CommonTasksStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 1, index: 0 };
  activatedRouteSnapshot.data = { sectionKey: 'activityLevelChangeInformation' };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [KeycloakService],
    });
    guard = TestBed.inject(PreliminaryAllocationGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);

    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate if task is changing', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: true } } } as any);
    expect(guard.canActivate(activatedRouteSnapshot)).toEqual(true);
  });

  it('should redirect to base step if index number is out of array list bound', async () => {
    store.setState({
      ...initialState,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
        {
          activityLevelChangeInformation: {
            preliminaryAllocations: [],
          },
        },
        { activityLevelChangeInformation: false },
      ),
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/1/doal/submit/alc-information/preliminary-allocations`));
  });

  it('should true if index number is valid', async () => {
    store.setState({
      ...initialState,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
        {
          activityLevelChangeInformation: {
            preliminaryAllocations: [
              {
                subInstallationName: 'ALUMINIUM',
                year: 2025,
                allowances: 10,
              },
            ],
          },
        },
        { activityLevelChangeInformation: false },
      ),
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to summary if section completed', async () => {
    store.setState({
      ...initialState,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
        {},
        {
          activityLevelChangeInformation: true,
        },
      ),
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/1/doal/submit/alc-information/summary`));
  });
});
