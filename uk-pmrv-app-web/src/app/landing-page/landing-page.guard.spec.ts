import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { AuthStore } from '@core/store/auth';
import { MockType } from '@testing';

import { LandingPageGuard } from './landing-page.guard';

describe('LandingPageGuard', () => {
  let guard: LandingPageGuard;
  let router: Router;
  let authStore: AuthStore;

  const authService: MockType<AuthService> = {
    checkUser: jest.fn(() => of(undefined)),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: AuthService, useValue: authService }, LandingPageGuard],
    });

    authStore = TestBed.inject(AuthStore);
    authStore.setIsLoggedIn(true);
    authStore.setUserState({ domainsLoginStatuses: { INSTALLATION: 'ENABLED' } });
    authStore.setTerms({ version: 1, url: 'asd' });
    authStore.setUser({ email: 'asd@asd.com', firstName: 'Darth', lastName: 'Vader', termsVersion: 1 });
    guard = TestBed.inject(LandingPageGuard);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow if user is not logged in', () => {
    authStore.setIsLoggedIn(false);
    return expect(lastValueFrom(guard.canActivate())).resolves.toEqual(true);
  });

  it('should allow if user is logged in and terms match and status is not ENABLED', () => {
    authStore.setUserState({ domainsLoginStatuses: { INSTALLATION: 'DISABLED' } });
    return expect(lastValueFrom(guard.canActivate())).resolves.toEqual(true);
  });

  it('should allow if user is logged in and no authority', () => {
    authStore.setIsLoggedIn(true);
    authStore.setUserState({ domainsLoginStatuses: { INSTALLATION: 'NO_AUTHORITY' } });
    return expect(lastValueFrom(guard.canActivate())).resolves.toEqual(true);
  });

  it('should redirect to dashboard if user is logged in and enabled and terms match', async () => {
    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(router.parseUrl('dashboard'));

    authStore.setTerms({ version: 2, url: 'asd' });
    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(router.parseUrl('terms'));
  });

  it('should allow if user is logged in and adding another installation', async () => {
    authStore.setIsLoggedIn(true);
    authStore.setUserState({ lastLoginDomain: 'INSTALLATION', domainsLoginStatuses: { INSTALLATION: 'ENABLED' } });
    authStore.setTerms({ version: 1, url: 'asd' });
    authStore.setUser({ email: 'asd@asd.com', firstName: 'Darth', lastName: 'Vader', termsVersion: 1 });

    jest
      .spyOn(router, 'getCurrentNavigation')
      .mockReturnValue({ extras: { state: { addAnotherInstallation: true } } } as any);
    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(true);
  });

  it('should allow if user is logged in, has both domains and adding another installation', async () => {
    authStore.setIsLoggedIn(true);
    authStore.setUserState({
      lastLoginDomain: 'INSTALLATION',
      domainsLoginStatuses: { INSTALLATION: 'ENABLED', AVIATION: 'ACCEPTED' },
    });
    authStore.setTerms({ version: 1, url: 'asd' });
    authStore.setUser({ email: 'asd@asd.com', firstName: 'Darth', lastName: 'Vader', termsVersion: 1 });

    jest
      .spyOn(router, 'getCurrentNavigation')
      .mockReturnValue({ extras: { state: { addAnotherInstallation: true } } } as any);
    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(true);
  });

  it(`should allow if INSTALLATION login status is 'DISABLED' and AVIATION login status is 'ENABLED'
  and last login domain is 'INSTALLATION'`, async () => {
    authStore.setUserState({
      domainsLoginStatuses: {
        INSTALLATION: 'DISABLED',
        AVIATION: 'ENABLED',
      },
      lastLoginDomain: 'INSTALLATION',
    });

    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(true);
  });

  it(`should allow if AVIATION login status is 'DISABLED' and INSTALLATION login status is 'ENABLED'
  and last login domain is 'AVIATION'`, async () => {
    authStore.setUserState({
      domainsLoginStatuses: {
        INSTALLATION: 'ENABLED',
        AVIATION: 'DISABLED',
      },
      lastLoginDomain: 'AVIATION',
    });

    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(true);
  });

  it(`should allow when user has single login with status 'ACCEPTED'`, async () => {
    authStore.setUserState({
      domainsLoginStatuses: {
        INSTALLATION: 'ACCEPTED',
        AVIATION: 'NO_AUTHORITY',
      },
    });

    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(true);
  });

  it(`should allow when user has multiple logins one of which has status 'ACCEPTED' and changes domain to that login`, async () => {
    authStore.setUserState({
      domainsLoginStatuses: {
        INSTALLATION: 'ACCEPTED',
        AVIATION: 'ENABLED',
      },
      lastLoginDomain: 'AVIATION',
    });
    authStore.setSwitchingDomain('INSTALLATION');

    await expect(lastValueFrom(guard.canActivate())).resolves.toBe(true);
  });

  it('should allow when user has only aviation login and switches to installation domain (add new installation account)', async () => {
    authStore.setUserState({
      domainsLoginStatuses: {
        INSTALLATION: 'NO_AUTHORITY',
        AVIATION: 'ENABLED',
      },
      lastLoginDomain: 'AVIATION',
    });
    authStore.setCurrentDomain('AVIATION');
    authStore.setSwitchingDomain('INSTALLATION');

    const allow = await lastValueFrom(guard.canActivate());
    expect(allow).toEqual(true);
  });

  it(`should redirect to dashboard when user is REGULATOR or VERIFIER and has NO_AUTHORITY in all domains`, async () => {
    authStore.setUserState({
      roleType: 'REGULATOR',
      domainsLoginStatuses: {
        INSTALLATION: 'NO_AUTHORITY',
        AVIATION: 'NO_AUTHORITY',
      },
    });
    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(router.parseUrl('dashboard'));

    authStore.setUserState({ ...authStore.getState().userState, roleType: 'REGULATOR' });
    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(router.parseUrl('dashboard'));

    authStore.setUserState({
      ...authStore.getState().userState,
      roleType: 'OPERATOR',
    });
    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(true);
  });

  it(`should allow when user has login 'DISABLED' or 'TEMP_DISABLED'`, async () => {
    authStore.setUserState({
      roleType: 'VERIFIER',
      domainsLoginStatuses: {
        INSTALLATION: 'TEMP_DISABLED',
        AVIATION: 'NO_AUTHORITY',
      },
      lastLoginDomain: 'INSTALLATION',
    });

    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(true);
  });
});
