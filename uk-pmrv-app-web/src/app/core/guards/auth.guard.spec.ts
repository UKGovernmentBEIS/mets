import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import { ConfigStore } from '@core/config/config.store';
import { AuthStore } from '@core/store/auth';
import { LatestTermsStore } from '@core/store/latest-terms/latest-terms.store';

import { MockType } from '../../../testing';
import { AuthService } from '../services/auth.service';
import { AuthGuard } from './auth.guard';

describe('AuthGuard', () => {
  let guard: AuthGuard;
  let router: Router;
  let authStore: AuthStore;
  let latestTermsStore: LatestTermsStore;
  let configStore: ConfigStore;

  const authService: MockType<AuthService> = {
    checkUser: jest.fn(() => of(undefined)),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [{ provide: AuthService, useValue: authService }],
    });

    authStore = TestBed.inject(AuthStore);
    authStore.setCurrentDomain('INSTALLATION');

    latestTermsStore = TestBed.inject(LatestTermsStore);

    configStore = TestBed.inject(ConfigStore);
    configStore.setState({ features: { terms: true } });

    guard = TestBed.inject(AuthGuard);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it("should redirect to terms if user is logged in and terms don't match and terms feature is enabled", async () => {
    authStore.setIsLoggedIn(true);
    authStore.setUserState({ domainsLoginStatuses: { INSTALLATION: 'DISABLED' } });
    latestTermsStore.setLatestTerms({ version: 2, url: 'asd' });
    authStore.setUserTerms({ termsVersion: 1 });

    configStore.setState({ features: { terms: true } });

    let res = await lastValueFrom(guard.canActivate());
    expect(res).toEqual(router.parseUrl('terms'));

    authStore.setUserTerms({ termsVersion: 2 });
    res = await lastValueFrom(guard.canActivate());
    expect(res).toEqual(router.parseUrl('landing'));

    authStore.setUserState({ domainsLoginStatuses: { INSTALLATION: 'ENABLED' } });
    res = await lastValueFrom(guard.canActivate());
    expect(res).toEqual(true);
  });

  it('should redirect to landing page if user is not logged in or is disabled and terms feature is enabled', async () => {
    authStore.setIsLoggedIn(false);
    configStore.setState({ features: { terms: true } });

    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(router.parseUrl('landing'));

    authStore.setIsLoggedIn(true);
    latestTermsStore.setLatestTerms({ version: 1, url: 'asd' });
    authStore.setUserTerms({ termsVersion: 1 });
    authStore.setUserState({ domainsLoginStatuses: { INSTALLATION: 'DISABLED' } });
    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(router.parseUrl('landing'));

    authStore.setIsLoggedIn(true);
    authStore.setUserState({ domainsLoginStatuses: { INSTALLATION: 'TEMP_DISABLED' } });
    await expect(lastValueFrom(guard.canActivate())).resolves.toEqual(router.parseUrl('landing'));
  });

  it('should allow access if user is logged in and not disabled and terms feature is disabled', async () => {
    authStore.setIsLoggedIn(true);
    configStore.setState({ features: { terms: false } });
    authStore.setUserState({ domainsLoginStatuses: { INSTALLATION: 'ENABLED' } });
    const result = await lastValueFrom(guard.canActivate());
    expect(result).toEqual(true);
  });
});
