import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BdrRoutingModule } from './bdr-routing.module';
import { BaselineReviewComponent, ReviewContainerComponent } from './review';
import { BdrTaskSharedModule } from './shared';
import {
  BaselineStepGuard,
  BaselineSummaryGuard,
  FreeAllocationComponent,
  ProvideMmpComponent,
  SendReportGuard,
  SendReportRegulatorComponent,
  SendReportVerifierComponent,
  SendReportVerifierGuard,
  SendVerifierOrRegulatorComponent,
  SubmitContainerComponent,
  SummaryComponent,
  UploadReportComponent,
} from './submit';
import {
  BaselineVerifierReviewComponent,
  OpinionStatementSummaryComponent,
  SendBdrReportComponent,
  VerificationSubmitContainerComponent,
} from './verification-submit';
import { OpinionStatementSummaryGuard } from './verification-submit/guards';
@NgModule({
  imports: [
    BaselineReviewComponent,
    BaselineVerifierReviewComponent,
    BdrRoutingModule,
    BdrTaskSharedModule,
    FreeAllocationComponent,
    OpinionStatementSummaryComponent,
    ProvideMmpComponent,
    ReviewContainerComponent,
    SendBdrReportComponent,
    SendReportRegulatorComponent,
    SendReportVerifierComponent,
    SendVerifierOrRegulatorComponent,
    SharedModule,
    SubmitContainerComponent,
    SummaryComponent,
    TaskSharedModule,
    UploadReportComponent,
    VerificationSubmitContainerComponent,
  ],
  providers: [
    BaselineStepGuard,
    BaselineSummaryGuard,
    OpinionStatementSummaryGuard,
    SendReportGuard,
    SendReportVerifierGuard,
  ],
})
export class BdrModule {}
