import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';
import { AerSharedModule } from '@tasks/aer/shared/aer-shared.module';

import { AdditionalChangesComponent } from './additional-changes/additional-changes.component';
import { CombustionSourceAddComponent } from './combustion-sources/combustion-source-add/combustion-source-add.component';
import { CombustionSourceDeleteComponent } from './combustion-sources/combustion-source-delete/combustion-source-delete.component';
import { CombustionSourcesComponent } from './combustion-sources/combustion-sources.component';
import { EmissionFactorsComponent } from './emission-factors/emission-factors.component';
import { MonitoringApproachesComponent } from './monitoring-approaches/monitoring-approaches.component';
import { OpinionStatementRoutingModule } from './opinion-statement-routing.module';
import { ProcessSourceAddComponent } from './process-sources/process-source-add/process-source-add.component';
import { ProcessSourceDeleteComponent } from './process-sources/process-source-delete/process-source-delete.component';
import { ProcessSourcesComponent } from './process-sources/process-sources.component';
import { RegulatedActivitiesComponent } from './regulated-activities/regulated-activities.component';
import { RegulatedActivityAddComponent } from './regulated-activities/regulated-activity-add/regulated-activity-add.component';
import { RegulatedActivityDeleteComponent } from './regulated-activities/regulated-activity-delete/regulated-activity-delete.component';
import { ReviewEmissionsComponent } from './review-emissions/review-emissions.component';
import { InPersonVisitComponent } from './site-visits/in-person-visit/in-person-visit.component';
import { NoVisitComponent } from './site-visits/no-visit/no-visit.component';
import { SiteVisitsComponent } from './site-visits/site-visits.component';
import { VirtualVisitComponent } from './site-visits/virtual-visit/virtual-visit.component';
import { SummaryComponent } from './summary/summary.component';

@NgModule({
  declarations: [
    AdditionalChangesComponent,
    CombustionSourceAddComponent,
    CombustionSourceDeleteComponent,
    CombustionSourcesComponent,
    EmissionFactorsComponent,
    InPersonVisitComponent,
    MonitoringApproachesComponent,
    NoVisitComponent,
    ProcessSourceAddComponent,
    ProcessSourceDeleteComponent,
    ProcessSourcesComponent,
    RegulatedActivitiesComponent,
    RegulatedActivityAddComponent,
    RegulatedActivityDeleteComponent,
    ReviewEmissionsComponent,
    SiteVisitsComponent,
    SummaryComponent,
    VirtualVisitComponent,
  ],
  imports: [AerSharedModule, OpinionStatementRoutingModule, SharedModule],
})
export class OpinionStatementModule {}
