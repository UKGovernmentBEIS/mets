import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { initialState } from '../../../store/common-tasks.state';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { mockCompletedNonComplianceCivilPenaltyRequestTaskItem, updateMockedCivilPenalty } from '../../test/mock';
import { UploadCivilPenaltyGuard } from './upload-civil-penalty.guard';

describe('UploadCivilPenaltyGuard', () => {
  let guard: UploadCivilPenaltyGuard;
  let router: Router;
  let store: CommonTasksStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();

  const routerStateSnapshot = {
    url: 'tasks/1/non-compliance/civil-penalty-notice/upload-penalty-notice',
  } as RouterStateSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [KeycloakService],
    });
    guard = TestBed.inject(UploadCivilPenaltyGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);

    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate if task is changing', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: true } } } as any);
    await expect(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot)).toEqual(true);
  });

  it('should redirect to summary if section completed', async () => {
    store.setState({
      ...initialState,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: mockCompletedNonComplianceCivilPenaltyRequestTaskItem,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/1/non-compliance/civil-penalty-notice/summary`));
  });

  it('should redirect to summary if wizard completed', async () => {
    store.setState({
      ...initialState,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: updateMockedCivilPenalty({}, false),
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/1/non-compliance/civil-penalty-notice/summary`));
  });

  it('should activate when neither wizard is complete nor section completed', async () => {
    store.setState({
      ...initialState,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: updateMockedCivilPenalty({ civilPenalty: undefined }, false),
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });
});