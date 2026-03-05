import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { HseTiService } from '@tasks/hseti/core';
import { HsetiTaskReviewComponent } from '@tasks/hseti/shared/components/hseti-review-task/hseti-review-task.component';
import { HseTiTaskSharedModule } from '@tasks/hseti/shared/hseti-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { HSETIApplicationRegulatorReviewSubmitRequestTaskPayload, HSETIRegulatorReviewOverallDecision } from 'pmrv-api';
export interface ViewModel {
  isEditable: boolean;
  hideSubmit: boolean;
  isGrantDisplayed: boolean;
  isRejectDisplayed: boolean;
  linkText: string;
}

@Component({
  selector: 'app-overall-decision',
  templateUrl: './overall-decision.component.html',
  standalone: true,
  imports: [SharedModule, TaskSharedModule, HseTiTaskSharedModule, HsetiTaskReviewComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HSETIOverallDecisionReviewComponent {
  isEditable: Signal<boolean> = this.hsetiService.isEditable;

  hsetiPayload = this.hsetiService.payload as Signal<HSETIApplicationRegulatorReviewSubmitRequestTaskPayload>;
  requestTaskType = this.hsetiService.requestTaskType;
  allocationPeriod = this.hsetiService.allocationPeriod;

  vm: Signal<ViewModel> = computed(() => {
    const isEditable = this.isEditable();
    const payload = this.hsetiPayload();
    const allocationPeriod = this.allocationPeriod();

    return {
      isEditable: true,
      isGrantDisplayed: true,
      isRejectDisplayed: true,
      hideSubmit: !isEditable || payload.regulatorReviewSectionsCompleted?.['overallDecision']?.[0],
      linkText: `Review ${allocationPeriod} HSE target increase application`,
    };
  });

  constructor(
    private readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly hsetiService: HseTiService,
  ) {}

  buttonDisplayed(type: HSETIRegulatorReviewOverallDecision['type']): boolean {
    const payload = this.hsetiPayload();
    const hSETIGroupDecisionType = payload.regulatorReviewGroupDecisions?.['HSETI']?.type;
    const acceptedTypes = new Set(['APPROVED', 'DEEMED_WITHDRAWN', 'WITHDRAWN']);
    const rejectedTypes = new Set(['REJECTED', 'WITHDRAWN']);
    if (hSETIGroupDecisionType === 'ACCEPTED') {
      return acceptedTypes.has(type);
    }
    return rejectedTypes.has(type);
  }

  onContinue(type: HSETIRegulatorReviewOverallDecision['type']): void {
    if (!this.overallDecisionChanged(type)) {
      this.router.navigate(['reason'], { relativeTo: this.route });
    } else {
      this.hsetiService
        .postOverallDecisionReview({ type })
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => {
          this.router.navigate(['reason'], { relativeTo: this.route });
        });
    }
  }

  overallDecisionChanged(type: HSETIRegulatorReviewOverallDecision['type']): boolean {
    const payload = this.hsetiPayload();
    return payload.overallDecision?.type !== type;
  }
}
