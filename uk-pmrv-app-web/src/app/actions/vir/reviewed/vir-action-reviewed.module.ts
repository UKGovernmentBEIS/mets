import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';
import { VirSharedModule } from '@shared/vir-shared/vir-shared.module';

import { ActionSharedModule } from '../../shared/action-shared-module';
import { ReportSummaryComponent } from './response-list/report-summary/report-summary.component';
import { ResponseListComponent } from './response-list/response-list.component';
import { SummaryComponent } from './response-list/summary/summary.component';
import { ReviewedComponent } from './reviewed.component';
import { VirActionReviewedRoutingModule } from './vir-action-reviewed-routing.module';

@NgModule({
  declarations: [ReportSummaryComponent, ResponseListComponent, ReviewedComponent, SummaryComponent],
  imports: [ActionSharedModule, SharedModule, VirActionReviewedRoutingModule, VirSharedModule],
})
export class VirActionReviewedModule {}
