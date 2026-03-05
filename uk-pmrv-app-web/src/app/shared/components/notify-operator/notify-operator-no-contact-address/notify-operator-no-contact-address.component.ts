import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-notify-operator-no-contact-address',
  template: `
    <div class="govuk-grid-column-two-thirds">
      <app-page-heading>You must add a contact address for the aviation operator</app-page-heading>
      <p class="govuk-body">
        You must
        <a [routerLink]="['/aviation/accounts', accountId]">add a contact address</a>
        for the aviation operator before you can issue the notice.
      </p>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotifyOperatorNoContactAddressComponent {
  @Input() accountId: number;
}
