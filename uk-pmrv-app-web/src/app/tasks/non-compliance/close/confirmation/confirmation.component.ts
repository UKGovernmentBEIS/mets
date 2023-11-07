import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map } from 'rxjs';

import { NonComplianceService } from '@tasks/non-compliance/core/non-compliance.service';

@Component({
  selector: 'app-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="Task closed successfully">
          <br /><br /><strong>Your reference code is:</strong><br />{{ requestId$ | async }}
        </govuk-panel>
      </div>
    </div>
    <p class="govuk-body">You have marked this task as 'closed'.</p>
    <h3 class="govuk-heading-s">What happens next</h3>
    <p class="govuk-body">You can start a new task if required.</p>
    <a govukLink [routerLink]="isAviation + '/dashboard'"> Return to dashboard </a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent {
  constructor(private readonly nonComplianceService: NonComplianceService, private readonly router: Router) {}

  isAviation = this.router.url.includes('/aviation/') ? '/aviation' : '';

  readonly requestId$ = this.nonComplianceService.requestTaskItem$.pipe(map((data) => data.requestInfo.id));
}
