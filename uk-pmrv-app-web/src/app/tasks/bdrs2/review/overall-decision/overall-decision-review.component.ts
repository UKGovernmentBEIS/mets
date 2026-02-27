import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { OverallVerificationAssessment } from '@shared/components/overall-decision-summary-template/overall-decision';
import { SharedModule } from '@shared/shared.module';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared/bdrs2-task-shared.module';
import { BdrS2VerificationReviewGroupDecisionComponent } from '@tasks/bdrs2/shared/components/decision/bdrs2-review-decision/bdrs2-verification-review-group-decision.component';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRS2ApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export interface ViewModel {
  isEditable: boolean;
  hideSubmit: boolean;
  overallDecision: OverallVerificationAssessment;
}

@Component({
  selector: 'app-bdrs2-verification-overall-decision-review',
  imports: [SharedModule, TaskSharedModule, BdrS2TaskSharedModule, BdrS2VerificationReviewGroupDecisionComponent],
  template: `
    <app-bdrs2-task-review returnToLink="../" [breadcrumb]="true">
      <app-page-heading caption="Overall decision">Review the overall decision</app-page-heading>
      <app-shared-overall-decision-summary-template
        [overallDecision]="vm().overallDecision"></app-shared-overall-decision-summary-template>
      <app-bdrs2-verification-review-group-decision></app-bdrs2-verification-review-group-decision>
    </app-bdrs2-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BdrS2OverallDecisionReviewComponent {
  readonly isEditable: Signal<boolean> = this.bdrs2Service.isEditable;
  readonly bdrs2Payload: Signal<BDRS2ApplicationVerificationSubmitRequestTaskPayload> = this.bdrs2Service.payload;

  readonly vm: Signal<ViewModel> = computed(() => {
    const isEditable = this.isEditable();
    const payload = this.bdrs2Payload();

    return {
      isEditable,
      hideSubmit: !isEditable || payload.verificationSectionsCompleted?.['overallDecision']?.[0],
      overallDecision: payload.verificationReport.overallAssessment as OverallVerificationAssessment,
    };
  });

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly bdrs2Service: BdrS2Service,
  ) {}
}
