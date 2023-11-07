import { ChangeDetectionStrategy, Component, OnInit, ViewEncapsulation } from '@angular/core';

import { Observable, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectIsLoggedIn, selectUserProfile } from '@core/store/auth';
import { KeycloakProfile } from 'keycloak-js';

/* eslint-disable @angular-eslint/use-component-view-encapsulation */
@Component({
  selector: 'app-phase-bar',
  template: `
    <govuk-phase-banner phase="beta">
      <!-- FIXME: apply correct href when discussed with analysis -->
      <ng-container *ngIf="isLoggedIn$ | async">
        This is a new service – your <a govukLink routerLink="feedback">feedback</a> will help us to improve it.
      </ng-container>

      <span *ngIf="userProfile$ | async as user" class="logged-in-user">
        You are logged in as: <span class="govuk-!-font-weight-bold">{{ user.firstName }} {{ user.lastName }}</span>
      </span>
    </govuk-phase-banner>
  `,
  styles: [
    `
      .govuk-phase-banner__text {
        width: 100%;
      }

      .logged-in-user {
        float: right;
      }
    `,
  ],
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class PhaseBarComponent implements OnInit {
  isLoggedIn$: Observable<boolean>;
  userProfile$: Observable<KeycloakProfile>;

  constructor(private readonly authStore: AuthStore, private readonly destroy$: DestroySubject) {}

  ngOnInit(): void {
    this.isLoggedIn$ = this.authStore.pipe(selectIsLoggedIn, takeUntil(this.destroy$));

    this.userProfile$ = this.authStore.pipe(selectUserProfile, takeUntil(this.destroy$));
  }
}
