import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { BehaviorSubject } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { SharedModule } from '@shared/shared.module';
import { BdrService, BdrTaskSharedModule } from '@tasks/bdr/shared';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { BDRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-bdr-return-for-amends',
  templateUrl: './return-for-amends.component.html',
  standalone: true,
  imports: [SharedModule, TaskSharedModule, BdrTaskSharedModule, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReturnForAmendsComponent {
  isSubmitted$ = new BehaviorSubject<boolean>(false);
  requestId$: BehaviorSubject<string | null> = new BehaviorSubject(null);

  payload = this.bdrService.payload as Signal<BDRApplicationRegulatorReviewSubmitRequestTaskPayload>;
  decisionAmends = computed(() => {
    const returnPayload = this.payload();
    return Object.keys(returnPayload?.regulatorReviewGroupDecisions ?? [])
      .filter((key) => returnPayload?.regulatorReviewGroupDecisions[key]?.['type'] === 'OPERATOR_AMENDS_NEEDED')
      .map((key) => ({ groupKey: key, data: returnPayload.regulatorReviewGroupDecisions[key] }) as any);
  });

  constructor(
    readonly store: CommonTasksStore,
    readonly bdrService: BdrService,
    private readonly pendingRequest: PendingRequestService,
    private readonly breadcrumbs: BreadcrumbService,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    this.bdrService
      .postSubmit('BDR_REGULATOR_REVIEW_RETURN_FOR_AMENDS')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        this.requestId$.next(this.bdrService.requestId);
        this.breadcrumbs.showDashboardBreadcrumb(this.router.url);
        this.isSubmitted$.next(true);
      });
  }
}
