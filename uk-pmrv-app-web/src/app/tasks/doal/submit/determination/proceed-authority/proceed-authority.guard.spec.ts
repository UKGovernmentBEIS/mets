import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { initialState } from '../../../../store/common-tasks.state';
import { CommonTasksStore } from '../../../../store/common-tasks.store';
import {
  mockDoalApplicationSubmitRequestTaskItem,
  updateMockDoalApplicationSubmitRequestTaskItem,
} from '../../../test/mock';
import { ProceedAuthorityGuard } from './proceed-authority.guard';

describe('ProceedAuthorityGuard', () => {
  let guard: ProceedAuthorityGuard;
  let router: Router;
  let store: CommonTasksStore;

  let activatedRouteSnapshot: ActivatedRouteSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [KeycloakService],
    });
    guard = TestBed.inject(ProceedAuthorityGuard);
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
      requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
        {},
        {
          ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doalSectionsCompleted,
          determination: true,
        },
      ),
    });

    expect(guard.canActivate(activatedRouteSnapshot)).toEqual(true);
  });

  it('should redirect to main task if not proceed authority determination', async () => {
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
    ).resolves.toEqual(router.parseUrl(`/tasks/1/doal/submit/determination`));
  });

  it('should redirect to summary page if wizard is populated', async () => {
    store.setState({
      ...initialState,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
        {},
        {
          ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doalSectionsCompleted,
          determination: false,
        },
      ),
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/1/doal/submit/determination/proceed-authority/summary`));
  });

  it('should return true if proceed authority but not fully populated for summary yet', async () => {
    store.setState({
      ...initialState,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem(
        {
          determination: {
            type: 'PROCEED_TO_AUTHORITY',
          } as any,
        },
        {
          ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doalSectionsCompleted,
          determination: false,
        },
      ),
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });
});
