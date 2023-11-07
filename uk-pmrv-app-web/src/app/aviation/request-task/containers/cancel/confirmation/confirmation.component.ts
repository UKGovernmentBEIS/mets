import { ChangeDetectionStrategy, Component } from '@angular/core';

import { BaseSuccessComponent } from '@shared/base-success/base-success.component';

@Component({
  selector: 'app-cancel-confirmation-aviation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="This task has been cancelled"></govuk-panel>
      </div>
    </div>
    <p class="govuk-body">It has been removed from your task dashboard.</p>
    <a govukLink routerLink="/aviation/dashboard"> Return to dashboard </a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent extends BaseSuccessComponent {}
