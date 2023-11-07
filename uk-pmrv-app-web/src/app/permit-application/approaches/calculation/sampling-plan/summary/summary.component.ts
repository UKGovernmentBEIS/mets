import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';

@Component({
  selector: 'app-summary',
  template: `
    <app-permit-task
      [notification]="notification"
      [breadcrumb]="[{ text: 'CALCULATION_CO2' | monitoringApproachDescription, link: ['calculation'] }]"
    >
      <app-page-heading [caption]="'CALCULATION_CO2' | monitoringApproachDescription">Sampling plan</app-page-heading>
      <app-calculation-plan-summary-details [changePerStage]="true"></app-calculation-plan-summary-details>
      <app-approach-return-link
        [parentTitle]="'CALCULATION_CO2' | monitoringApproachDescription"
        reviewGroupUrl="calculation"
      ></app-approach-return-link>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  constructor(private readonly router: Router, readonly store: PermitApplicationStore<PermitApplicationState>) {}
}
