import { APP_BASE_HREF } from '@angular/common';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, of } from 'rxjs';

import { ConfigStore } from '@core/config/config.store';
import {
  AuthStore,
  initialState,
  selectIsLoggedIn,
  selectUser,
  selectUserProfile,
  selectUserState,
  selectUserTerms,
  UserState,
} from '@core/store';
import { ActivatedRouteSnapshotStub, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AuthoritiesService, TermsAndConditionsService, TermsDTO, UsersService, UserTermsVersionDTO } from 'pmrv-api';

import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let authStore: AuthStore;
  let configStore: ConfigStore;
  let activatedRoute: ActivatedRoute;

  const keycloakService = mockClass(KeycloakService);
  keycloakService.logout.mockReturnValue(Promise.resolve());

  const user = {
    email: 'test@test.com',
    firstName: 'test',
    lastName: 'test',
  };
  const userState: UserState = {
    domainsLoginStatuses: { INSTALLATION: 'ENABLED' },
    roleType: 'OPERATOR',
    userId: 'opTestId',
  };

  const usersService: Partial<jest.Mocked<UsersService>> = {
    getCurrentUser: jest.fn().mockReturnValue(of(user)),
  };

  const authoritiesService: Partial<jest.Mocked<AuthoritiesService>> = {
    getCurrentUserState: jest.fn().mockReturnValue(of(userState)),
  };

  const latestTerms: TermsDTO = { url: '/test', version: 1 };
  const userTerms: UserTermsVersionDTO = { termsVersion: 1 };
  const termsService: Partial<jest.Mocked<TermsAndConditionsService>> = {
    getLatestTerms: jest.fn().mockReturnValue(of(latestTerms)),
    getUserTerms: jest.fn().mockReturnValue(of(userTerms)),
  };
  const baseHref = '/installation-aviation/';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        { provide: KeycloakService, useValue: keycloakService },
        { provide: UsersService, useValue: usersService },
        { provide: AuthoritiesService, useValue: authoritiesService },
        { provide: TermsAndConditionsService, useValue: termsService },
        { provide: APP_BASE_HREF, useValue: baseHref },
      ],
    });

    authStore = TestBed.inject(AuthStore);
    configStore = TestBed.inject(ConfigStore);
    configStore.setState({ features: { terms: true } });

    service = TestBed.inject(AuthService);
    activatedRoute = TestBed.inject(ActivatedRoute);
    keycloakService.loadUserProfile.mockResolvedValue({ email: 'test@test.com' });
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should login', async () => {
    await service.login();
    await service.loadUser();

    expect(keycloakService.login).toHaveBeenCalledTimes(1);
    expect(keycloakService.login).toHaveBeenCalledWith({});
    expect(usersService.getCurrentUser).toHaveBeenCalledTimes(1);
  });

  it('should logout', async () => {
    await service.logout();

    expect(keycloakService.logout).toHaveBeenCalled();
  });

  it('should load and update user status', async () => {
    await expect(firstValueFrom(authStore.pipe(selectUserState))).resolves.toBeNull();
    await expect(firstValueFrom(service.loadUserState())).resolves.toEqual(userState);
    await expect(firstValueFrom(authStore.pipe(selectUserState))).resolves.toEqual(userState);
  });

  it('should update all user info when checkUser is called', async () => {
    await expect(firstValueFrom(authStore.asObservable())).resolves.toEqual(initialState);
    keycloakService.isLoggedIn.mockReturnValue(false);

    await expect(firstValueFrom(service.checkUser())).resolves.toBeUndefined();

    await expect(firstValueFrom(authStore.pipe(selectIsLoggedIn))).resolves.toBeFalsy();
    await expect(firstValueFrom(authStore.pipe(selectUserState))).resolves.toBeNull();
    await expect(firstValueFrom(authStore.pipe(selectUserTerms))).resolves.toBeNull();
    await expect(firstValueFrom(authStore.pipe(selectUser))).resolves.toBeNull();
    await expect(firstValueFrom(authStore.pipe(selectUserProfile))).resolves.toBeNull();

    authStore.setIsLoggedIn(null);
    keycloakService.isLoggedIn.mockReturnValue(true);

    await expect(firstValueFrom(service.checkUser())).resolves.toBeUndefined();

    await expect(firstValueFrom(authStore.pipe(selectIsLoggedIn))).resolves.toBeTruthy();
    await expect(firstValueFrom(authStore.pipe(selectUserState))).resolves.toEqual(userState);
    await expect(firstValueFrom(authStore.pipe(selectUserTerms))).resolves.toEqual(userTerms);
    await expect(firstValueFrom(authStore.pipe(selectUser))).resolves.toEqual(user);
    await expect(firstValueFrom(authStore.pipe(selectUserProfile))).resolves.toEqual({ email: 'test@test.com' });
  });

  it('should not update user info if logged in is already determined', async () => {
    authStore.setIsLoggedIn(false);
    const spy = jest.spyOn(service, 'loadUserState');

    await expect(firstValueFrom(service.checkUser())).resolves.toBeUndefined();
    expect(spy).not.toHaveBeenCalled();
  });

  it('should redirect to origin if leaf data is blocking sign in redirect', async () => {
    (<any>activatedRoute.snapshot) = new ActivatedRouteSnapshotStub(undefined, undefined, {
      blockSignInRedirect: true,
    });

    await service.login();

    expect(keycloakService.login).toHaveBeenCalledTimes(1);
    expect(keycloakService.login).toHaveBeenCalledWith({ redirectUri: service.baseRedirectUri });
  });

  it('should not invoke load user terms service when terms feature is disabled', async () => {
    configStore.setState({ features: { terms: false } });
    await service.loadUserTerms();

    expect(termsService.getUserTerms).not.toHaveBeenCalled();
    await expect(firstValueFrom(authStore.pipe(selectUserTerms))).resolves.toBeNull();
  });
});
