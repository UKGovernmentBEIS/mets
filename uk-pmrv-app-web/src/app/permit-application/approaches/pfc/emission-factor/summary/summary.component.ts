import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';

@Component({
  selector: 'app-summary',
  template: `
    <app-permit-task
      [notification]="notification"
      [breadcrumb]="[{ text: 'CALCULATION_PFC' | monitoringApproachDescription, link: ['pfc'] }]">
      <app-page-heading [caption]="'CALCULATION_PFC' | monitoringApproachDescription">
        Tier 2 - Emission factor
      </app-page-heading>
      <app-emission-factor-summary-details
        [emissionFactor]="('CALCULATION_PFC' | monitoringApproachTask | async).tier2EmissionFactor"
        [changePerStage]="true"
        cssClass="summary-list--edge-border"></app-emission-factor-summary-details>
      <app-approach-return-link
        [parentTitle]="'CALCULATION_PFC' | monitoringApproachDescription"
        reviewGroupUrl="pfc"></app-approach-return-link>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
  ) {}
}
