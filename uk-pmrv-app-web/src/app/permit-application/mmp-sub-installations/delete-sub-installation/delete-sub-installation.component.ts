import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { isFallbackApproach, isProductBenchmark } from '../mmp-sub-installations-status';

@Component({
  selector: 'app-delete-sub-installation',
  template: `
    <app-page-heading size="xl">
      Are you sure you want to delete Sub-installation {{ (subInstallationNo$ | async) + 1 }}?
    </app-page-heading>

    <div class="govuk-button-group">
      <button type="button" appPendingButton (click)="delete()" govukWarnButton>Yes, delete</button>
      <a routerLink="../.." govukLink>Cancel</a>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteSubInstallationComponent implements PendingRequest {
  subInstallationNo$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('subInstallationNo'))));
  permitTask$ = this.route.data.pipe(map((x) => x?.permitTask));

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  delete(): void {
    combineLatest([this.permitTask$, this.store, this.route.paramMap])
      .pipe(
        first(),
        switchMap(([permitTask, state, paramMap]) => {
          const digitizedPlan = state.permit?.monitoringMethodologyPlans?.digitizedPlan;
          const subInstallations = digitizedPlan && digitizedPlan?.subInstallations;

          const productBenchmarkIndex = subInstallations
            ?.filter((subInstallation) => isProductBenchmark(subInstallation.subInstallationType))
            .findIndex((x) => x.subInstallationNo == paramMap.get('subInstallationNo'));

          const fallbackApproachIndex = subInstallations
            ?.filter((subInstallation) => isFallbackApproach(subInstallation.subInstallationType))
            .findIndex((x) => x.subInstallationNo == paramMap.get('subInstallationNo'));

          const productBenchmarkStatusValue =
            productBenchmarkIndex !== -1 &&
            state.permitSectionsCompleted?.['MMP_SUB_INSTALLATION_Product_Benchmark']?.length
              ? state.permitSectionsCompleted['MMP_SUB_INSTALLATION_Product_Benchmark']?.filter(
                  (item, idx) => productBenchmarkIndex !== idx,
                )
              : state.permitSectionsCompleted?.['MMP_SUB_INSTALLATION_Product_Benchmark'];

          const fallbackApproachStatusValue =
            fallbackApproachIndex !== -1 &&
            state.permitSectionsCompleted?.['MMP_SUB_INSTALLATION_Fallback_Approach']?.length
              ? state.permitSectionsCompleted['MMP_SUB_INSTALLATION_Fallback_Approach']?.filter(
                  (item, idx) => fallbackApproachIndex !== idx,
                )
              : state.permitSectionsCompleted?.['MMP_SUB_INSTALLATION_Fallback_Approach'];

          return this.store
            .patchTask(
              permitTask,
              {
                ...state.permit.monitoringMethodologyPlans,
                digitizedPlan: {
                  ...state.permit.monitoringMethodologyPlans?.digitizedPlan,
                  subInstallations: subInstallations
                    ?.filter((item) => item.subInstallationNo !== paramMap.get('subInstallationNo'))
                    .map((item, index) => {
                      return { ...item, subInstallationNo: index + '' };
                    }),
                },
              },
              productBenchmarkIndex !== -1 ? productBenchmarkStatusValue : fallbackApproachStatusValue,

              productBenchmarkIndex !== -1
                ? 'MMP_SUB_INSTALLATION_Product_Benchmark'
                : 'MMP_SUB_INSTALLATION_Fallback_Approach',
            )
            .pipe(this.pendingRequest.trackRequest());
        }),
      )
      .subscribe(() => this.router.navigate(['../../'], { relativeTo: this.route }));
  }
}
