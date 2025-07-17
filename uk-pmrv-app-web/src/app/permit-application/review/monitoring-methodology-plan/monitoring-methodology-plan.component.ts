import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import {
  isFallbackApproach,
  isProductBenchmark,
} from '@permit-application/mmp-sub-installations/mmp-sub-installations-status';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { statusMap } from '@shared/task-list/task-item/status.map';

import { GovukTableColumn } from 'govuk-components';

import { SubInstallation } from 'pmrv-api';

@Component({
  selector: 'app-monitoring-methodology-plan',
  templateUrl: './monitoring-methodology-plan.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringMethodologyPlanComponent {
  showDiff$ = this.store.showDiff$;
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  groupKey$ = this.route.data.pipe(map((x) => x?.groupKey));
  showMMPTasks =
    this.store.getState()?.features?.['digitized-mmp'] &&
    this.store.getState()?.permit?.monitoringMethodologyPlans?.exist;

  subInstallations$ = this.store.findTask<SubInstallation[]>(
    'monitoringMethodologyPlans.digitizedPlan.subInstallations',
  );
  originalSubInstallations$ = this.store.findOriginalTask<SubInstallation[]>(
    'monitoringMethodologyPlans.digitizedPlan.subInstallations',
  );

  productBenchmarks$ = this.subInstallations$.pipe(
    map((subInstallations) =>
      subInstallations?.filter((subInstallation) => isProductBenchmark(subInstallation.subInstallationType)),
    ),
  );
  originalProductBenchmarks$ = this.originalSubInstallations$.pipe(
    map((subInstallations) =>
      subInstallations?.filter((subInstallation) => isProductBenchmark(subInstallation.subInstallationType)),
    ),
  );

  fallbackApproaches$ = this.subInstallations$.pipe(
    map((subInstallations) =>
      subInstallations?.filter((subInstallation) => isFallbackApproach(subInstallation.subInstallationType)),
    ),
  );

  originalFallbackApproaches$ = this.originalSubInstallations$.pipe(
    map((subInstallations) =>
      subInstallations?.filter((subInstallation) => isFallbackApproach(subInstallation.subInstallationType)),
    ),
  );

  columns: GovukTableColumn<any>[] = [
    { field: 'type', header: 'Sub-installation type', widthClass: 'govuk-!-width-one-third custom-width' },
    { field: 'carbon', header: 'Carbon leakage', widthClass: 'govuk-!-width--one-third' },
    { field: 'status', header: '', widthClass: 'govuk-!-width-one-third' },
  ];
  statusMap = statusMap;

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly store: PermitApplicationStore<PermitApplicationState>,
  ) {}
}
