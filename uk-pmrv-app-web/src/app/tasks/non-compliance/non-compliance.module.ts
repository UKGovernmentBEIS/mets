import { CommonModule } from '@angular/common';
import { InjectionToken, NgModule } from '@angular/core';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { PeerReviewDecisionGuard } from '../../shared/components/peer-review-decision/peer-review-decision.guard';
import { CivilPenaltyNoticeComponent } from './civil-penalty-notice/civil-penalty-notice.component';
import { NotifyOperatorComponent as CivilPenaltyNotifyOperatorComponent } from './civil-penalty-notice/notify-operator/notify-operator.component';
import { PeerReviewComponent as CivilPenaltyPeerReviewComponent } from './civil-penalty-notice/peer-review/peer-review.component';
import { PeerReviewWaitComponent as CivilPenaltyPeerReviewWaitComponent } from './civil-penalty-notice/peer-review-wait/peer-review-wait.component';
import { SummaryComponent as CivilPenaltySummaryComponent } from './civil-penalty-notice/summary/summary.component';
import { UploadCivilPenaltyComponent } from './civil-penalty-notice/upload-civil-penalty/upload-civil-penalty.component';
import { CloseComponent } from './close/close.component';
import { ConfirmationComponent as CloseConfirmationComponent } from './close/confirmation/confirmation.component';
import { ConclusionComponent } from './conclusion/conclusion.component';
import { ConfirmationComponent as ConclusionConfirmationComponent } from './conclusion/confirmation/confirmation.component';
import { ProvideConclusionComponent } from './conclusion/provide-conclusion/provide-conclusion.component';
import { SummaryComponent as ConclusionSummaryComponet } from './conclusion/summary/summary.component';
import { DailyPenaltyNoticeComponent } from './daily-penalty-notice/daily-penalty-notice.component';
import { NotifyOperatorComponent as DailyPenaltyNoticeNotifyOperatorComponent } from './daily-penalty-notice/notify-operator/notify-operator.component';
import { PeerReviewComponent as DailyPenaltyNoticePeerReviewComponent } from './daily-penalty-notice/peer-review/peer-review.component';
import { PeerReviewWaitComponent as DailyPenaltyNoticePeerReviewWaitComponent } from './daily-penalty-notice/peer-review-wait/peer-review-wait.component';
import { SummaryComponent as DailyPenaltySummaryComponet } from './daily-penalty-notice/summary/summary.component';
import { UploadInitialNoticeComponent } from './daily-penalty-notice/upload-initial-notice/upload-initial-notice.component';
import { NonComplianceRoutingModule } from './non-compliance-routing.module';
import { NoticeOfIntentComponent as NoticeOfIntentTaskComponent } from './notice-of-intent/notice-of-intent.component';
import { NotifyOperatorComponent as NoticeOfIntentNotifyOperatorComponent } from './notice-of-intent/notify-operator/notify-operator.component';
import { PeerReviewComponent as NoticeOfIntentPeerReviewComponent } from './notice-of-intent/peer-review/peer-review.component';
import { PeerReviewWaitComponent as NoticeOfIntentPeerReviewWaitComponent } from './notice-of-intent/peer-review-wait/peer-review-wait.component';
import { SummaryComponent as NoticeOfIntentSummaryComponent } from './notice-of-intent/summary/summary.component';
import { UploadNoticeOfIntentComponent } from './notice-of-intent/upload-notice-of-intent/upload-notice-of-intent.component';
import { NonComplianceTaskComponent } from './shared/components/non-compliance-task/non-compliance-task.component';
import { ChooseWorkflowComponent } from './submit/choose-workflow/choose-workflow.component';
import { ChooseWorkflowAddComponent } from './submit/choose-workflow/choose-workflow-add/choose-workflow-add.component';
import { DeleteComponent } from './submit/choose-workflow/choose-workflow-add/delete/delete.component';
import { CivilPenaltyComponent } from './submit/civil-penalty/civil-penalty.component';
import { DailyPenaltyComponent } from './submit/civil-penalty/daily-penalty/daily-penalty.component';
import { NoticeOfIntentComponent } from './submit/civil-penalty/notice-of-intent/notice-of-intent.component';
import { ConfirmationComponent } from './submit/confirmation/confirmation.component';
import { DetailsOfBreachComponent } from './submit/details-of-breach/details-of-breach.component';
import { SubmitContainerComponent } from './submit/submit-container.component';
import { SummaryComponent as SubmitSummaryComponent } from './submit/summary/summary.component';

export const NON_COMPLIANCE_SUBMIT_PAGE_ROUTABLE = new InjectionToken<boolean>('NON_COMPLIANCE_SUBMIT_PAGE_ROUTABLE', {
  factory: () => true,
});

@NgModule({
  declarations: [
    ChooseWorkflowAddComponent,
    ChooseWorkflowComponent,
    CivilPenaltyComponent,
    CivilPenaltyNoticeComponent,
    CivilPenaltyNotifyOperatorComponent,
    CivilPenaltyPeerReviewComponent,
    CivilPenaltyPeerReviewWaitComponent,
    CivilPenaltySummaryComponent,
    CloseComponent,
    CloseConfirmationComponent,
    ConclusionComponent,
    ConclusionConfirmationComponent,
    ConclusionSummaryComponet,
    ConfirmationComponent,
    DailyPenaltyComponent,
    DailyPenaltyNoticeComponent,
    DailyPenaltyNoticeNotifyOperatorComponent,
    DailyPenaltyNoticePeerReviewComponent,
    DailyPenaltyNoticePeerReviewWaitComponent,
    DailyPenaltySummaryComponet,
    DeleteComponent,
    DetailsOfBreachComponent,
    NonComplianceTaskComponent,
    NoticeOfIntentComponent,
    NoticeOfIntentNotifyOperatorComponent,
    NoticeOfIntentPeerReviewComponent,
    NoticeOfIntentPeerReviewWaitComponent,
    NoticeOfIntentSummaryComponent,
    NoticeOfIntentTaskComponent,
    ProvideConclusionComponent,
    SubmitContainerComponent,
    SubmitSummaryComponent,
    UploadCivilPenaltyComponent,
    UploadInitialNoticeComponent,
    UploadNoticeOfIntentComponent,
  ],
  imports: [CommonModule, NonComplianceRoutingModule, ReturnToLinkComponent, SharedModule, TaskSharedModule],
  providers: [PeerReviewDecisionGuard],
})
export class NonComplianceModule {}
