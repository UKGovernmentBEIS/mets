import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of, throwError } from 'rxjs';

import { AccountVerificationBodyService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub } from '../../../../../../../testing';
import { BusinessTestingModule, expectBusinessErrorToBe } from '../../../../../../error/testing/business-error';
import { appointedVerificationBodyError } from '../../errors/business-error';
import { AppointGuard } from './appoint.guard';

describe('AppointGuard', () => {
  let guard: AppointGuard;
  let accountVerificationBodyService: Partial<jest.Mocked<AccountVerificationBodyService>>;

  const route = new ActivatedRouteSnapshotStub({ accountId: '1' });

  beforeEach(() => {
    accountVerificationBodyService = {
      getVerificationBodyOfAccount: jest.fn(),
    };
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, BusinessTestingModule],
      providers: [{ provide: AccountVerificationBodyService, useValue: accountVerificationBodyService }],
    });
    guard = TestBed.inject(AppointGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow access if a verification body is not found', async () => {
    accountVerificationBodyService.getVerificationBodyOfAccount.mockReturnValue(of(null));

    await expect(lastValueFrom(guard.canActivate(route))).resolves.toBeTruthy();
  });

  it('should navigate to error page if a verification body is appointed', async () => {
    accountVerificationBodyService.getVerificationBodyOfAccount.mockReturnValue(of({ id: 1, name: 'Verifier' }));

    await expect(lastValueFrom(guard.canActivate(route))).rejects.toBeTruthy();

    await expectBusinessErrorToBe(appointedVerificationBodyError(1));
  });

  it('should rethrow all other errors', async () => {
    accountVerificationBodyService.getVerificationBodyOfAccount.mockReturnValue(throwError(() => ({ status: 500 })));

    await expect(lastValueFrom(guard.canActivate(route))).rejects.toBeTruthy();
  });
});
