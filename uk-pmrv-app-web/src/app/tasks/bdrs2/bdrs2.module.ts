import { NgModule } from '@angular/core';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BdrS2RoutingModule } from './bdrs2-routing.module';
import { BdrS2Service } from './core';
import {
  BDRS2BaselineStepGuard,
  BDRS2BaselineSummaryGuard,
  Bdrs2SendReportGuard,
  Bdrs2SendReportVerifierGuard,
} from './submit';
import { OpinionStatementSummaryGuard } from './verification-submit';

@NgModule({
  imports: [BdrS2RoutingModule, SharedModule, TaskSharedModule],
  providers: [
    BDRS2BaselineStepGuard,
    BDRS2BaselineSummaryGuard,
    Bdrs2SendReportGuard,
    Bdrs2SendReportVerifierGuard,
    BdrS2Service,
    CapitalizeFirstPipe,
    ItemNamePipe,
    OpinionStatementSummaryGuard,
  ],
})
export class BdrS2Module {}
