import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';

import { lastValueFrom } from 'rxjs';

import { AuthStore } from '@core/store/auth';

import { InstallationAccountDTO, InstallationAccountPermitDTO, InstallationAccountViewService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, asyncData } from '../../../testing';
import { mockedAccountPermit } from '../../accounts/testing/mock-data';
import { FirstYearStatusGuard } from './first-year-status.guard';

describe('AddFirstYearObligationGuard', () => {
  let guard: FirstYearStatusGuard;
  let router: Router;
  let installationAccountViewService: Partial<jest.Mocked<InstallationAccountViewService>>;
  let authStore: AuthStore;

  beforeEach(() => {
    installationAccountViewService = {
      getInstallationAccountById: jest
        .fn()
        .mockReturnValue(asyncData<InstallationAccountPermitDTO>(mockedAccountPermit)),
    };

    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        provideHttpClient(),
        { provide: InstallationAccountViewService, useValue: installationAccountViewService },
      ],
    });
    authStore = TestBed.inject(AuthStore);
    authStore.setCurrentDomain('INSTALLATION');
    guard = TestBed.inject(FirstYearStatusGuard);
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
    const newAccountPermit: InstallationAccountPermitDTO = {
      ...mockedAccountPermit,
      account: { ...mockedAccountPermit.account, status: 'NEW' } as InstallationAccountDTO,
    };
    installationAccountViewService.getInstallationAccountById.mockReturnValue(
      asyncData<InstallationAccountPermitDTO>(newAccountPermit),
    );

    expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ accountId: mockedAccountPermit.account.id }))),
    ).resolves.toEqual(router.parseUrl(`/accounts/${mockedAccountPermit.account.id}`));

    expect(installationAccountViewService.getInstallationAccountById).toHaveBeenCalledWith(
      mockedAccountPermit.account.id,
    );
  });
});
