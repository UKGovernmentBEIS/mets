import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom } from 'rxjs';

import { AuthStore } from '@core/store/auth';

import {
  AviationAccountEmpDTO,
  AviationAccountViewService,
  InstallationAccountDTO,
  InstallationAccountPermitDTO,
  InstallationAccountViewService,
} from 'pmrv-api';

import { ActivatedRouteSnapshotStub, asyncData } from '../../../../../../testing';
import { mockedAccountPermit } from '../../../../../accounts/testing/mock-data';
import { mockedAccount } from '../../../../../aviation/accounts/testing/mock-data';
import { AccountStatusGuard } from './account-status.guard';

describe('AddUserGuard', () => {
  let guard: AccountStatusGuard;
  let router: Router;
  let installationAccountViewService: Partial<jest.Mocked<InstallationAccountViewService>>;
  let aviationAccountViewService: Partial<jest.Mocked<AviationAccountViewService>>;
  let authStore: AuthStore;

  beforeEach(() => {
    installationAccountViewService = {
      getInstallationAccountById: jest
        .fn()
        .mockReturnValue(asyncData<InstallationAccountPermitDTO>(mockedAccountPermit)),
    };
    aviationAccountViewService = {
      getAviationAccountById: jest.fn().mockReturnValue(asyncData<AviationAccountEmpDTO>(mockedAccount)),
    };

    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientModule],
      providers: [
        { provide: InstallationAccountViewService, useValue: installationAccountViewService },
        { provide: AviationAccountViewService, useValue: aviationAccountViewService },
      ],
    });
    authStore = TestBed.inject(AuthStore);
    authStore.setCurrentDomain('INSTALLATION');
    guard = TestBed.inject(AccountStatusGuard);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should be activated', async () => {
    expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ accountId: mockedAccountPermit.account.id }))),
    ).resolves.toBeTruthy();

    expect(installationAccountViewService.getInstallationAccountById).toHaveBeenCalledWith(
      mockedAccountPermit.account.id,
    );
  });

  it('should not be activated', async () => {
    const unapprovedAccountPermit: InstallationAccountPermitDTO = {
      ...mockedAccountPermit,
      account: { ...mockedAccountPermit.account, status: 'UNAPPROVED' } as InstallationAccountDTO,
    };
    installationAccountViewService.getInstallationAccountById.mockReturnValue(
      asyncData<InstallationAccountPermitDTO>(unapprovedAccountPermit),
    );

    expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ accountId: mockedAccountPermit.account.id }))),
    ).resolves.toEqual(router.parseUrl(`/accounts/${mockedAccountPermit.account.id}`));

    expect(installationAccountViewService.getInstallationAccountById).toHaveBeenCalledWith(
      mockedAccountPermit.account.id,
    );

    const deniedAccountPermit: InstallationAccountPermitDTO = {
      ...mockedAccountPermit,
      account: { ...mockedAccountPermit.account, status: 'DENIED' } as InstallationAccountDTO,
    };
    installationAccountViewService.getInstallationAccountById.mockReturnValue(
      asyncData<InstallationAccountPermitDTO>(deniedAccountPermit),
    );

    expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ accountId: mockedAccountPermit.account.id }))),
    ).resolves.toEqual(router.parseUrl(`/accounts/${mockedAccountPermit.account.id}`));

    expect(installationAccountViewService.getInstallationAccountById).toHaveBeenCalledWith(
      mockedAccountPermit.account.id,
    );
  });

  it('should be activated for aviation', async () => {
    authStore.setCurrentDomain('AVIATION');
    expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ accountId: mockedAccount.aviationAccount.id }))),
    ).resolves.toBeTruthy();

    expect(aviationAccountViewService.getAviationAccountById).toHaveBeenCalledWith(mockedAccount.aviationAccount.id);
  });
});
