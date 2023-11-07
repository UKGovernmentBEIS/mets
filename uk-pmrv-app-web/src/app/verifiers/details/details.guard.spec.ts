import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { lastValueFrom, throwError } from 'rxjs';

import { AuthStore } from '@core/store/auth';
import { ErrorCodes } from '@error/business-errors';
import { HttpStatuses } from '@error/http-status';
import { BusinessTestingModule, expectBusinessErrorToBe } from '@error/testing/business-error';
import { ActivatedRouteSnapshotStub, asyncData } from '@testing';

import { UsersService, VerifierUsersService } from 'pmrv-api';

import { viewNotFoundVerifierError } from '../errors/business-error';
import { DetailsGuard } from './details.guard';

describe('VerifierDetailsGuard', () => {
  let guard: DetailsGuard;
  let authStore: AuthStore;

  const response = {
    user: { id: '1', firstName: 'Bob', lastName: 'Squarepants', email: 'bob@squarepants.com', phoneNumber: '123312' },
  };

  let usersService: Partial<jest.Mocked<UsersService>>;
  let verifierUsersService: Partial<jest.Mocked<VerifierUsersService>>;

  beforeEach(() => {
    usersService = {
      getCurrentUser: jest.fn().mockReturnValue(asyncData(response)),
    };
    verifierUsersService = {
      getVerifierUserById: jest.fn().mockReturnValue(asyncData(response)),
    };

    TestBed.configureTestingModule({
      imports: [BusinessTestingModule],
      providers: [
        { provide: UsersService, useValue: usersService },
        { provide: VerifierUsersService, useValue: verifierUsersService },
      ],
    });

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({
      roleType: 'VERIFIER',
      userId: '2',
      domainsLoginStatuses: { INSTALLATION: 'ENABLED' },
    });
    guard = TestBed.inject(DetailsGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow if verifier exists', async () => {
    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ userId: '1' }))),
    ).resolves.toBeTruthy();

    expect(verifierUsersService.getVerifierUserById).toHaveBeenCalledWith('1');
  });

  it('should allow if verifier is the current user', async () => {
    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ userId: '2' }))),
    ).resolves.toBeTruthy();

    expect(usersService.getCurrentUser).toHaveBeenCalled();
  });

  it('should block access if current user is not a verifier', async () => {
    usersService.getCurrentUser.mockReturnValue(throwError(() => 'User not found'));

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ userId: '2' }))),
    ).rejects.toBeTruthy();
  });

  it('should block access if verifier is not found', async () => {
    verifierUsersService.getVerifierUserById.mockReturnValue(
      throwError(
        () => new HttpErrorResponse({ status: HttpStatuses.BadRequest, error: { code: ErrorCodes.AUTHORITY1006 } }),
      ),
    );

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ userId: '1' }))),
    ).rejects.toBeTruthy();

    await expectBusinessErrorToBe(viewNotFoundVerifierError);
  });

  it('should resolve the user', async () => {
    await lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ userId: '1' })));

    expect(guard.resolve()).toEqual(response);
  });
});
