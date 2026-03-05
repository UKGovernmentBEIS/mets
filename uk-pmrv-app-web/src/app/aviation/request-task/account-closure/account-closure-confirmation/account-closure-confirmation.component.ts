import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterModule } from '@angular/router';

import { BaseSuccessComponent } from '@shared/base-success/base-success.component';
import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-account-closure-confirmation',
  standalone: true,
  imports: [SharedModule, RouterModule],
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="Account closed"></govuk-panel>
        <p class="govuk-body">The account has been permanently closed.</p>
      </div>
    </div>
    <a govukLink routerLink="/aviation/dashboard">Return to your tasks</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccountClosureConfirmationComponent extends BaseSuccessComponent {}
