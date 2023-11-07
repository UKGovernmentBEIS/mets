import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-start-process',
  template: `
    <app-page-heading>Create a UK Emissions Trading Scheme reporting sign in</app-page-heading>
    <div class="govuk-body">
      <h2 class="govuk-heading-m">Before you start</h2>
      <p>To create a sign in, you'll need:</p>
      <ul>
        <li>a work email address that is not shared with anyone else</li>
        <li>a mobile phone, tablet or browser to set up two-factor authentication</li>
      </ul>
      <a govukButton routerLink="email"> Continue </a>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StartProcessComponent {}
