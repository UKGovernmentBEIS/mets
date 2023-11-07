import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { lastValueFrom, throwError } from 'rxjs';

import { AuthStore } from '@core/store/auth';
import { HttpStatuses } from '@error/http-status';
import { BusinessTestingModule, expectBusinessErrorToBe } from '@error/testing/business-error';
import { ActivatedRouteSnapshotStub, asyncData } from '@testing';

import { RegulatorUserDTO, RegulatorUsersService, UsersService } from 'pmrv-api';

import { viewNotFoundRegulatorError } from '../errors/business-error';
import { DetailsResolver } from './details.resolver';

describe('DetailsResolver', () => {
  let resolver: DetailsResolver;
  let regulatorUsersService: Partial<jest.Mocked<RegulatorUsersService>>;
  let usersService: Partial<jest.Mocked<UsersService>>;
  let authStore: AuthStore;

  const user: RegulatorUserDTO = {
    email: 'test@host.com',
    firstName: 'John',
    lastName: 'Doe',
    jobTitle: 'developer',
    phoneNumber: '23456',
  };

  beforeEach(() => {
    regulatorUsersService = { getRegulatorUserByCaAndId: jest.fn().mockReturnValue(asyncData(user)) };
    usersService = { getCurrentUser: jest.fn().mockReturnValue(asyncData(user)) };

    TestBed.configureTestingModule({
      imports: [BusinessTestingModule],
      providers: [
        { provide: RegulatorUsersService, useValue: regulatorUsersService },
        { provide: UsersService, useValue: usersService },
      ],
    });

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({
      roleType: 'REGULATOR',
      domainsLoginStatuses: { INSTALLATION: 'ENABLED' },
      userId: 'ABC1',
    });
    resolver = TestBed.inject(DetailsResolver);
  });

  it('should be created', () => {
    expect(resolver).toBeTruthy();
  });

  it('should provide the information of another user', async () => {
    await expect(
      lastValueFrom(resolver.resolve(new ActivatedRouteSnapshotStub({ userId: '1234567' }))),
    ).resolves.toEqual(user);

    expect(regulatorUsersService.getRegulatorUserByCaAndId).toHaveBeenCalledWith('1234567');
  });

  it('should provide current user information', async () => {
    await expect(
      lastValueFrom(resolver.resolve(new ActivatedRouteSnapshotStub({ accountId: '1', userId: 'ABC1' }))),
    ).resolves.toEqual(user);

    expect(usersService.getCurrentUser).toHaveBeenCalled();
  });

  it('should return to regulator list when visiting a deleted user', async () => {
    usersService.getCurrentUser.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: HttpStatuses.BadRequest,
            error: { code: 'AUTHORITY1003', message: 'User is not related to competent authority', data: [] },
          }),
      ),
    );

    await expect(
      lastValueFrom(resolver.resolve(new ActivatedRouteSnapshotStub({ accountId: '1', userId: 'ABC1' }))),
    ).rejects.toBeTruthy();
    await expectBusinessErrorToBe(viewNotFoundRegulatorError);
  });
});
