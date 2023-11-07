import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { SummaryGuard } from '@tasks/doal/authority-response/summary.guard';
import { mockDoalAuthorityResponseRequestTaskTaskItem } from '@tasks/doal/test/mock';
import { initialState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { KeycloakService } from 'keycloak-angular';

import { RequestTaskPayload } from 'pmrv-api';

describe('SummaryGuard', () => {
  let guard: SummaryGuard;
  let router: Router;
  let store: CommonTasksStore;

  let activatedRouteSnapshot: ActivatedRouteSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [KeycloakService],
    });
    guard = TestBed.inject(SummaryGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);

    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  describe('For date submitted to authority', () => {
    beforeEach(() => {
      activatedRouteSnapshot = new ActivatedRouteSnapshot();
      activatedRouteSnapshot.url = [new UrlSegment('summary', null)];
      activatedRouteSnapshot.params = { taskId: 1 };
      activatedRouteSnapshot.data = { sectionKey: 'dateSubmittedToAuthority' };
    });

    const routerStateSnapshot = {
      url: '/tasks/1/doal/authority-response/date-submitted/summary',
    } as RouterStateSnapshot;

    it('should be created', () => {
      expect(guard).toBeTruthy();
    });

    it('should return true if section completed', async () => {
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: mockDoalAuthorityResponseRequestTaskTaskItem,
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(true);
    });

    it('should return true if wizard completed', async () => {
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          ...mockDoalAuthorityResponseRequestTaskTaskItem,
          requestTask: {
            ...mockDoalAuthorityResponseRequestTaskTaskItem.requestTask,
            payload: {
              ...mockDoalAuthorityResponseRequestTaskTaskItem.requestTask.payload,
              doalSectionsCompleted: {
                dateSubmittedToAuthority: false,
              },
            } as RequestTaskPayload,
          },
        },
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(true);
    });

    it('should redirect to base url if section not completed', async () => {
      store.setState({
        ...initialState,
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          ...mockDoalAuthorityResponseRequestTaskTaskItem,
          requestTask: {
            ...mockDoalAuthorityResponseRequestTaskTaskItem.requestTask,
            payload: {
              ...mockDoalAuthorityResponseRequestTaskTaskItem.requestTask.payload,
              doalSectionsCompleted: {},
            } as RequestTaskPayload,
          },
        },
      });

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(router.parseUrl(`/tasks/1/doal/authority-response/date-submitted`));
    });
  });
});
