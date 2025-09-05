import { ChangeDetectionStrategy, Component, computed, Inject, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { HSE_TI_TASK_FORM, HseTiService } from '@tasks/hseti/core';
import { HsetiTaskReviewComponent } from '@tasks/hseti/shared/components/hseti-review-task/hseti-review-task.component';
import { HseTiTaskSharedModule } from '@tasks/hseti/shared/hseti-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { HSETIApplicationRegulatorReviewSubmitRequestTaskPayload, HSETIRegulatorReviewOverallDecision } from 'pmrv-api';

import { decisionReasonFormProvider } from './decision-reason-form.provider';
export interface ViewModel {
  title: string;
  isEditable: boolean;
  hideSubmit: boolean;
  isGrantDisplayed: boolean;
  isRejectDisplayed: boolean;
  linkText: string;
}

@Component({
  selector: 'app-decision-reason',
  templateUrl: './decision-reason.component.html',
  standalone: true,
  imports: [SharedModule, TaskSharedModule, HseTiTaskSharedModule, HsetiTaskReviewComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [decisionReasonFormProvider],
})
export class HSETIOverallDecisionReviewReasonComponent {
  isEditable: Signal<boolean> = this.hsetiService.isEditable;

  hsetiPayload = this.hsetiService.payload as Signal<HSETIApplicationRegulatorReviewSubmitRequestTaskPayload>;
  requestTaskType = this.hsetiService.requestTaskType;
  allocationPeriod = this.hsetiService.allocationPeriod;

  overallDecision = computed(
    () => this.hsetiPayload().overallDecision?.type as HSETIRegulatorReviewOverallDecision['type'],
  );

  vm: Signal<ViewModel> = computed(() => {
    const titleDecisions: Record<any, string> = {
      APPROVED: 'Approve',
      REJECTED: 'Reject',
      DEEMED_WITHDRAWN: 'Deemed withdrawn',
      WITHDRAWN: 'Withdraw',
    };
    const isEditable = this.isEditable();
    const payload = this.hsetiPayload();
    const allocationPeriod = this.allocationPeriod();
    const overallDecisionType = payload.overallDecision?.type;
    const title = titleDecisions[overallDecisionType];

    return {
      title,
      isEditable: true,
      isGrantDisplayed: true,
      isRejectDisplayed: true,
      hideSubmit: !isEditable || payload.regulatorReviewSectionsCompleted?.['overallDecision']?.[0],
      linkText: `Review ${allocationPeriod} HSE target increase application`,
    };
  });

  constructor(
    @Inject(HSE_TI_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly pendingRequest: PendingRequestService,
    private readonly hsetiService: HseTiService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onContinue(): void {
    const payload = this.hsetiPayload();
    const type = payload.overallDecision?.type;

    if (!this.form.dirty) {
      this.router.navigate(['../summary'], { relativeTo: this.route });
    } else {
      this.hsetiService
        .postOverallDecisionReview({ type, reason: this.form.value.reason })
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => {
          this.router.navigate(['../summary'], { relativeTo: this.route });
        });
    }
  }
}
