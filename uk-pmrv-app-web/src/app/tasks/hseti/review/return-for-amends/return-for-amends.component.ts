import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { BehaviorSubject } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { SharedModule } from '@shared/shared.module';
import { HseTiService } from '@tasks/hseti/core';
import { HseTiTaskSharedModule } from '@tasks/hseti/shared/hseti-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { HSETIApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-hseti-return-for-amends',
  imports: [SharedModule, TaskSharedModule, HseTiTaskSharedModule, RouterLink],
  templateUrl: './return-for-amends.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HsetiReturnForAmendsComponent {
  isSubmitted$ = new BehaviorSubject<boolean>(false);
  requestId$: BehaviorSubject<string | null> = new BehaviorSubject(null);

  payload = this.hsetiService.payload as Signal<HSETIApplicationRegulatorReviewSubmitRequestTaskPayload>;

  decisionAmends = computed(() => {
    const returnPayload = this.payload();
    return Object.keys(returnPayload?.regulatorReviewGroupDecisions ?? [])
      .filter((key) => returnPayload?.regulatorReviewGroupDecisions[key]?.['type'] === 'OPERATOR_AMENDS_NEEDED')
      .map((key) => ({ groupKey: key, data: returnPayload.regulatorReviewGroupDecisions[key] }) as any);
  });

  constructor(
    readonly store: CommonTasksStore,
    readonly hsetiService: HseTiService,
    private readonly pendingRequest: PendingRequestService,
    private readonly breadcrumbs: BreadcrumbService,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    this.hsetiService
      .postHseTiSubmit('HSE_TI_REGULATOR_REVIEW_RETURN_FOR_AMENDS')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        this.requestId$.next(this.hsetiService.requestId);
        this.breadcrumbs.showDashboardBreadcrumb(this.router.url);
        this.isSubmitted$.next(true);
      });
  }
}
