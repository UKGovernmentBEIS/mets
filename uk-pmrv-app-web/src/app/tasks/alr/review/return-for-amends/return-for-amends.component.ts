import { ChangeDetectionStrategy, Component, computed, Signal, signal } from '@angular/core';
import { Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { SharedModule } from '@shared/shared.module';
import { AlrService } from '@tasks/alr/core';
import { AlrTaskReviewComponent } from '@tasks/alr/shared';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

interface ViewModel {
  isSubmitted: boolean;
  decisions: Array<any>;
  isEditable: boolean;
}

@Component({
  selector: 'app-alr-return-for-amends',
  imports: [SharedModule, TaskSharedModule, AlrTaskReviewComponent],
  templateUrl: './return-for-amends.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrReturnForAmendsComponent {
  vm: Signal<ViewModel> = computed(() => {
    const isSubmitted = this.isSubmitted();
    const payload = this.payload();
    const isEditable = this.isEditable();

    return {
      isSubmitted,
      decisions: Object.keys(payload?.regulatorReviewGroupDecisions ?? [])
        .filter((key) => payload?.regulatorReviewGroupDecisions[key]?.['type'] === 'OPERATOR_AMENDS_NEEDED')
        .map((key) => ({ groupKey: key, data: payload.regulatorReviewGroupDecisions[key] }) as any),
      isEditable,
    };
  });

  private readonly isEditable = this.alrService.isEditable;
  private readonly isSubmitted = signal(false);
  private readonly payload = this.alrService.payload as Signal<ALRApplicationRegulatorReviewSubmitRequestTaskPayload>;

  constructor(
    readonly alrService: AlrService,
    private readonly pendingRequest: PendingRequestService,
    private readonly breadcrumbs: BreadcrumbService,
    private readonly router: Router,
  ) {}

  onSubmit() {
    this.alrService
      .postAlrSubmit('ALR_REGULATOR_REVIEW_RETURN_FOR_AMENDS')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        this.breadcrumbs.showDashboardBreadcrumb(this.router.url);
        this.isSubmitted.set(true);
      });
  }
}
