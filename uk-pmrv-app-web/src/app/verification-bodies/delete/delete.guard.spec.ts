import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { lastValueFrom, throwError } from 'rxjs';

import { VerificationBodiesService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, asyncData, mockClass } from '../../../testing';
import { BusinessTestingModule, expectBusinessErrorToBe } from '../../error/testing/business-error';
import { saveNotFoundVerificationBodyError } from '../errors/business-error';
import { verificationBody } from '../testing/mock-data';
import { DeleteGuard } from './delete.guard';

describe('DeleteGuard', () => {
  let guard: DeleteGuard;
  let verificationBodiesService: jest.Mocked<VerificationBodiesService>;

  beforeEach(() => {
    verificationBodiesService = mockClass(VerificationBodiesService);

    TestBed.configureTestingModule({
      imports: [BusinessTestingModule],
      providers: [{ provide: VerificationBodiesService, useValue: verificationBodiesService }],
    });
    guard = TestBed.inject(DeleteGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow access if the body exists and is available to the user', async () => {
    verificationBodiesService.getVerificationBodyById.mockReturnValue(asyncData(verificationBody));

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ verificationBodyId: '1' }))),
    ).resolves.toBeTruthy();
  });

  it('should show the business error page if the body does not exist', async () => {
    verificationBodiesService.getVerificationBodyById.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 404 })),
    );

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ verificationBodyId: '1' }))),
    ).rejects.toBeTruthy();
    await expectBusinessErrorToBe(saveNotFoundVerificationBodyError);
  });
});
