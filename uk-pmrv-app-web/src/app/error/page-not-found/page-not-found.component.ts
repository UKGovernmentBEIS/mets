import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-page-not-found',
  template: `
    <app-page-heading size="xl">Page Not Found</app-page-heading>
    <p class="govuk-body">If you typed the web address, check it is correct.</p>
    <p class="govuk-body">If you pasted the web address, check you copied the entire address.</p>
    <p class="govuk-body">
      If the web address is correct,
      <a govukLink [routerLink]="['/contact-us']">contact your regulator</a>
      for help.
    </p>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PageNotFoundComponent {}
