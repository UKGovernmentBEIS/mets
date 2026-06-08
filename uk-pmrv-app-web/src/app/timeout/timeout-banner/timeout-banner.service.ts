import { effect, inject, Injectable } from '@angular/core';

import { BehaviorSubject, EMPTY, switchMap, tap, timer } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { KEYCLOAK_EVENT_SIGNAL, KeycloakEventType } from 'keycloak-angular';
import Keycloak from 'keycloak-js';

import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class TimeoutBannerService {
  private readonly keycloak = inject(Keycloak);
  private readonly keycloakEvent = inject(KEYCLOAK_EVENT_SIGNAL);
  private readonly authService = inject(AuthService);

  timeOffsetSeconds = environment.timeoutBanner.timeOffsetSeconds;

  timeExtensionAllowed$ = new BehaviorSubject<boolean>(true);
  isVisible$ = new BehaviorSubject<boolean>(false);

  countDownTime$ = new BehaviorSubject<number>(this.calculateCountdownTime());
  private initialRefreshTokenExpOffset = this.refreshTokenExpOffset;

  private get refreshTokenParsed() {
    return this.keycloak.refreshTokenParsed;
  }

  private get refreshTokenParsedExp() {
    return this.refreshTokenParsed?.exp;
  }

  private get refreshTokenParsedIat() {
    return this.refreshTokenParsed?.iat;
  }

  private get refreshTokenExpOffset() {
    return this.refreshTokenParsedExp - this.refreshTokenParsedIat;
  }

  constructor() {
    effect(() => {
      const event = this.keycloakEvent();
      if (!event) return;

      switch (event.type) {
        case KeycloakEventType.AuthRefreshSuccess:
          this.countDownTime$.next(this.calculateCountdownTime());

          if (this.refreshTokenExpOffset < this.initialRefreshTokenExpOffset) {
            this.timeExtensionAllowed$.next(false);
          }
          break;
        case KeycloakEventType.AuthLogout:
          this.idleLogout();
          break;
      }
    });

    this.countDownTime$
      .pipe(
        switchMap((countDownTime) => {
          return countDownTime > 0 ? timer(countDownTime).pipe(tap(() => this.isVisible$.next(true))) : EMPTY;
        }),
      )
      .subscribe();

    this.isVisible$
      .pipe(
        switchMap((isVisible) =>
          isVisible
            ? timer(this.timeOffsetSeconds * 1000).pipe(
                tap(() => {
                  this.isVisible$.next(false);
                  this.idleLogout();
                }),
              )
            : EMPTY,
        ),
      )
      .subscribe();
  }

  extendSession() {
    this.keycloak.updateToken(-1).then(() => this.isVisible$.next(false));
  }

  signOut() {
    this.isVisible$.next(false);
    this.authService.logout();
  }

  private idleLogout() {
    const idleTime = this.refreshTokenParsedExp - this.refreshTokenParsedIat;
    this.authService.logout('timed-out?idle=' + idleTime);
  }

  private calculateCountdownTime(): number {
    return this.refreshTokenParsedExp * 1000 - Date.now() - this.timeOffsetSeconds * 1000;
  }
}
