import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="Extension request {{ decision === 'ACCEPTED' ? 'accepted' : 'rejected' }}"></govuk-panel>
      </div>
    </div>
    <a govukLink [routerLink]="isAviation + '/dashboard'">Return to dashboard</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent {
  decision = this.router.getCurrentNavigation().extras?.state?.decision;
  isAviation = this.router.url.includes('/aviation/') ? '/aviation' : '';

  constructor(private readonly router: Router) {}
}
