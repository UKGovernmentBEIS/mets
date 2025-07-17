import { Injectable } from '@angular/core';

import { BehaviorSubject, EMPTY, filter, map, switchMap, tap, timer } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { KeycloakEventType, KeycloakService } from 'keycloak-angular';

import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class TimeoutBannerService {
  private get refreshTokenParsed() {
    return this.keycloak.getKeycloakInstance()?.refreshTokenParsed;
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

  timeOffsetSeconds = environment.timeoutBanner.timeOffsetSeconds;

  timeExtensionAllowed$ = new BehaviorSubject<boolean>(true);
  isVisible$ = new BehaviorSubject<boolean>(false);

  countDownTime$ = new BehaviorSubject<number>(this.calculateCountdownTime());
  private initialRefreshTokenExpOffset = this.refreshTokenExpOffset;

  constructor(
    private readonly keycloak: KeycloakService,
    private readonly authService: AuthService,
  ) {
    this.keycloak.keycloakEvents$
      .pipe(
        map((event) => event?.type),
        filter((eventType) =>
          [KeycloakEventType.OnAuthRefreshSuccess, KeycloakEventType.OnAuthLogout].includes(eventType),
        ),
      )
      .subscribe((eventType) => {
        switch (eventType) {
          case KeycloakEventType.OnAuthRefreshSuccess:
            this.countDownTime$.next(this.calculateCountdownTime());

            if (this.refreshTokenExpOffset < this.initialRefreshTokenExpOffset) {
              this.timeExtensionAllowed$.next(false);
            }
            break;
          case KeycloakEventType.OnAuthLogout:
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
    if (this.keycloak.getKeycloakInstance()) {
      this.keycloak.updateToken(-1).then(() => this.isVisible$.next(false));
    }
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
