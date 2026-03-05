import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';
import { AerSharedModule } from '@tasks/aer/shared/aer-shared.module';
import { CompetentAuthorityGuidanceComponent } from '@tasks/aer/verification-submit/compliance-ets/competent-authority-guidance/competent-authority-guidance.component';
import { ComplianceEtsComponent } from '@tasks/aer/verification-submit/compliance-ets/compliance-ets.component';
import { ControlActivitiesComponent } from '@tasks/aer/verification-submit/compliance-ets/control-activities/control-activities.component';
import { DataVerificationComponent } from '@tasks/aer/verification-submit/compliance-ets/data-verification/data-verification.component';
import { DetailSourceDataComponent } from '@tasks/aer/verification-submit/compliance-ets/detail-source-data/detail-source-data.component';
import { MissingDataMethodsComponent } from '@tasks/aer/verification-submit/compliance-ets/missing-data-methods/missing-data-methods.component';
import { MonitoringApproachComponent } from '@tasks/aer/verification-submit/compliance-ets/monitoring-approach/monitoring-approach.component';
import { MonitoringPlanProceduresComponent } from '@tasks/aer/verification-submit/compliance-ets/monitoring-plan-procedures/monitoring-plan-procedures.component';
import { NonConformitiesComponent } from '@tasks/aer/verification-submit/compliance-ets/non-conformities/non-conformities.component';
import { PlannedChangesComponent } from '@tasks/aer/verification-submit/compliance-ets/planned-changes/planned-changes.component';
import { RegulationMonitoringReportingComponent } from '@tasks/aer/verification-submit/compliance-ets/regulation-monitoring-reporting/regulation-monitoring-reporting.component';
import { SummaryComponent } from '@tasks/aer/verification-submit/compliance-ets/summary/summary.component';
import { UncertaintyAssessmentComponent } from '@tasks/aer/verification-submit/compliance-ets/uncertainty-assessment/uncertainty-assessment.component';

import { ComplianceEtsRoutingModule } from './compliance-ets-routing.module';

@NgModule({
  declarations: [
    CompetentAuthorityGuidanceComponent,
    ComplianceEtsComponent,
    ControlActivitiesComponent,
    DataVerificationComponent,
    DetailSourceDataComponent,
    MissingDataMethodsComponent,
    MonitoringApproachComponent,
    MonitoringPlanProceduresComponent,
    NonConformitiesComponent,
    PlannedChangesComponent,
    RegulationMonitoringReportingComponent,
    SummaryComponent,
    UncertaintyAssessmentComponent,
  ],
  imports: [AerSharedModule, ComplianceEtsRoutingModule, SharedModule],
})
export class ComplianceEtsModule {}
