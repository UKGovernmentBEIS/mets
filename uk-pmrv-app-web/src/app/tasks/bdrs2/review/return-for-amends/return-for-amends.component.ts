import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { BehaviorSubject } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { SharedModule } from '@shared/shared.module';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-bdrs2-return-for-amends',
  imports: [SharedModule, TaskSharedModule, BdrS2TaskSharedModule, RouterLink],
  templateUrl: './return-for-amends.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Bdrs2ReturnForAmendsComponent {
  isSubmitted$ = new BehaviorSubject<boolean>(false);
  requestId$: BehaviorSubject<string | null> = new BehaviorSubject(null);

  payload = this.bdrs2Service.payload as Signal<BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload>;
  decisionAmends = computed(() => {
    const returnPayload = this.payload();
    return Object.keys(returnPayload?.regulatorReviewGroupDecisions ?? [])
      .filter((key) => returnPayload?.regulatorReviewGroupDecisions[key]?.['type'] === 'OPERATOR_AMENDS_NEEDED')
      .map((key) => ({ groupKey: key, data: returnPayload.regulatorReviewGroupDecisions[key] }) as any);
  });

  constructor(
    readonly store: CommonTasksStore,
    readonly bdrs2Service: BdrS2Service,
    private readonly pendingRequest: PendingRequestService,
    private readonly breadcrumbs: BreadcrumbService,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    this.bdrs2Service
      .postSubmit('BDRS2_REGULATOR_REVIEW_RETURN_FOR_AMENDS')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        this.requestId$.next(this.bdrs2Service.requestId);
        this.breadcrumbs.showDashboardBreadcrumb(this.router.url);
        this.isSubmitted$.next(true);
      });
  }
}
