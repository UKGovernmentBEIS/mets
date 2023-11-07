import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { mockStateBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import { mockVerificationApplyPayload } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { KeycloakService } from 'keycloak-angular';

import { OpinionStatement } from 'pmrv-api';

import { SiteVisitsGuard } from './site-visits.guard';

describe('SiteVisitsGuard', () => {
  let guard: SiteVisitsGuard;
  let router: Router;
  let store: CommonTasksStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [KeycloakService],
    });
    guard = TestBed.inject(SiteVisitsGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it("should activate when siteVisitType is IN_PERSON and path is 'in-person-visit'", async () => {
    store.setState(
      mockStateBuild({
        opinionStatement: {
          ...mockVerificationApplyPayload.verificationReport.opinionStatement,
          siteVisit: {
            siteVisitType: 'IN_PERSON',
          },
        } as OpinionStatement,
      }),
    );
    const activatedRouteSnapshotInPersonVisit = new ActivatedRouteSnapshot();
    activatedRouteSnapshotInPersonVisit.url = [
      new UrlSegment('site-visits', {}),
      new UrlSegment('in-person-visit', {}),
    ];
    activatedRouteSnapshotInPersonVisit.params = { taskId: 1 };

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshotInPersonVisit) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it("should activate when siteVisitType is VIRTUAL and path is 'virtual-visit'", async () => {
    store.setState(
      mockStateBuild({
        opinionStatement: {
          ...mockVerificationApplyPayload.verificationReport.opinionStatement,
          siteVisit: {
            siteVisitType: 'VIRTUAL',
          },
        } as OpinionStatement,
      }),
    );
    const activatedRouteSnapshotVirtualVisit = new ActivatedRouteSnapshot();
    activatedRouteSnapshotVirtualVisit.url = [new UrlSegment('site-visits', {}), new UrlSegment('virtual-visit', {})];
    activatedRouteSnapshotVirtualVisit.params = { taskId: 1 };

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshotVirtualVisit) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it("should activate when siteVisitType is NO_VISIT and path is 'no-visit'", async () => {
    store.setState(
      mockStateBuild({
        opinionStatement: {
          ...mockVerificationApplyPayload.verificationReport.opinionStatement,
          siteVisit: {
            siteVisitType: 'NO_VISIT',
          },
        } as OpinionStatement,
      }),
    );
    const activatedRouteSnapshotNoVisit = new ActivatedRouteSnapshot();
    activatedRouteSnapshotNoVisit.url = [new UrlSegment('site-visits', {}), new UrlSegment('no-visit', {})];
    activatedRouteSnapshotNoVisit.params = { taskId: 1 };

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshotNoVisit) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect when siteVisitType is different than last url fragment', async () => {
    store.setState(
      mockStateBuild({
        opinionStatement: {
          ...mockVerificationApplyPayload.verificationReport.opinionStatement,
          siteVisit: {
            siteVisitType: 'IN_PERSON',
          },
        } as OpinionStatement,
      }),
    );
    const activatedRouteSnapshotNoVisit = new ActivatedRouteSnapshot();
    activatedRouteSnapshotNoVisit.url = [new UrlSegment('site-visits', {}), new UrlSegment('no-visit', {})];
    activatedRouteSnapshotNoVisit.params = { taskId: 1 };

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshotNoVisit) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/tasks/1/aer/verification-submit/opinion-statement/site-visits'));
  });
});
