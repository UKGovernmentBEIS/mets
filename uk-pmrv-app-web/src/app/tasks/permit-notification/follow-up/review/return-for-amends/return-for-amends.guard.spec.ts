import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { addDays, subDays } from 'date-fns';
import { KeycloakService } from 'keycloak-angular';

import { PermitNotificationFollowUpApplicationReviewRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { FollowUpReturnForAmendsGuard } from './return-for-amends.guard';

describe('NotifyOperatorGuard', () => {
  let guard: FollowUpReturnForAmendsGuard;
  let router: Router;
  let store: CommonTasksStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('determination', null)];
  activatedRouteSnapshot.params = { taskId: 1 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [KeycloakService],
      imports: [HttpClientModule, RouterTestingModule],
    });
    store = TestBed.inject(CommonTasksStore);
    router = TestBed.inject(Router);
    guard = TestBed.inject(FollowUpReturnForAmendsGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should not activate if decision is not taken', async () => {
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: {
        requestTask: {
          id: 1,
          type: 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW',
          payload: {
            payloadType: 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW_PAYLOAD',
            reviewDecision: {
              type: 'ACCEPTED',
              details: { notes: 'some notes' },
            },
          } as PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
        },
        allowedRequestTaskActions: ['PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_REVIEW_DECISION'],
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(
        `/tasks/${activatedRouteSnapshot.params.taskId}/permit-notification/follow-up/review/invalid-data`,
      ),
    );
  });

  it('should not activate if prerequisites are met', async () => {
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: {
        requestTask: {
          id: 1,
          type: 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW',
          payload: {
            payloadType: 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW_PAYLOAD',
            reviewDecision: {
              type: 'AMENDS_NEEDED',
              details: {
                requiredChanges: [{ reason: 'changes' }],
                notes: 'notes',
              },
            },
          } as any,
        },
        allowedRequestTaskActions: ['PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_REVIEW_DECISION'],
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should not activate if decision is operator for amend and new due date is in past', async () => {
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: {
        requestTask: {
          payload: {
            payloadType: 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW_PAYLOAD',
            reviewDecision: {
              type: 'AMENDS_NEEDED',
              details: {
                notes: 'some notes',
                requiredChanges: [{ reason: 'reason', files: ['85ff773f-3cd4-4df4-adfa-09444b7fd732'] }],
                dueDate: subDays(new Date(), 1),
              } as any,
            },
          } as PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
          id: 1,
          type: 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW',
        },
        allowedRequestTaskActions: ['PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_REVIEW_DECISION'],
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(
        `/tasks/${activatedRouteSnapshot.params.taskId}/permit-notification/follow-up/review/invalid-data`,
      ),
    );
  });

  it('should activate if decision is operator for amend and new due date is in future', async () => {
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: {
        requestTask: {
          payload: {
            payloadType: 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW_PAYLOAD',
            reviewDecision: {
              type: 'AMENDS_NEEDED',
              details: {
                notes: 'some notes',
                requiredChanges: [{ reason: 'reason', files: ['85ff773f-3cd4-4df4-adfa-09444b7fd732'] }],
                dueDate: addDays(new Date(), 1),
              } as any,
            },
          } as PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
          id: 1,
          type: 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW',
        },
        allowedRequestTaskActions: ['PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_REVIEW_DECISION'],
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(true);
  });
});
