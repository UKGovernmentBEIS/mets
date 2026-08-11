import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, map, shareReplay } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { SubInstallationTypePipe } from '@shared/pipes/sub-installation-type.pipe';
import { statusMap } from '@shared/task-list/task-item/status.map';

import { GovukTableColumn } from 'govuk-components';

import { RequestInfoDTO, SubInstallation, SubInstallationTypesService } from 'pmrv-api';

import { isFallbackApproach, isProductBenchmark } from './mmp-sub-installations-status';

@Component({
  selector: 'app-mmp-sub-installations',
  standalone: false,
  templateUrl: './mmp-sub-installations.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MmpSubInstallationsComponent implements PendingRequest {
  permitTask$ = this.route.data.pipe(map((x) => x?.permitTask));
  competentAuthority: RequestInfoDTO['competentAuthority'] = this.store.getState().competentAuthority;
  isEditable$ = this.store.pipe(map((state) => state.isEditable));
  isTask = toSignal(this.store.pipe(map((state) => state.isRequestTask)));
  creationDate = toSignal(this.store.pipe(map((state) => state.requestActionCreationDate)));

  getSubInstallationTypesDetails$ = this.subInstallationTypesService
    .getSubInstallationTypesDetails()
    .pipe(shareReplay({ bufferSize: 1, refCount: true }));

  productBenchmarks$ = combineLatest([
    this.store.getTask('monitoringMethodologyPlans'),
    this.getSubInstallationTypesDetails$,
  ]).pipe(
    map(([monitoringMethodologyPlans, subInstallationDetails]) => {
      const data = monitoringMethodologyPlans?.digitizedPlan?.subInstallations
        ?.filter((subInstallation) => {
          return isProductBenchmark(subInstallation.subInstallationType);
        })
        .map((subInstallation) => {
          const res = {
            ...subInstallation,
            coveredByUKCBAM: subInstallationDetails.find(
              (item) => item['subInstallationType'] === subInstallation.subInstallationType,
            )['coveredByUKCBAM'],
          };
          return res;
        });

      return data;
    }),
  );

  fallbackApproaches$ = combineLatest([
    this.store.getTask('monitoringMethodologyPlans'),
    this.getSubInstallationTypesDetails$,
  ]).pipe(
    map(([monitoringMethodologyPlans, subInstallationDetails]) => {
      return monitoringMethodologyPlans?.digitizedPlan?.subInstallations
        ?.filter((subInstallation) => {
          return isFallbackApproach(subInstallation.subInstallationType);
        })
        .map((subInstallation) => {
          const res = {
            ...subInstallation,
            coveredByUKCBAM: subInstallationDetails.find(
              (item) => item['subInstallationType'] === subInstallation.subInstallationType,
            )['coveredByUKCBAM'],
          };
          return res;
        });
    }),
  );

  readonly subInstallationTypesDetails = toSignal(this.getSubInstallationTypesDetails$);

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
          { field: 'action', header: '', widthClass: 'govuk-input--width-10' },
          { field: 'status', header: '' },
        ]
      : [
          { field: 'type', header: 'Sub-installation type', widthClass: 'govuk-!-width-one-quarter' },
          { field: 'carbon', header: 'Carbon leakage', widthClass: 'govuk-!-width-one-quarter' },
          { field: 'action', header: '', widthClass: 'govuk-!-width-one-quarter' },
          { field: 'status', header: '', widthClass: 'govuk-!-width-one-quarter' },
        ];
  });

  statusMap = statusMap;

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
    private readonly subInstallationTypesService: SubInstallationTypesService,
  ) {}

  hiddenTextOnRemove(type: SubInstallation['subInstallationType']): string {
    const subInstallationPipe = new SubInstallationTypePipe();
    const typeFormatted = subInstallationPipe.transform(type);
    const lowerCaseFirstLetterType = typeFormatted.charAt(0).toLowerCase() + typeFormatted.slice(1);
    return `${lowerCaseFirstLetterType} sub-installation`;
  }
}
