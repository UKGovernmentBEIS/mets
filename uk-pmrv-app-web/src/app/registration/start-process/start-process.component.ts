import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-start-process',
  standalone: false,
  template: `
    <app-page-heading>Create a sign in for the installations part of Manage your UK ETS reporting</app-page-heading>
    <div class="govuk-body">
      <p>You can create a sign in if you are an installation operator.</p>
      <p>You will need:</p>
      <ul>
        <li>a work email address that only you use</li>
        <li>a mobile phone, tablet or browser to set up two-factor authentication</li>
      </ul>
      <h2 class="govuk-heading-m">If you need an aviation or maritime sign in</h2>
      <p>
        <a class="govuk-link" href="/contact-us" target="_blank" rel="noreferrer noopener">Contact your regulator</a>
        to ask for an invitation.
      </p>
      <a govukButton routerLink="email">Create an installations sign in</a>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StartProcessComponent {}
