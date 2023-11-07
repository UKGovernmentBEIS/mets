import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { mockReview, mockState } from '@tasks/aer/review/testing/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { KeycloakService } from 'keycloak-angular';

import { AerApplicationReviewRequestTaskPayload } from 'pmrv-api';

import { CompleteReviewGuard } from './complete-review.guard';

describe('CompleteReviewGuard', () => {
  let guard: CompleteReviewGuard;
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
    guard = TestBed.inject(CompleteReviewGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should not activate if decisions are not completed', async () => {
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: {
        requestTask: {
          id: 1,
          type: 'AER_APPLICATION_REVIEW',
          payload: {
            payloadType: 'AER_APPLICATION_REVIEW_PAYLOAD',
            aer: mockReview.aer,
            reviewSectionsCompleted: {},
          } as AerApplicationReviewRequestTaskPayload,
        },
        allowedRequestTaskActions: ['AER_COMPLETE_REVIEW'],
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/${activatedRouteSnapshot.params.taskId}/aer/review`));
  });

  it('should activate if all are completed', async () => {
    store.setState(mockState);

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(true);
  });
});
