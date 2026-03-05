import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { HseTiService } from '@tasks/hseti/core';
import { HseTiTaskSharedModule } from '@tasks/hseti/shared/hseti-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { HSETIApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-hseti-decision-summary',
  templateUrl: './decision-summary.component.html',
  standalone: true,
  imports: [SharedModule, TaskSharedModule, HseTiTaskSharedModule, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DecisionSummaryComponent {
  isEditable: Signal<boolean> = this.hsetiService.isEditable;
  hsetiPayload = this.hsetiService.payload as Signal<HSETIApplicationRegulatorReviewSubmitRequestTaskPayload>;
  allocationPeriod: Signal<string> = this.hsetiService.allocationPeriod;
  linkText: Signal<string> = computed(() => `Review ${this.allocationPeriod()} HSE target increase application`);

  hideSubmit: Signal<boolean> = computed(() => {
    const isEditable = this.isEditable();
    return !isEditable || this.hsetiPayload().regulatorReviewSectionsCompleted?.['OVERALL_DECISION'];
  });

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly hsetiService: HseTiService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onConfirm() {
    this.hsetiService
      .postOverallDecisionReview(null, true)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        this.router.navigate(['../..'], { relativeTo: this.route });
      });
  }
}
