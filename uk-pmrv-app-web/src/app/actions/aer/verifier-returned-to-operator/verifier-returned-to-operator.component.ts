import { ChangeDetectionStrategy, Component, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { Router, RouterLinkWithHref } from '@angular/router';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { SharedModule } from '@shared/shared.module';

import { AerVerificationReturnedToOperatorRequestActionPayload } from 'pmrv-api';

import { AerService } from '../core/aer.service';

@Component({
  selector: 'app-verifier-returned-to-operator',
  standalone: true,
  imports: [ActionSharedModule, SharedModule, RouterLinkWithHref],
  template: `
    <app-base-action-container-component
      header="Verifier returned to operator for changes"
      [customContentTemplate]="customContentTemplate"
      [expectedActionType]="['AER_VERIFICATION_RETURNED_TO_OPERATOR']"></app-base-action-container-component>
    <a govukLink routerLink="../../../../">
      Return to: {{ this.router.url.includes('workflows') ? 'Emissions report' : 'Dashboard' }}
    </a>
    <ng-template #customContentTemplate>
      <h2 app-summary-header class="govuk-heading-m">Verifier comments</h2>

      <dl govuk-summary-list appGroupedSummaryList class="govuk-!-margin-bottom-6" *ngIf="payload() as payload">
        <div govukSummaryListRow>
          <dt govukSummaryListRowKey>Changes required by the operator</dt>
          <dd govukSummaryListRowValue class="pre-wrap">{{ payload.changesRequired }}</dd>
        </div>
      </dl>
    </ng-template>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerifierReturnedToOperatorActionComponent {
  payload = toSignal(this.aerService.getPayload()) as Signal<AerVerificationReturnedToOperatorRequestActionPayload>;

  constructor(
    private readonly aerService: AerService,
    readonly router: Router,
  ) {}
}
