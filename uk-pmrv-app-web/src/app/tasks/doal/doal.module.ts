import { NgModule } from '@angular/core';

import { PeerReviewDecisionGuard } from '@shared/components/peer-review-decision/peer-review-decision.guard';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { SharedModule } from '@shared/shared.module';
import { AuthorityResponseContainerComponent } from '@tasks/doal/authority-response/authority-response-container.component';
import { DateSubmittedComponent } from '@tasks/doal/authority-response/date-submitted/date-submitted.component';
import { SummaryComponent as DateSubmittedSummaryComponent } from '@tasks/doal/authority-response/date-submitted/summary/summary.component';
import { NotifyOperatorComponent as AuthorityResponseNotifyOperatorComponent } from '@tasks/doal/authority-response/notify-operator/notify-operator.component';
import { ApprovedAllocationsComponent } from '@tasks/doal/authority-response/response/approved-allocations/approved-allocations.component';
import { DeleteComponent as AuthorityResponseDeleteComponent } from '@tasks/doal/authority-response/response/preliminary-allocations/preliminary-allocation/delete/delete.component';
import { PreliminaryAllocationComponent as AuthorityResponsePreliminaryAllocationComponent } from '@tasks/doal/authority-response/response/preliminary-allocations/preliminary-allocation/preliminary-allocation.component';
import { PreliminaryAllocationsComponent as AuthorityResponsePreliminaryAllocationsComponent } from '@tasks/doal/authority-response/response/preliminary-allocations/preliminary-allocations.component';
import { ResponseComponent } from '@tasks/doal/authority-response/response/response.component';
import { SummaryComponent as ResponseSummaryComponent } from '@tasks/doal/authority-response/response/summary/summary.component';

import { TaskSharedModule } from '../shared/task-shared-module';
import { DoalRoutingModule } from './doal-routing.module';
import { PeerReviewComponent } from './peer-review/peer-review.component';
import { PeerReviewWaitComponent } from './peer-review-wait/peer-review-wait.component';
import { AdditionalDocumentsSummaryComponent } from './shared/components/additional-documents-summary/additional-documents-summary.component';
import { AlcInformationSummaryComponent } from './shared/components/alc-information-summary/alc-information-summary.component';
import { DeterminationSummaryComponent } from './shared/components/determination-summary/determination-summary.component';
import { DoalTaskComponent } from './shared/components/doal-task/doal-task.component';
import { OperatorReportSummaryComponent } from './shared/components/operator-report-summary/operator-report-summary.component';
import { SubmitSectionListComponent } from './shared/components/submit-section-list/submit-section-list.component';
import { VerificationReportSummaryComponent } from './shared/components/verification-report-summary/verification-report-summary.component';
import { AdditionalDocumentsComponent } from './submit/additional-documents/additional-documents.component';
import { SummaryComponent as AdditionalDocumentsSubmitSummaryComponent } from './submit/additional-documents/summary/summary.component';
import { ActivityLevelComponent } from './submit/alc-information/activity-levels/activity-level/activity-level.component';
import { DeleteComponent as ActivityLevelDeleteComponent } from './submit/alc-information/activity-levels/activity-level/delete/delete.component';
import { ActivityLevelsComponent } from './submit/alc-information/activity-levels/activity-levels.component';
import { CommentsComponent } from './submit/alc-information/comments/comments.component';
import { EstimatesComponent } from './submit/alc-information/estimates/estimates.component';
import { DeleteComponent as PreliminaryAllocationDeleteComponent } from './submit/alc-information/preliminary-allocations/preliminary-allocation/delete/delete.component';
import { PreliminaryAllocationComponent } from './submit/alc-information/preliminary-allocations/preliminary-allocation/preliminary-allocation.component';
import { PreliminaryAllocationsComponent } from './submit/alc-information/preliminary-allocations/preliminary-allocations.component';
import { SummaryComponent as AlcInformationSubmitSummaryComponent } from './submit/alc-information/summary/summary.component';
import { CompleteComponent } from './submit/complete/complete.component';
import { ReasonComponent as CloseReasonComponent } from './submit/determination/close/reason/reason.component';
import { DeterminationComponent } from './submit/determination/determination.component';
import { OfficialNoticeComponent as ProceedAuthorityOfficialNoticeComponent } from './submit/determination/proceed-authority/official-notice/official-notice.component';
import { ReasonComponent as ProceedAuthorityArticleReasonComponent } from './submit/determination/proceed-authority/reason/reason.component';
import { WithholdingComponent as ProceedAuthorityWithholdingComponent } from './submit/determination/proceed-authority/withholding/withholding.component';
import { SummaryComponent as DeterminationSubmitSummaryComponent } from './submit/determination/summary/summary.component';
import { NotifyOperatorComponent } from './submit/notify-operator/notify-operator.component';
import { OperatorReportComponent } from './submit/operator-report/operator-report.component';
import { SummaryComponent as OperatorReportSubmitSummaryComponent } from './submit/operator-report/summary/summary.component';
import { SubmitContainerComponent } from './submit/submit-container.component';
import { SummaryComponent as VerificationReportSubmitSummaryComponent } from './submit/verification-report/summary/summary.component';
import { VerificationReportComponent } from './submit/verification-report/verification-report.component';

@NgModule({
  declarations: [
    ActivityLevelComponent,
    ActivityLevelDeleteComponent,
    ActivityLevelsComponent,
    AdditionalDocumentsComponent,
    AdditionalDocumentsSubmitSummaryComponent,
    AdditionalDocumentsSummaryComponent,
    AlcInformationSubmitSummaryComponent,
    AlcInformationSummaryComponent,
    ApprovedAllocationsComponent,
    AuthorityResponseContainerComponent,
    AuthorityResponseContainerComponent,
    AuthorityResponseDeleteComponent,
    AuthorityResponseNotifyOperatorComponent,
    AuthorityResponsePreliminaryAllocationComponent,
    AuthorityResponsePreliminaryAllocationsComponent,
    CloseReasonComponent,
    CommentsComponent,
    CompleteComponent,
    DateSubmittedComponent,
    DateSubmittedSummaryComponent,
    DeterminationComponent,
    DeterminationSubmitSummaryComponent,
    DeterminationSummaryComponent,
    DoalTaskComponent,
    EstimatesComponent,
    NotifyOperatorComponent,
    OperatorReportComponent,
    OperatorReportSubmitSummaryComponent,
    OperatorReportSummaryComponent,
    PeerReviewComponent,
    PeerReviewWaitComponent,
    PreliminaryAllocationComponent,
    PreliminaryAllocationDeleteComponent,
    PreliminaryAllocationsComponent,
    ProceedAuthorityArticleReasonComponent,
    ProceedAuthorityOfficialNoticeComponent,
    ProceedAuthorityWithholdingComponent,
    ResponseComponent,
    ResponseSummaryComponent,
    SubmitContainerComponent,
    SubmitSectionListComponent,
    VerificationReportComponent,
    VerificationReportSubmitSummaryComponent,
    VerificationReportSummaryComponent,
  ],
  imports: [DoalRoutingModule, SharedModule, TaskSharedModule],
  providers: [ItemNamePipe, PeerReviewDecisionGuard],
})
export class DoalModule {}
