import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';

import { firstValueFrom } from 'rxjs';
import { TestScheduler } from 'rxjs/testing';

import { AuthService } from '@core/services/auth.service';
import { mockClass } from '@testing';
import { KEYCLOAK_EVENT_SIGNAL, KeycloakEvent, KeycloakEventType } from 'keycloak-angular';
import Keycloak from 'keycloak-js';

import { testSchedulerFactory } from '../../../testing/marble-helpers';
import { TimeoutBannerService } from './timeout-banner.service';

describe('TimeoutBannerService', () => {
  let service: TimeoutBannerService;
  let testScheduler: TestScheduler;

  const mockRefreshTokenParsed = { iat: 0, exp: 210 };
  const mockRefreshTokenParsedNoExtension = { iat: 0, exp: 100 };

  const keycloakEventSignal = signal<KeycloakEvent>({ type: KeycloakEventType.KeycloakAngularInit });

  const keycloak: Partial<Keycloak> = {
    refreshTokenParsed: mockRefreshTokenParsed,
    updateToken: jest.fn().mockReturnValue(Promise.resolve(true)),
    logout: jest.fn().mockImplementation(),
  };

  const authService = mockClass(AuthService);

  beforeEach(() => {
    testScheduler = testSchedulerFactory();
    keycloak.refreshTokenParsed = mockRefreshTokenParsed;
    keycloakEventSignal.set({ type: KeycloakEventType.KeycloakAngularInit });

    TestBed.configureTestingModule({
      providers: [
        { provide: Keycloak, useValue: keycloak },
        { provide: KEYCLOAK_EVENT_SIGNAL, useValue: keycloakEventSignal },
        { provide: AuthService, useValue: authService },
      ],
    });
    service = TestBed.inject(TimeoutBannerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should logout user if time limit exceeded', () => {
    testScheduler.run(({ cold, expectObservable, flush }) => {
      jest.useFakeTimers();
      jest.setSystemTime(testScheduler.now());
      cold('-a- 200s', { a: { type: KeycloakEventType.AuthRefreshSuccess } }).subscribe((event) => {
        jest.setSystemTime(testScheduler.now());
        keycloakEventSignal.set(event);
        TestBed.flushEffects();
      });
      expectObservable(service.isVisible$).toBe('a 89s 999ms b 119s 999ms a', { a: false, b: true });

      flush();

      expect(authService.logout).toHaveBeenCalled();
    });
  });

  it('should extend time session', async () => {
    await service.extendSession();

    expect(keycloak.updateToken).toHaveBeenCalled();
    await expect(firstValueFrom(service.isVisible$)).resolves.toBeFalsy();
  });

  it('should not allow time extension', () => {
    testScheduler.run(({ cold, expectObservable }) => {
      jest.useFakeTimers();
      jest.setSystemTime(testScheduler.now());
      keycloak.refreshTokenParsed = mockRefreshTokenParsedNoExtension;
      cold('-a- 200s', { a: { type: KeycloakEventType.AuthRefreshSuccess } }).subscribe((event) => {
        jest.setSystemTime(testScheduler.now());
        keycloakEventSignal.set(event);
        TestBed.flushEffects();
      });
      expectObservable(service.timeExtensionAllowed$).toBe('ab', { a: true, b: false });
    });
  });
});
