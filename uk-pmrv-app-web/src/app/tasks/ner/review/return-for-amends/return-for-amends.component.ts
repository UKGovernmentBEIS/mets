import { ChangeDetectionStrategy, Component, computed, Signal, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { SharedModule } from '@shared/shared.module';
import { NerService } from '@tasks/ner/core';
import { NerTaskComponent } from '@tasks/ner/shared';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { NERApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-ner-return-for-amends',
  imports: [SharedModule, TaskSharedModule, NerTaskComponent, RouterLink],
  templateUrl: './return-for-amends.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NerReturnForAmendsComponent {
  readonly isEditable = this.nerService.isEditable;
  readonly isSubmitted = signal(false);
  readonly requestId = signal(null);

  readonly requestTaskType = this.nerService.requestTaskType;

  payload = this.nerService.payload as Signal<NERApplicationRegulatorReviewSubmitRequestTaskPayload>;
  decisionAmends = computed(() => {
    const returnPayload = this.payload();
    return Object.keys(returnPayload?.regulatorReviewGroupDecisions ?? [])
      .filter((key) => returnPayload?.regulatorReviewGroupDecisions[key]?.['type'] === 'OPERATOR_AMENDS_NEEDED')
      .map((key) => ({ groupKey: key, data: returnPayload.regulatorReviewGroupDecisions[key] }) as any);
  });

  constructor(
    readonly store: CommonTasksStore,
    readonly nerService: NerService,
    private readonly pendingRequest: PendingRequestService,
    private readonly breadcrumbs: BreadcrumbService,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    this.nerService
      .postNerSubmit(true)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        this.requestId.set(this.nerService.requestId);
        this.breadcrumbs.showDashboardBreadcrumb(this.router.url);
        this.isSubmitted.set(true);
      });
  }
}
