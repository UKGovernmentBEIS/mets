import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { inspectionMockStateBuild } from '../test/mock';
import { SendPeerReviewGuard } from './send-peer-review.guard';

describe('SendPeerReviewGuard', () => {
  let guard: SendPeerReviewGuard;
  let router: Router;
  let store: CommonTasksStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('peer-review', {})];
  activatedRouteSnapshot.params = { taskId: 1, type: 'audit', id: '0' };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SendPeerReviewGuard],
      imports: [RouterTestingModule, HttpClientTestingModule],
    });

    store = TestBed.inject(CommonTasksStore);
    router = TestBed.inject(Router);
    guard = TestBed.inject(SendPeerReviewGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate', async () => {
    store.setState(inspectionMockStateBuild({ payloadType: 'INSTALLATION_AUDIT_REQUEST_PEER_REVIEW' }));

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to submit', async () => {
    store.setState(
      inspectionMockStateBuild({
        payloadType: 'INSTALLATION_AUDIT_REQUEST_PEER_REVIEW',
        installationInspectionSectionsCompleted: { followUpAction: false },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/${activatedRouteSnapshot.params.taskId}/inspection/audit/submit`));
  });
});
