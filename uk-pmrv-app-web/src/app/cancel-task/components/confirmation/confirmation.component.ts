import { ChangeDetectionStrategy, Component } from '@angular/core';

import { BaseSuccessComponent } from '@shared/base-success/base-success.component';

@Component({
  selector: 'app-cancel-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="Task cancelled"></govuk-panel>
      </div>
    </div>
    <a govukLink routerLink="/dashboard">Return to dashboard</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent extends BaseSuccessComponent {}
