import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { BaseSuccessComponent } from '@shared/base-success/base-success.component';

@Component({
  selector: 'app-summary-container',
  template: `<div class="govuk-grid-row">
    <div class="govuk-grid-column-full">
      <govuk-notification-banner *ngIf="notification" type="success">
        <h1 class="govuk-notification-banner__heading">Details updated</h1>
      </govuk-notification-banner>
      <app-page-heading>{{ (route.data | async)?.pageTitle }}</app-page-heading>
      <app-withdraw-summary></app-withdraw-summary>
      <a govukLink routerLink="../">Return to: Withdraw permit revocation</a>
    </div>
  </div>`,

  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WithdrawSummaryContainerComponent extends BaseSuccessComponent {
  notification: any;
  readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));

  constructor(readonly route: ActivatedRoute) {
    super();
    this.notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  }
}
