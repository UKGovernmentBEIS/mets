import { TestBed } from '@angular/core/testing';

import { of } from 'rxjs';

import { AuthStore } from '@core/store/auth';
import { mockClass } from '@testing';

import { AviationAccountsService, InstallationAccountsService } from 'pmrv-api';

import { AccountsService } from './accounts.service';

describe('AccountsService', () => {
  let service: AccountsService;
  let authStore: AuthStore;
  const installationService = mockClass(InstallationAccountsService);
  const aviationService = mockClass(AviationAccountsService);
  installationService.getCurrentUserInstallationAccounts.mockReturnValueOnce(of({}));
  aviationService.getCurrentUserAviationAccounts.mockReturnValueOnce(of({}));

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: InstallationAccountsService, useValue: installationService },
        { provide: AviationAccountsService, useValue: aviationService },
      ],
    });
    service = TestBed.inject(AccountsService);
    authStore = TestBed.inject(AuthStore);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call correct service based on domain', () => {
    authStore.setCurrentDomain('INSTALLATION');
    service.getCurrentUserAccounts(1, 30).subscribe();
    expect(installationService.getCurrentUserInstallationAccounts).toHaveBeenCalled();
    expect(aviationService.getCurrentUserAviationAccounts).not.toHaveBeenCalled();

    installationService.getCurrentUserInstallationAccounts.mockClear();
    aviationService.getCurrentUserAviationAccounts.mockClear();

    authStore.setCurrentDomain('AVIATION');
    service.getCurrentUserAccounts(1, 30).subscribe();
    expect(aviationService.getCurrentUserAviationAccounts).toHaveBeenCalled();
    expect(installationService.getCurrentUserInstallationAccounts).not.toHaveBeenCalled();
  });
});
