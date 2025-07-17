import { ChangeDetectionStrategy, Component } from '@angular/core';

import { BaseSuccessComponent } from '@shared/base-success/base-success.component';

import { DestroySubject } from '../../../core/services/destroy-subject.service';
import { PermitSurrenderStore } from '../../store/permit-surrender.store';

@Component({
  selector: 'app-summary',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-full">
        <govuk-notification-banner *ngIf="notification" type="success">
          <h1 class="govuk-notification-banner__heading">Details updated</h1>
        </govuk-notification-banner>
      </div>
    </div>

    <div class="govuk-grid-row">
      <div class="govuk-grid-column-full">
        <app-page-heading>Surrender your permit</app-page-heading>
        <app-permit-surrender-summary-details></app-permit-surrender-summary-details>
        <a govukLink routerLink="../..">Return to: Surrender your permit</a>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class SummaryComponent extends BaseSuccessComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  constructor(readonly store: PermitSurrenderStore) {
    super();
  }
}
