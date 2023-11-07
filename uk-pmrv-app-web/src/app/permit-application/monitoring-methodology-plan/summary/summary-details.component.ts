import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-monitoring-methodology-summary-details',
  templateUrl: './summary-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringMethodologyPlanSummaryDetailsComponent {
  @Input() showOriginal = false;
  @Input() isPreview: boolean;
  @Input() hasBottomBorder = true;

  readonly files$ = this.store.pipe(
    map((state) => {
      return this.showOriginal
        ? (state as any).originalPermitContainer.permit.monitoringMethodologyPlans?.plans ?? []
        : state.permit.monitoringMethodologyPlans?.plans ?? [];
    }),
  );

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  changeClick(wizardStep?: string): void {
    this.router.navigate(['../' + wizardStep], { relativeTo: this.route, state: { changing: true } });
  }
}
