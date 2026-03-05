import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';
import { AerSharedModule } from '@tasks/aer/shared/aer-shared.module';

import { SummaryComponent } from './summary/summary.component';
import { VerifiedActivityLevelReportComponent } from './verified-activity-level-report.component';
import { VerifiedActivityLevelReportRoutingModule } from './verified-activity-level-report-routing.module';

@NgModule({
  declarations: [SummaryComponent, VerifiedActivityLevelReportComponent],
  imports: [AerSharedModule, SharedModule, VerifiedActivityLevelReportRoutingModule],
})
export class VerifiedActivityLevelReportModule {}
