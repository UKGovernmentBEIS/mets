import { TestBed } from '@angular/core/testing';

import { lastValueFrom } from 'rxjs';

import { VerificationBodiesService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, asyncData, mockClass } from '../../../testing';
import { BusinessTestingModule } from '../../error/testing/business-error';
import { verificationBody } from '../testing/mock-data';
import { DetailsGuard } from './details.guard';

describe('DetailsGuard', () => {
  let guard: DetailsGuard;
  let verificationBodiesService: jest.Mocked<VerificationBodiesService>;

  beforeEach(() => {
    verificationBodiesService = mockClass(VerificationBodiesService);

    TestBed.configureTestingModule({
      imports: [BusinessTestingModule],
      providers: [{ provide: VerificationBodiesService, useValue: verificationBodiesService }],
    });
    guard = TestBed.inject(DetailsGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow access if the verification body exists and is accessible to the user', async () => {
    verificationBodiesService.getVerificationBodyById.mockReturnValue(asyncData(verificationBody));

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ verificationBodyId: '1' }))),
    ).resolves.toBeTruthy();
  });

  it('should resolve the verification body', async () => {
    verificationBodiesService.getVerificationBodyById.mockReturnValue(asyncData(verificationBody));
    await lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ verificationBodyId: '1' })));

    expect(guard.resolve()).toEqual(verificationBody);
    expect(verificationBodiesService.getVerificationBodyById).toHaveBeenCalledTimes(1);
  });
});
