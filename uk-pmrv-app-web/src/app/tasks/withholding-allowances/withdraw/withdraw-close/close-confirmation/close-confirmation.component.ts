import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map } from 'rxjs';

import { WithholdingAllowancesService } from '../../../core/withholding-allowances.service';

@Component({
  selector: 'app-close-confirmation',

  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="Task closed successfully">
          <br />
          <br />
          <strong>Your reference code is:</strong>
          <br />
          {{ requestId$ | async }}
        </govuk-panel>
      </div>
    </div>
    <p class="govuk-body">You have marked this task as 'closed'.</p>
    <h3 class="govuk-heading-s">What happens next</h3>
    <p class="govuk-body">You can start a new task if required.</p>
    <a govukLink routerLink="/dashboard">Return to dashboard</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CloseConfirmationComponent {
  constructor(private readonly withholdingAllowancesService: WithholdingAllowancesService) {}

  readonly requestId$ = this.withholdingAllowancesService.requestTaskItem$.pipe(map((data) => data.requestInfo.id));
}
