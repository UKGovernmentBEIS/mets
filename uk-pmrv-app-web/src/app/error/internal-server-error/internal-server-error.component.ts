import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-internal-server-error',
  template: `
    <app-page-heading size="xl">Sorry, there is a problem with the service</app-page-heading>

    <p class="govuk-body">Try again later.</p>

    <p class="govuk-body">
      <a href="mailto:METS@energysecurity.gov.uk" govukLink class="govuk-!-font-weight-bold"
        >Contact the UK ETS reporting helpdesk</a
      >
      if you have any questions.
    </p>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InternalServerErrorComponent {}
