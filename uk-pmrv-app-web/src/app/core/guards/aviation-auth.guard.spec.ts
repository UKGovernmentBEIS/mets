import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import {
  mockAuthorityService,
  mockKeycloakService,
  mockTermsAndConditionsService,
  mockUsersService,
} from '@core/guards/mocks';
import { AuthStore } from '@core/store/auth';
import { ActivatedRouteStub } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AuthoritiesService, TermsAndConditionsService, UsersService } from 'pmrv-api';

import { AviationAuthGuard } from './aviation-auth.guard';

describe('AviationAuthGuard', () => {
  let guard: AviationAuthGuard;
  let authStore: AuthStore;
  let router: Router;
  const route = new ActivatedRouteStub();

  mockUsersService.registerUserLastLoginDomain.mockReturnValue(of({}));

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        { provide: KeycloakService, useValue: mockKeycloakService },
        { provide: UsersService, useValue: mockUsersService },
        { provide: AuthoritiesService, useValue: mockAuthorityService },
        { provide: TermsAndConditionsService, useValue: mockTermsAndConditionsService },
        { provide: ActivatedRoute, useValue: route },
      ],
    });

    authStore = TestBed.inject(AuthStore);
    router = TestBed.inject(Router);
    guard = TestBed.inject(AviationAuthGuard);
  });

  afterEach(() => {
    mockUsersService.registerUserLastLoginDomain.mockClear();
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow navigation when loginStatus enabled', async () => {
    authStore.setUserState({ domainsLoginStatuses: { AVIATION: 'ENABLED' } });
    const result = await lastValueFrom(guard.canActivate());
    expect(result).toEqual(true);
    expect(mockUsersService.registerUserLastLoginDomain).toHaveBeenCalledTimes(1);
  });

  it('should redirect to landing page when loginStatus not enabled', async () => {
    authStore.setUserState({ domainsLoginStatuses: { AVIATION: 'DISABLED' } });
    const result = await lastValueFrom(guard.canActivate());
    expect(result).toEqual(router.parseUrl('landing'));
    expect(mockUsersService.registerUserLastLoginDomain).not.toHaveBeenCalled();
  });
});
