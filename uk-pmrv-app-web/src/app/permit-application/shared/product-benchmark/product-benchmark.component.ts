import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map } from 'rxjs';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { reviewRequestTaskTypes } from '../utils/permit';

export abstract class ProductBenchmarkComponent {
  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly router: Router,
    readonly route: ActivatedRoute,
  ) {}

  productBenchmarkType$ = combineLatest([this.store.getTask('monitoringMethodologyPlans'), this.route.paramMap]).pipe(
    map(([monitoringMethodologyPlans, paramMap]) => {
      const subInstallationNo = paramMap.get('subInstallationNo') || paramMap.get('subInstallationNo') === '0';

      return subInstallationNo
        ? monitoringMethodologyPlans?.digitizedPlan?.subInstallations?.find(
            (x) => x.subInstallationNo == subInstallationNo,
          )?.subInstallationType
        : monitoringMethodologyPlans?.digitizedPlan?.subInstallations?.[
            monitoringMethodologyPlans?.digitizedPlan?.subInstallations.length - 1
          ]?.subInstallationType;
    }),
  );

  breadcrumb$ = this.store.pipe(
    map((response) =>
      reviewRequestTaskTypes.includes(response.requestTaskType)
        ? [
            {
              text: 'Monitoring methodology',
              link: ['monitoring-methodology-plan'],
            },
          ]
        : [
            {
              text: 'Sub-installations',
              link: ['mmp-sub-installations'],
            },
          ],
    ),
  );
}
