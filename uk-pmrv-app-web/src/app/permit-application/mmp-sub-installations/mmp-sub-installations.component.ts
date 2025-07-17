import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { statusMap } from '@shared/task-list/task-item/status.map';

import { GovukTableColumn } from 'govuk-components';

import { RequestInfoDTO } from 'pmrv-api';

import { isFallbackApproach, isProductBenchmark } from './mmp-sub-installations-status';

@Component({
  selector: 'app-mmp-sub-installations',
  templateUrl: './mmp-sub-installations.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MmpSubInstallationsComponent implements PendingRequest {
  permitTask$ = this.route.data.pipe(map((x) => x?.permitTask));
  competentAuthority: RequestInfoDTO['competentAuthority'] = this.store.getState().competentAuthority;
  isEditable$ = this.store.pipe(map((state) => state.isEditable));
  productBenchmarks$ = this.store
    .getTask('monitoringMethodologyPlans')
    .pipe(
      map((monitoringMethodologyPlans) =>
        monitoringMethodologyPlans?.digitizedPlan?.subInstallations?.filter((subInstallation) =>
          isProductBenchmark(subInstallation.subInstallationType),
        ),
      ),
    );
  fallbackApproaches$ = this.store
    .getTask('monitoringMethodologyPlans')
    .pipe(
      map((monitoringMethodologyPlans) =>
        monitoringMethodologyPlans?.digitizedPlan?.subInstallations?.filter((subInstallation) =>
          isFallbackApproach(subInstallation.subInstallationType),
        ),
      ),
    );

  columns: GovukTableColumn<any>[] = [
    { field: 'type', header: 'Sub-installation type', widthClass: 'govuk-!-width-one-quarter' },
    { field: 'carbon', header: 'Carbon leakage', widthClass: 'govuk-!-width--one-quarter' },
    { field: 'action', header: '', widthClass: 'govuk-!-width-one-quarter' },
    { field: 'status', header: '', widthClass: 'govuk-!-width-one-quarter' },
  ];

  statusMap = statusMap;

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
  ) {}
}
