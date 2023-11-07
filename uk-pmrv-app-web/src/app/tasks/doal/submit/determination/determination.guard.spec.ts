import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { initialState } from '../../../store/common-tasks.state';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import {
  mockDoalApplicationSubmitRequestTaskItem,
  updateMockDoalApplicationSubmitRequestTaskItem,
} from '../../test/mock';
import { DeterminationGuard } from './determination.guard';

describe('DeterminationGuard', () => {
  let guard: DeterminationGuard;
  let router: Router;
  let store: CommonTasksStore;

  let activatedRouteSnapshot: ActivatedRouteSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [KeycloakService],
    });
    guard = TestBed.inject(DeterminationGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);

    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  beforeEach(() => {
    activatedRouteSnapshot = new ActivatedRouteSnapshot();
    activatedRouteSnapshot.params = { taskId: 1 };
    activatedRouteSnapshot.data = { sectionKey: 'determination' };
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
      requestTaskItem: mockDoalApplicationSubmitRequestTaskItem,
    });

    expect(guard.canActivate(activatedRouteSnapshot)).toEqual(true);
  });

  it('should return true if can be started and determination not exist yet', async () => {
    store.setState({
      ...initialState,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
        {
          determination: undefined,
        },
        {
          ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doalSectionsCompleted,
          determination: null,
        },
      ),
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to task if cannot start yet', async () => {
    store.setState({
      ...initialState,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
        {
          operatorActivityLevelReport: undefined,
          determination: undefined,
        },
        {
          ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doalSectionsCompleted,
          operatorActivityLevelReport: null,
          determination: null,
        },
      ),
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/1/doal/submit`));
  });

  it('should redirect to summary determination if proceed authority determination already exist', async () => {
    store.setState({
      ...initialState,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: mockDoalApplicationSubmitRequestTaskItem,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/1/doal/submit/determination/proceed-authority/summary`));
  });

  it('should redirect to summary determination if close determination already exist and status is true', async () => {
    store.setState({
      ...initialState,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
        {
          ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doal,
          determination: {
            type: 'CLOSED',
            reason: 'reason',
          },
        },
        {
          ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doalSectionsCompleted,
          determination: true,
        },
      ),
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/1/doal/submit/determination/close/summary`));
  });

  it('should redirect to summary determination if close determination is populated without status flag to true yet', async () => {
    store.setState({
      ...initialState,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
        {
          ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doal,
          determination: {
            type: 'CLOSED',
            reason: 'reason',
          },
        },
        {
          ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doalSectionsCompleted,
          determination: false,
        },
      ),
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/1/doal/submit/determination/close/summary`));
  });
});
