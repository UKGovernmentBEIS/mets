import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { OverallVerificationAssessment } from '@shared/components/overall-decision-summary-template/overall-decision';
import { SharedModule } from '@shared/shared.module';
import { AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';
import { AlrVerificationReviewGroupDecisionComponent } from '@tasks/alr/shared/components/decision/alr-verification-review-group-decision/alr-verification-review-group-decision.component';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { ALRApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export interface ViewModel {
  isEditable: boolean;
  hideSubmit: boolean;
  overallDecision: OverallVerificationAssessment;
}

@Component({
  selector: 'app-alr-verification-opinion-statement-review',
  imports: [SharedModule, TaskSharedModule, AlrTaskSharedModule, AlrVerificationReviewGroupDecisionComponent],
  template: `
    <app-alr-task-common returnLink="../" [breadcrumb]="true">
      <app-page-heading caption="Overall decision">Review the overall decision</app-page-heading>
      <app-shared-overall-decision-summary-template
        [overallDecision]="vm().overallDecision"></app-shared-overall-decision-summary-template>
      <app-alr-verification-review-group-decision></app-alr-verification-review-group-decision>
    </app-alr-task-common>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrOverallDecisionReviewComponent {
  readonly isEditable: Signal<boolean> = this.alrService.isEditable;
  readonly alrPayload: Signal<ALRApplicationVerificationSubmitRequestTaskPayload> = this.alrService.payload;

  readonly vm: Signal<ViewModel> = computed(() => {
    const isEditable = this.isEditable();
    const payload = this.alrPayload();

    return {
      isEditable,
      hideSubmit: !isEditable || payload.verificationSectionsCompleted?.['overallDecision']?.[0],
      overallDecision: payload.verificationReport.overallAssessment as OverallVerificationAssessment,
    };
  });

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly alrService: AlrService,
  ) {}
}
