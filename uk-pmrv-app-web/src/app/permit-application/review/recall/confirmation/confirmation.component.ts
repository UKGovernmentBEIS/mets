import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map } from 'rxjs';

import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { PermitApplicationState } from '../../../store/permit-application.state';

@Component({
  selector: 'app-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="The {{ permitType$ | async | permitRequestType }} has been recalled"></govuk-panel>
        <p class="govuk-body" *ngIf="isVariation$ | async">
          The request for amends to the permit variation application has been recalled. The operator will be notified.
        </p>
        <a govukLink routerLink="/dashboard">Return to dashboard</a>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent {
  readonly permitType$ = this.store.pipe(map((state) => state.requestType));
  isVariation$ = this.permitType$.pipe(map((requestType) => requestType === 'PERMIT_VARIATION'));

  constructor(readonly store: PermitApplicationStore<PermitApplicationState>) {}
}
