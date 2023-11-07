import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable, of } from 'rxjs';

import { ActivatedRouteSnapshotStub, mockClass, MockType } from '@testing';

import { RequestsService } from 'pmrv-api';

import { mockPermitBatchReissues } from '../../shared/components/batch-reissue-requests/testing/mock-data';
import { BatchReissueSubmitGuard } from './batch-reissue-submit.guard';

describe('BatchReissueSubmitGuard', () => {
  let guard: BatchReissueSubmitGuard;
  let router: Router;
  let requestsService: MockType<RequestsService>;

  beforeEach(() => {
    requestsService = mockClass(RequestsService);
  });

  describe('when has permission', () => {
    beforeEach(() => {
      requestsService.getBatchReissueRequests.mockReturnValueOnce(
        of({ ...mockPermitBatchReissues, canInitiateBatchReissue: true }),
      );

      TestBed.configureTestingModule({
        imports: [RouterTestingModule, HttpClientTestingModule],
        providers: [{ provide: RequestsService, useValue: requestsService }],
      });
      guard = TestBed.inject(BatchReissueSubmitGuard);
      router = TestBed.inject(Router);
    });

    it('should be created', () => {
      expect(guard).toBeTruthy();
    });

    it('should activate', async () => {
      await expect(
        firstValueFrom(
          guard.canActivate(new ActivatedRouteSnapshotStub({}, {}, { accountType: 'INSTALLATION' })) as Observable<
            true | UrlTree
          >,
        ),
      ).resolves.toEqual(true);
    });
  });

  describe('when does not have permission', () => {
    beforeEach(() => {
      requestsService.getBatchReissueRequests.mockReturnValueOnce(
        of({ ...mockPermitBatchReissues, canInitiateBatchReissue: false }),
      );

      TestBed.configureTestingModule({
        imports: [RouterTestingModule, HttpClientTestingModule],
        providers: [{ provide: RequestsService, useValue: requestsService }],
      });
      guard = TestBed.inject(BatchReissueSubmitGuard);
      router = TestBed.inject(Router);
    });

    it('should be created', () => {
      expect(guard).toBeTruthy();
    });

    it('should not activate', async () => {
      await expect(
        firstValueFrom(
          guard.canActivate(new ActivatedRouteSnapshotStub({}, {}, { accountType: 'INSTALLATION' })) as Observable<
            true | UrlTree
          >,
        ),
      ).resolves.toEqual(router.parseUrl(`/landing`));
    });
  });
});
