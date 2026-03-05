import { NgModule } from '@angular/core';

import { AirSharedModule } from '@shared/air-shared/air-shared.module';
import { SharedModule } from '@shared/shared.module';
import { AirTaskSharedModule } from '@tasks/air/shared/air-task-shared.module';

import { ActionSharedModule } from '../../shared/action-shared-module';
import { AirActionReviewedRoutingModule } from './air-action-reviewed-routing.module';
import { ProvideSummaryComponent } from './response-list/provide-summary/provide-summary.component';
import { ResponseListComponent } from './response-list/response-list.component';
import { SummaryComponent } from './response-list/summary/summary.component';
import { ReviewedComponent } from './reviewed.component';

@NgModule({
  declarations: [ProvideSummaryComponent, ResponseListComponent, ReviewedComponent, SummaryComponent],
  imports: [ActionSharedModule, AirActionReviewedRoutingModule, AirSharedModule, AirTaskSharedModule, SharedModule],
})
export class AirActionReviewedModule {}
