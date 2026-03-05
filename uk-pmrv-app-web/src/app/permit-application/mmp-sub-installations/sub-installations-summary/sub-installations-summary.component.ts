import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { ProductBenchmarkComponent } from '@permit-application/shared/product-benchmark/product-benchmark.component';
import { reviewRequestTaskTypes } from '@permit-application/shared/utils/permit';

import { SubInstallation } from 'pmrv-api';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { isProductBenchmark } from '../mmp-sub-installations-status';

@Component({
  selector: 'app-sub-installations-summary',
  templateUrl: './sub-installations-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubInstallationsSummaryComponent extends ProductBenchmarkComponent implements PendingRequest {
  permitTask$ = this.route.data.pipe(map((x) => x?.permitTask));
  isReview$ = this.store.pipe(map((x) => reviewRequestTaskTypes.includes(x?.requestTaskType)));

  data: SubInstallation;

  subInstallation$ = combineLatest([this.store, this.route.paramMap]).pipe(
    map(([state, paramMap]) => {
      const subInstallationNo = paramMap.get('subInstallationNo') || paramMap.get('subInstallationNo') === '0';
      const digitizedPlan = state.permit?.monitoringMethodologyPlans?.digitizedPlan;
      const subInstallations = digitizedPlan && digitizedPlan?.subInstallations;
      return subInstallations.find((x) => x.subInstallationNo == subInstallationNo);
    }),
  );

  hideSubmit$ = combineLatest([this.store.isEditable$, this.store, this.route.paramMap]).pipe(
    map(([isEditable, state, paramMap]) => {
      const digitizedPlan = state.permit?.monitoringMethodologyPlans?.digitizedPlan;
      const subInstallations = digitizedPlan && digitizedPlan?.subInstallations;

      const productBenchmarkIndex = subInstallations
        ?.filter((subInstallation) => isProductBenchmark(subInstallation.subInstallationType))
        .findIndex((x) => x.subInstallationNo == paramMap.get('subInstallationNo'));
      return (
        !isEditable ||
        this.store.getState().permitSectionsCompleted?.['MMP_SUB_INSTALLATION_Product_Benchmark']?.[
          productBenchmarkIndex
        ]
      );
    }),
  );

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly router: Router,

    readonly pendingRequest: PendingRequestService,
    readonly route: ActivatedRoute,
  ) {
    super(store, router, route);
  }

  confirm(): void {
    combineLatest([this.store, this.route.paramMap, this.route.data])
      .pipe(
        first(),
        switchMap(([state, paramMap, data]) => {
          const digitizedPlan = state.permit?.monitoringMethodologyPlans?.digitizedPlan;
          const subInstallations = digitizedPlan && digitizedPlan?.subInstallations;
          const productBenchmarkIndex = subInstallations
            ?.filter((subInstallation) => isProductBenchmark(subInstallation.subInstallationType))
            .findIndex((x) => x.subInstallationNo == paramMap.get('subInstallationNo'));

          return this.store.postStatus(
            'MMP_SUB_INSTALLATION_Product_Benchmark',
            productBenchmarkIndex !== -1 &&
              state.permitSectionsCompleted?.['MMP_SUB_INSTALLATION_Product_Benchmark']?.length
              ? state.permitSectionsCompleted['MMP_SUB_INSTALLATION_Product_Benchmark']?.map((item, idx) =>
                  productBenchmarkIndex === idx ? true : item,
                )
              : state.permitSectionsCompleted?.['MMP_SUB_INSTALLATION_Product_Benchmark']
                ? [...state.permitSectionsCompleted['MMP_SUB_INSTALLATION_Product_Benchmark'], true]
                : [true],
            data.permitTask,
          );
        }),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route, state: { notification: true } }));
  }
}
