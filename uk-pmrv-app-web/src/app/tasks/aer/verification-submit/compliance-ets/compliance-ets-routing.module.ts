import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
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
import { SummaryGuard } from '@tasks/aer/verification-submit/summary.guard';

const routes: Routes = [
  {
    path: '',
    data: { pageTitle: 'Monitoring plan requirements' },
    component: ComplianceEtsComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'regulation-monitoring-reporting',
    data: { pageTitle: 'EU regulation on monitoring and reporting', backlink: '../' },
    component: RegulationMonitoringReportingComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'detail-source-data',
    data: { pageTitle: 'Verify the detail and source of data', backlink: '../regulation-monitoring-reporting' },
    component: DetailSourceDataComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'control-activities',
    data: { pageTitle: 'Control activities', backlink: '../detail-source-data' },
    component: ControlActivitiesComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'monitoring-plan-procedures',
    data: { pageTitle: 'Procedures in the monitoring plan', backlink: '../control-activities' },
    component: MonitoringPlanProceduresComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'data-verification',
    data: { pageTitle: 'Data verification', backlink: '../monitoring-plan-procedures' },
    component: DataVerificationComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'monitoring-approach',
    data: { pageTitle: 'Monitoring approach applied correctly', backlink: '../data-verification' },
    component: MonitoringApproachComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'planned-changes',
    data: { pageTitle: 'Report any planned or actual changes', backlink: '../monitoring-approach' },
    component: PlannedChangesComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'missing-data-methods',
    data: { pageTitle: 'Methods for applying missing data', backlink: '../planned-changes' },
    component: MissingDataMethodsComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'uncertainty-assessment',
    data: { pageTitle: 'Uncertainty assessment', backlink: '../missing-data-methods' },
    component: UncertaintyAssessmentComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'competent-authority-guidance',
    data: { pageTitle: 'Competent authority guidance', backlink: '../uncertainty-assessment' },
    component: CompetentAuthorityGuidanceComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'non-conformities',
    data: { pageTitle: 'Non-conformities', backlink: '../competent-authority-guidance' },
    component: NonConformitiesComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'summary',
    data: { pageTitle: 'Summary', breadcrumb: 'Compliance with ETS rules summary' },
    component: SummaryComponent,
    canActivate: [SummaryGuard],
    canDeactivate: [PendingRequestGuard],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ComplianceEtsRoutingModule {}
