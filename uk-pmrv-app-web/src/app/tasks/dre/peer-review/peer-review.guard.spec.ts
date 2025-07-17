import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { format, subDays } from 'date-fns';
import { KeycloakService } from 'keycloak-angular';

import { DreApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../../tasks/store/common-tasks.store';
import { mockCompletedDreApplicationSubmitRequestTaskItem } from '../test/mock';
import { PeerReviewGuard } from './peer-review.guard';

describe('PeerReviewGuard', () => {
  let guard: PeerReviewGuard;
  let router: Router;
  let store: CommonTasksStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 1 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [KeycloakService],
      imports: [HttpClientModule, RouterTestingModule],
    });
    store = TestBed.inject(CommonTasksStore);
    router = TestBed.inject(Router);
    guard = TestBed.inject(PeerReviewGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should not activate if section is not completed', async () => {
    store.setState({
      ...store.getState(),
      requestTaskItem: {
        requestTask: {
          id: 1,
          type: 'DRE_APPLICATION_SUBMIT',
          payload: {
            sectionCompleted: false,
          } as DreApplicationSubmitRequestTaskPayload,
        },
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/${activatedRouteSnapshot.params.taskId}/dre/submit`));
  });

  it('should activate if section is completed', async () => {
    store.setState({
      ...store.getState(),
      requestTaskItem: {
        ...mockCompletedDreApplicationSubmitRequestTaskItem,
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should navigate to invalid page if data is invalid', async () => {
    store.setState({
      ...store.getState(),
      requestTaskItem: {
        requestTask: {
          id: 1,
          type: 'DRE_APPLICATION_SUBMIT',
          payload: {
            dre: {
              fee: {
                feeDetails: {
                  dueDate: format(subDays(new Date(), 1), 'yyyy-MM-dd'),
                },
              },
            },
          } as DreApplicationSubmitRequestTaskPayload,
        },
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/${activatedRouteSnapshot.params.taskId}/dre/submit/invalid-data`));
  });
});
