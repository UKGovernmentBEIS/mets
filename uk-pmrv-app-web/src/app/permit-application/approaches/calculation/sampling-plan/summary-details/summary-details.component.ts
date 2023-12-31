import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { iif, map } from 'rxjs';

import { CalculationOfCO2MonitoringApproach } from 'pmrv-api';

import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';

@Component({
  selector: 'app-calculation-plan-summary-details',
  templateUrl: './summary-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryDetailsComponent {
  @Input() showOriginal = false;
  @Input() changePerStage: boolean;
  @Input() hasBottomBorder = true;

  private samplingPlan$ = iif(
    () => this.showOriginal,
    this.store.getOriginalTask('monitoringApproaches'),
    this.store.getTask('monitoringApproaches'),
  ).pipe(map((task) => (task?.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach).samplingPlan));

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  planAttachments$ = this.samplingPlan$.pipe(
    map((plan) =>
      plan.exist && plan.details.procedurePlan?.procedurePlanIds ? plan.details.procedurePlan.procedurePlanIds : [],
    ),
  );

  changeClick(wizardStep?: string): void {
    this.router.navigate(['../' + wizardStep], { relativeTo: this.route, state: { changing: true } });
  }
}
