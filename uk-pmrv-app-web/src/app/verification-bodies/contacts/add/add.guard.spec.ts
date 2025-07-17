import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, throwError } from 'rxjs';

import { HttpStatuses } from '@error/http-status';
import { BusinessTestingModule, expectBusinessErrorToBe } from '@error/testing/business-error';
import { ActivatedRouteSnapshotStub, asyncData, RouterStubComponent } from '@testing';

import { VerificationBodiesService } from 'pmrv-api';

import { disabledVerificationBodyError, viewNotFoundVerificationBodyError } from '../../errors/business-error';
import { AddGuard } from './add.guard';

describe('AddGuard', () => {
  let guard: AddGuard;

  const verificationBodiesService: Partial<jest.Mocked<VerificationBodiesService>> = {
    getVerificationBodyById: jest.fn(),
  };

  beforeEach(() => {
    jest.clearAllMocks();

    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([
          {
            path: 'verification-bodies/:verificationBodyId/disabled',
            component: RouterStubComponent,
          },
        ]),
        BusinessTestingModule,
      ],
      declarations: [RouterStubComponent],
      providers: [{ provide: VerificationBodiesService, useValue: verificationBodiesService }],
    });

    guard = TestBed.inject(AddGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should block access if the body is disabled', async () => {
    verificationBodiesService.getVerificationBodyById.mockReturnValue(asyncData({ status: 'DISABLED' }));

    const snapshot = new ActivatedRouteSnapshotStub({ verificationBodyId: '1' });

    await expect(lastValueFrom(guard.canActivate(snapshot))).rejects.toBeTruthy();

    await expectBusinessErrorToBe(disabledVerificationBodyError);
  });

  it('should allow access if the body is active or pending', async () => {
    verificationBodiesService.getVerificationBodyById.mockReturnValue(asyncData({ status: 'ACTIVE' }));

    const snapshot = new ActivatedRouteSnapshotStub({ verificationBodyId: '1' });

    await expect(lastValueFrom(guard.canActivate(snapshot))).resolves.toBeTruthy();

    verificationBodiesService.getVerificationBodyById.mockReturnValue(asyncData({ status: 'PENDING' }));

    await expect(lastValueFrom(guard.canActivate(snapshot))).resolves.toBeTruthy();
  });

  it('should display the error page if the verification body is not found', async () => {
    verificationBodiesService.getVerificationBodyById.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: HttpStatuses.NotFound })),
    );

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ verificationBodyId: '1' }))),
    ).rejects.toBeTruthy();

    await expectBusinessErrorToBe(viewNotFoundVerificationBodyError);
  });
});
