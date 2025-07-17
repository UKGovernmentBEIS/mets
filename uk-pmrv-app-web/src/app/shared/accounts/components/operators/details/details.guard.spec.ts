import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { lastValueFrom, throwError } from 'rxjs';

import { AuthStore } from '@core/store/auth';
import { HttpStatuses } from '@error/http-status';
import { BusinessTestingModule, expectBusinessErrorToBe } from '@error/testing/business-error';
import { ActivatedRouteSnapshotStub, asyncData } from '@testing';

import { OperatorUserDTO, OperatorUsersService, UsersService } from 'pmrv-api';

import { viewNotFoundOperatorError } from '../errors/business-error';
import { DetailsGuard } from './details.guard';

describe('DetailsGuard', () => {
  let guard: DetailsGuard;
  let authStore: AuthStore;
  let usersService: Partial<jest.Mocked<UsersService>>;
  let operatorUsersService: Partial<jest.Mocked<OperatorUsersService>>;

  const operator: OperatorUserDTO = {
    email: 'test@host.com',
    firstName: 'Mary',
    lastName: 'Za',
    mobileNumber: { countryCode: '+30', number: '1234567890' },
    phoneNumber: { countryCode: '+30', number: '123456780' },
  };

  beforeEach(() => {
    operatorUsersService = {
      getOperatorUserById: jest.fn().mockReturnValue(asyncData<OperatorUserDTO>(operator)),
    };

    usersService = {
      getCurrentUser: jest.fn().mockReturnValue(asyncData<OperatorUserDTO>(operator)),
    };

    TestBed.configureTestingModule({
      imports: [BusinessTestingModule],
      providers: [
        DetailsGuard,
        { provide: UsersService, useValue: usersService },
        { provide: OperatorUsersService, useValue: operatorUsersService },
      ],
    });

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({ userId: 'ABC1' });

    guard = TestBed.inject(DetailsGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should provide other user information', async () => {
    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ accountId: '1', userId: 'asdf4' }))),
    ).resolves.toBeTruthy();

    expect(guard.resolve()).toEqual(operator);

    expect(operatorUsersService.getOperatorUserById).toHaveBeenCalledWith(1, 'asdf4');
  });

  it('should provide current user information', async () => {
    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ accountId: '1', userId: 'ABC1' }))),
    ).resolves.toBeTruthy();

    expect(guard.resolve()).toEqual(operator);

    expect(usersService.getCurrentUser).toHaveBeenCalled();
  });

  it('should throw an error when visiting a deleted user', async () => {
    operatorUsersService.getOperatorUserById.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: HttpStatuses.BadRequest, error: { code: 'AUTHORITY1004' } })),
    );

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ accountId: '1', userId: 'asdf4' }))),
    ).rejects.toBeTruthy();

    await expectBusinessErrorToBe(viewNotFoundOperatorError(1));
  });
});
