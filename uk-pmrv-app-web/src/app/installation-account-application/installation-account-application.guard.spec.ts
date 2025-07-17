import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import { ConfigStore } from '@core/config/config.store';
import { AuthService } from '@core/services/auth.service';
import { AuthStore, LoginStatus, UserState } from '@core/store/auth';
import { LatestTermsStore } from '@core/store/latest-terms/latest-terms.store';
import { MockType } from '@testing';

import { InstallationAccountApplicationGuard } from './installation-account-application.guard';

describe('InstallationAccountApplicationGuard', () => {
  let guard: InstallationAccountApplicationGuard;
  let router: Router;
  let authStore: AuthStore;
  let latestTermsStore: LatestTermsStore;
  let configStore: ConfigStore;

  const authService: MockType<AuthService> = {
    checkUser: jest.fn(() => of(undefined)),
  };

  const setUser = (roleType: UserState['roleType'], loginStatus?: LoginStatus) => {
    authStore.setUserState({ roleType, domainsLoginStatuses: { INSTALLATION: loginStatus } });
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: AuthService, useValue: authService }, InstallationAccountApplicationGuard],
    });

    authStore = TestBed.inject(AuthStore);
    authStore.setIsLoggedIn(true);
    authStore.setUserState({ domainsLoginStatuses: { INSTALLATION: 'DISABLED' } });
    authStore.setUser({ email: 'asd@asd.com', firstName: 'Darth', lastName: 'Vader' });
    authStore.setUserTerms({ termsVersion: 1 });

    latestTermsStore = TestBed.inject(LatestTermsStore);
    latestTermsStore.setLatestTerms({ version: 1, url: 'asd' });

    configStore = TestBed.inject(ConfigStore);
    configStore.setState({ features: { terms: true } });

    guard = TestBed.inject(InstallationAccountApplicationGuard);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it("should redirect to terms if user is logged in and terms don't match", async () => {
    latestTermsStore.setLatestTerms({ version: 2, url: 'asd' });
    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(router.parseUrl('terms'));

    authStore.setUserTerms({ termsVersion: 2 });
    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(router.parseUrl(''));
  });

  it('should redirect to landing page if user is not logged in', async () => {
    authStore.setIsLoggedIn(false);
    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(router.parseUrl(''));
  });

  it('should redirect to landing page if user is regulator, logged in and not enabled', async () => {
    authStore.setIsLoggedIn(true);
    setUser('REGULATOR', 'DISABLED');
    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(router.parseUrl(''));
  });

  it('should allow logged in operator when terms feature is disabled', async () => {
    authStore.setIsLoggedIn(true);
    setUser('OPERATOR');
    configStore.setState({ features: { terms: false } });

    await expect(lastValueFrom(guard.canActivate())).resolves.toBeTruthy();
  });

  it('should allow disabled operator or operator with no authority', async () => {
    authStore.setIsLoggedIn(true);
    setUser('OPERATOR', 'DISABLED');
    await expect(lastValueFrom(guard.canActivate())).resolves.toBeTruthy();

    authStore.setIsLoggedIn(true);
    setUser('OPERATOR', 'NO_AUTHORITY');
    await expect(lastValueFrom(guard.canActivate())).resolves.toBeTruthy();
  });

  it('should redirect to landing page if user is verifier, logged in and not enabled', async () => {
    authStore.setIsLoggedIn(true);
    setUser('VERIFIER', 'DISABLED');
    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(router.parseUrl(''));
  });

  it('should allow verifier user', async () => {
    authStore.setIsLoggedIn(true);
    setUser('VERIFIER', 'ENABLED');
    await expect(lastValueFrom(guard.canActivate())).resolves.toBeTruthy();
  });
});
