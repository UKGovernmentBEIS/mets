import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';

import { map, shareReplay } from 'rxjs';

import {
  isFallbackApproach,
  isProductBenchmark,
} from '@permit-application/mmp-sub-installations/mmp-sub-installations-status';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { statusMap } from '@shared/task-list/task-item/status.map';

import { GovukTableColumn } from 'govuk-components';

import { SubInstallation, SubInstallationTypeDetails, SubInstallationTypesService } from 'pmrv-api';

@Component({
  selector: 'app-monitoring-methodology-plan',
  standalone: false,
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
  isTask = toSignal(this.store.pipe(map((state) => state.isRequestTask)));
  creationDate = toSignal(this.store.pipe(map((state) => state.requestActionCreationDate)));

  getSubInstallationTypesDetails$ = this.subInstallationTypesService
    .getSubInstallationTypesDetails()
    .pipe(shareReplay({ bufferSize: 1, refCount: true }));

  readonly subInstallationsSignal = toSignal(
    this.store.findTask<SubInstallation[]>('monitoringMethodologyPlans.digitizedPlan.subInstallations'),
    { initialValue: [] },
  );

  readonly originalSubInstallationsSignal = toSignal(
    this.store.findOriginalTask<SubInstallation[]>('monitoringMethodologyPlans.digitizedPlan.subInstallations'),
    { initialValue: [] },
  );

  readonly subInstallations = computed(() => {
    const subInstallationDetails = this.subInstallationTypesDetails();

    return this.subInstallationsSignal()?.map((subInstallation) => ({
      ...subInstallation,
      coveredByUKCBAM: subInstallationDetails.find(
        (item) => item.subInstallationType === subInstallation.subInstallationType,
      )?.coveredByUKCBAM,
    }));
  });

  readonly originalSubInstallations = computed(() => {
    const subInstallationDetails = this.subInstallationTypesDetails();

    return this.originalSubInstallationsSignal()?.map((subInstallation) => ({
      ...subInstallation,
      coveredByUKCBAM: subInstallationDetails.find(
        (item) => item.subInstallationType === subInstallation.subInstallationType,
      )?.coveredByUKCBAM,
    }));
  });

  readonly productBenchmarks = computed(() =>
    this.subInstallations()?.filter((subInstallation) => isProductBenchmark(subInstallation.subInstallationType)),
  );

  readonly originalProductBenchmarks = computed(() =>
    this.originalSubInstallations()?.filter((subInstallation) =>
      isProductBenchmark(subInstallation.subInstallationType),
    ),
  );

  readonly fallbackApproaches = computed(() =>
    this.subInstallations()?.filter((subInstallation) => isFallbackApproach(subInstallation.subInstallationType)),
  );

  readonly originalFallbackApproaches = computed(() =>
    this.originalSubInstallations()?.filter((subInstallation) =>
      isFallbackApproach(subInstallation.subInstallationType),
    ),
  );
  readonly subInstallationTypesDetails = toSignal(this.getSubInstallationTypesDetails$, {
    initialValue: [] as SubInstallationTypeDetails[],
  });

  cbamEnabled: Signal<boolean> = computed(() => {
    const subDetails = this.subInstallationTypesDetails();
    const isTask = this.isTask();
    const creationDate = this.creationDate();

    const cbamToggle = subDetails.some(
      (item) =>
        (item.subInstallationType == 'HYDROGEN_CBAM' && item.valid) ||
        (item.subInstallationType == 'HYDROGEN_NON_CBAM' && item.valid) ||
        (item.subInstallationType == 'IRON_CASTING_CBAM' && item.valid) ||
        (item.subInstallationType == 'IRON_CASTING_NON_CBAM' && item.valid) ||
        (item.subInstallationType == 'FUEL_BENCHMARK_CL_CBAM' && item.valid) ||
        (item.subInstallationType == 'FUEL_BENCHMARK_CL_NON_CBAM' && item.valid) ||
        (item.subInstallationType == 'HEAT_BENCHMARK_CL_CBAM' && item.valid) ||
        (item.subInstallationType == 'HEAT_BENCHMARK_CL_NON_CBAM' && item.valid) ||
        (item.subInstallationType == 'PROCESS_EMISSIONS_CL_CBAM' && item.valid) ||
        (item.subInstallationType == 'PROCESS_EMISSIONS_CL_NON_CBAM' && item.valid),
    );

    return isTask ? !!cbamToggle : new Date(creationDate) > new Date('2027-01-01T00:00:00.000Z');
  });

  columns: Signal<GovukTableColumn<any>[]> = computed(() => {
    const cbam = this.cbamEnabled();
    return cbam
      ? [
          { field: 'type', header: 'Sub-installation type', widthClass: 'govuk-!-width-one-quarter' },
          { field: 'carbon', header: 'Carbon leakage', widthClass: 'govuk-!-width-one-quarter' },
          { field: 'coveredByUKCBAM', header: 'Covered by UK CBAM', widthClass: 'govuk-!-width-one-quarter' },
          { field: 'status', header: '' },
        ]
      : [
          { field: 'type', header: 'Sub-installation type', widthClass: 'govuk-!-width-one-quarter' },
          { field: 'carbon', header: 'Carbon leakage', widthClass: 'govuk-!-width-one-quarter' },
          { field: 'status', header: '', widthClass: 'govuk-!-width-one-quarter' },
        ];
  });

  statusMap = statusMap;

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly subInstallationTypesService: SubInstallationTypesService,
  ) {}
}
