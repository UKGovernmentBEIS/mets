import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { AuthStore, LoginStatus, UserState } from '@core/store/auth';
import { MockType } from '@testing';

import { InstallationAccountApplicationGuard } from './installation-account-application.guard';

describe('InstallationAccountApplicationGuard', () => {
  let guard: InstallationAccountApplicationGuard;
  let router: Router;
  let authStore: AuthStore;

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
    authStore.setTerms({ version: 1, url: 'asd' });
    authStore.setUser({ email: 'asd@asd.com', firstName: 'Darth', lastName: 'Vader', termsVersion: 1 });
    guard = TestBed.inject(InstallationAccountApplicationGuard);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it("should redirect to terms if user is logged in and terms don't match", async () => {
    authStore.setTerms({ version: 2, url: 'asd' });
    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(router.parseUrl('terms'));

    authStore.setUser({ email: 'asd@asd.com', firstName: 'Darth', lastName: 'Vader', termsVersion: 2 });
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
