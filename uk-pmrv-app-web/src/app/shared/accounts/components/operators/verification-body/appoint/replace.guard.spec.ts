import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { lastValueFrom, of, throwError } from 'rxjs';

import { AccountVerificationBodyService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub } from '../../../../../../../testing';
import { BusinessTestingModule, expectBusinessErrorToBe } from '../../../../../../error/testing/business-error';
import { viewNotFoundOperatorError } from '../../errors/business-error';
import { ReplaceGuard } from './replace.guard';

describe('ReplaceGuard', () => {
  let guard: ReplaceGuard;
  let accountVerificationBodyService: Partial<jest.Mocked<AccountVerificationBodyService>>;

  const route = new ActivatedRouteSnapshotStub({ accountId: '1' });

  beforeEach(() => {
    accountVerificationBodyService = {
      getVerificationBodyOfAccount: jest.fn(),
    };
    TestBed.configureTestingModule({
      imports: [BusinessTestingModule],
      providers: [{ provide: AccountVerificationBodyService, useValue: accountVerificationBodyService }],
    });
    guard = TestBed.inject(ReplaceGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow access if a verification body is found', async () => {
    accountVerificationBodyService.getVerificationBodyOfAccount.mockReturnValueOnce(of({ id: 1, name: 'testName' }));

    await expect(lastValueFrom(guard.canActivate(route))).resolves.toBeTruthy();
  });

  it('should navigate to error page if a verification body is appointed', async () => {
    accountVerificationBodyService.getVerificationBodyOfAccount.mockReturnValueOnce(
      throwError(() => new HttpErrorResponse({ status: 404 })),
    );

    await expect(lastValueFrom(guard.canActivate(route))).rejects.toBeTruthy();

    await expectBusinessErrorToBe(viewNotFoundOperatorError(1));
  });

  it('should rethrow all other errors', async () => {
    accountVerificationBodyService.getVerificationBodyOfAccount.mockReturnValue(throwError(() => ({ status: 500 })));

    await expect(lastValueFrom(guard.canActivate(route))).rejects.toBeTruthy();
  });
});
