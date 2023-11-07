import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { mockTaskState } from '@permit-revocation/testing/mock-state';

import { PeerReviewGuard } from './peer-review.guard';

describe('PeerReviewGuard', () => {
  let guard: PeerReviewGuard;
  let router: Router;
  let store: PermitRevocationStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('determination', null)];
  activatedRouteSnapshot.params = { taskId: mockTaskState.requestTaskId };
  activatedRouteSnapshot.data = { requestTaskActionType: 'PERMIT_REVOCATION_REQUEST_PEER_REVIEW' };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, RouterTestingModule],
    });
    store = TestBed.inject(PermitRevocationStore);
    router = TestBed.inject(Router);
    guard = TestBed.inject(PeerReviewGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate if prerequisites are met for permit revocation flow', async () => {
    store.setState({
      ...mockTaskState,
      sectionsCompleted: { REVOCATION_APPLY: true },
      allowedRequestTaskActions: ['PERMIT_REVOCATION_REQUEST_PEER_REVIEW'],
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to invalid data page if validations are not met for permit revocation flow', async () => {
    store.setState({
      ...mockTaskState,
      permitRevocation: { ...mockTaskState.permitRevocation, surrenderDate: '2022-10-24' },
      sectionsCompleted: { REVOCATION_APPLY: true },
      allowedRequestTaskActions: ['PERMIT_REVOCATION_REQUEST_PEER_REVIEW'],
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/permit-revocation/${activatedRouteSnapshot.params.taskId}/invalid-data`));
  });

  it('should not activate if determination is not completed', async () => {
    store.setState({
      ...mockTaskState,
      sectionsCompleted: { REVOCATION_APPLY: false },
      allowedRequestTaskActions: ['PERMIT_REVOCATION_REQUEST_PEER_REVIEW'],
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/permit-revocation/${activatedRouteSnapshot.params.taskId}`));
  });
});
