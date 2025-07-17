import { ChangeDetectionStrategy, Component } from '@angular/core';

import { AuthService } from '@core/services/auth.service';

import { UserRegistrationStore } from '../store/user-registration.store';

@Component({
  selector: 'app-email-confirmed',
  template: `
    <govuk-panel>Email address confirmed</govuk-panel>

    <ng-container *ngIf="(emailVerificationStatus$ | async) === 'NOT_REGISTERED'">
      <p class="govuk-body">You can continue to create a UK ETS reporting sign in.</p>
      <a routerLink="../user/contact-details" govukButton>Continue</a>
    </ng-container>
    <ng-container *ngIf="(emailVerificationStatus$ | async) === 'REGISTERED'">
      <p class="govuk-body">A sign in already exists for {{ email$ | async }}.</p>
      <a class="govuk-button govuk-button--start" routerLink="/" draggable="false">
        Sign in
        <img class="govuk-button__start-icon" ngSrc="./assets/images/start-button-arrow.svg" alt="" />
      </a>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmailConfirmedComponent {
  emailVerificationStatus$ = this.store.select('emailVerificationStatus');
  email$ = this.store.select('email');

  constructor(
    private readonly store: UserRegistrationStore,
    public readonly authService: AuthService,
  ) {}
}
