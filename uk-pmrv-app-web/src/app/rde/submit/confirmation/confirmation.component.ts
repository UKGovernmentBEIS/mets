import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="Request sent"></govuk-panel>

        <a govukLink [routerLink]="isAviation + '/dashboard'">Return to dashboard</a>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent {
  isAviation = this.router.url.includes('/aviation/') ? '/aviation' : '';

  constructor(private readonly router: Router) {}
}
