import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { NonComplianceDailyPenaltyNoticeRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../../store/common-tasks.store';
import { mockCompletedNonComplianceDailyPenaltyNoticeRequestTaskItem } from '../../test/mock';
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
          type: 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE',
          payload: {
            dailyPenaltyCompleted: false,
          } as NonComplianceDailyPenaltyNoticeRequestTaskPayload,
        },
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(`/tasks/${activatedRouteSnapshot.params.taskId}/non-compliance/daily-penalty-notice`),
    );
  });

  it('should activate if section is completed', async () => {
    store.setState({
      ...store.getState(),
      requestTaskItem: {
        ...mockCompletedNonComplianceDailyPenaltyNoticeRequestTaskItem,
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(true);
  });
});
