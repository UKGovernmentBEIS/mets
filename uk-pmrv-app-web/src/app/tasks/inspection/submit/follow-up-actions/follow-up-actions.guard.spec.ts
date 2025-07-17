import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { inspectionMockStateBuild, inspectionSubmitMockState } from '@tasks/inspection/test/mock';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { FollowUpSubmitSummaryGuard } from '../follow-up-summary/follow-up-summary.guard';

describe('FollowUpSummaryGuard', () => {
  let guard: FollowUpSubmitSummaryGuard;
  let router: Router;
  let store: CommonTasksStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('follow-up-summary', {})];
  activatedRouteSnapshot.params = { taskId: 1, type: 'audit', id: '1' };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [FollowUpSubmitSummaryGuard],
    });

    guard = TestBed.inject(FollowUpSubmitSummaryGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should resolve installationInspections are completed', async () => {
    store.setState(inspectionSubmitMockState);
    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to follow-up-actions', async () => {
    store.setState(inspectionMockStateBuild({ installationInspection: null }));

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(
        `tasks/${activatedRouteSnapshot.params.taskId}/inspection/${activatedRouteSnapshot.params.type}/submit/follow-up-actions`,
      ),
    );
  });
});
