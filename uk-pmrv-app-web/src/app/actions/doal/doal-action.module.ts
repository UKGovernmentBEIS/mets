import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { ActionSharedModule } from '../shared/action-shared-module';
import { ClosedComponent } from './closed/closed.component';
import { AdditionalDocumentsComponent } from './common/additional-documents/additional-documents.component';
import { AlcInformationComponent } from './common/alc-information/alc-information.component';
import { DeterminationComponent } from './common/determination/determination.component';
import { OperatorReportComponent } from './common/operator-report/operator-report.component';
import { SubmittedComponent } from './common/submitted.component';
import { VerificationReportComponent } from './common/verification-report/verification-report.component';
import { CompletedComponent } from './completed/completed.component';
import { DateSubmittedComponent } from './completed/submitted/date-submitted/date-submitted.component';
import { ResponseComponent } from './completed/submitted/response/response.component';
import { SubmittedComponent as CompletedSubmittedComponent } from './completed/submitted/submitted.component';
import { DoalActionComponent } from './doal-action.component';
import { DoalActionRoutingModule } from './doal-action-routing.module';
import { PeerReviewDecisionComponent } from './peer-review-decision/peer-review-decision.component';
import { ProceededComponent } from './proceeded/proceeded.component';
import { DoalActionTaskComponent } from './shared/doal-action-task.component';

@NgModule({
  declarations: [
    AdditionalDocumentsComponent,
    AlcInformationComponent,
    ClosedComponent,
    CompletedComponent,
    CompletedSubmittedComponent,
    DateSubmittedComponent,
    DeterminationComponent,
    DoalActionComponent,
    DoalActionTaskComponent,
    OperatorReportComponent,
    PeerReviewDecisionComponent,
    ProceededComponent,
    ResponseComponent,
    SubmittedComponent,
    VerificationReportComponent,
  ],
  imports: [ActionSharedModule, DoalActionRoutingModule, SharedModule],
})
export class DoalActionModule {}
