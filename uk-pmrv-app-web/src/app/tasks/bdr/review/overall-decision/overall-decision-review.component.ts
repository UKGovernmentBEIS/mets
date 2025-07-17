import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BaselineSummaryTemplateComponent } from '@shared/components/bdr/baseline-summary-template/baseline-summary-template.component';
import { OverallVerificationAssessment } from '@shared/components/overall-decision-summary-template/overall-decision';
import { SharedModule } from '@shared/shared.module';
import { BdrTaskSharedModule } from '@tasks/bdr/shared/bdr-task-shared.module';
import { BdrVerificationReviewGroupDecisionComponent } from '@tasks/bdr/shared/components/decision/bdr-review-decision/bdr-verification-review-group-decision.component';
import { BdrService } from '@tasks/bdr/shared/services/bdr.service';
import { OverallDecisionSummaryComponent } from '@tasks/bdr/verification-submit/overall-decision';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export interface ViewModel {
  isEditable: boolean;
  hideSubmit: boolean;
  overallDecision: OverallVerificationAssessment;
}

@Component({
  selector: 'app-bdr-verification-opinion-statement-review',
  template: `
    <app-bdr-task-review returnToLink="../" [breadcrumb]="true">
      <app-page-heading caption="Overall decision">Review the overall decision</app-page-heading>
      <app-shared-overall-decision-summary-template
        [overallDecision]="vm().overallDecision"></app-shared-overall-decision-summary-template>
      <app-bdr-verification-review-group-decision></app-bdr-verification-review-group-decision>
    </app-bdr-task-review>
  `,
  standalone: true,
  imports: [
    SharedModule,
    TaskSharedModule,
    BdrTaskSharedModule,
    BaselineSummaryTemplateComponent,
    OverallDecisionSummaryComponent,
    BdrVerificationReviewGroupDecisionComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BdrOverallDecisionReviewComponent {
  isEditable: Signal<boolean> = this.bdrService.isEditable;
  bdrPayload: Signal<BDRApplicationVerificationSubmitRequestTaskPayload> = this.bdrService.payload;

  vm: Signal<ViewModel> = computed(() => {
    const isEditable = this.isEditable();
    const payload = this.bdrPayload();

    return {
      isEditable,
      hideSubmit: !isEditable || payload.verificationSectionsCompleted?.['overallDecision']?.[0],
      overallDecision: payload.verificationReport.overallAssessment as OverallVerificationAssessment,
    };
  });

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly bdrService: BdrService,
  ) {}
}
