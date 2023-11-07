import { ChangeDetectionStrategy, Component } from '@angular/core';

import { BaseSuccessComponent } from '@shared/base-success/base-success.component';

@Component({
  selector: 'app-peer-review-decision-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="Returned to regulator"></govuk-panel>
        <p class="govuk-body">The regulator will continue their review.</p>
        <a govukLink routerLink="{{ isAviation }}/dashboard"> Return to dashboard </a>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent extends BaseSuccessComponent {
  isAviation = this.router.url.includes('/aviation/') ? '/aviation' : '';

  constructor() {
    super();
  }
}
