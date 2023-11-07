import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { updateMockDoalApplicationSubmitRequestTaskItem } from '@tasks/doal/test/mock';
import { initialState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { KeycloakService } from 'keycloak-angular';

import { AlcInformationGuard } from './alc-information.guard';

describe('AlcInformationGuard', () => {
  let guard: AlcInformationGuard;
  let router: Router;
  let store: CommonTasksStore;

  let activatedRouteSnapshot: ActivatedRouteSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [KeycloakService],
    });
    guard = TestBed.inject(AlcInformationGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);

    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  beforeEach(() => {
    activatedRouteSnapshot = new ActivatedRouteSnapshot();
    activatedRouteSnapshot.url = [new UrlSegment('summary', null)];
    activatedRouteSnapshot.params = { taskId: 1 };
    activatedRouteSnapshot.data = { sectionKey: 'activityLevelChangeInformation' };
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should return true if in changing state', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: true } } } as any);

    store.setState({
      ...initialState,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: {
        ...updateMockDoalApplicationSubmitRequestTaskItem({}, { activityLevelChangeInformation: false }),
      },
    });

    expect(guard.canActivate(activatedRouteSnapshot)).toEqual(true);
  });

  it('should return true if section not completed', async () => {
    store.setState({
      ...initialState,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem({}, { activityLevelChangeInformation: false }),
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
