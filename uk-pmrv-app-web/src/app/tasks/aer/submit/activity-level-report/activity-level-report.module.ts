import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';
import { AerSharedModule } from '@tasks/aer/shared/aer-shared.module';

import { ActivityLevelReportComponent } from './activity-level-report.component';
import { ActivityLevelReportRoutingModule } from './activity-level-report-routing.module';
import { SummaryComponent } from './summary/summary.component';

@NgModule({
  imports: [ActivityLevelReportRoutingModule, AerSharedModule, SharedModule],
  declarations: [ActivityLevelReportComponent, SummaryComponent],
})
export class ActivityLevelReportModule {}
