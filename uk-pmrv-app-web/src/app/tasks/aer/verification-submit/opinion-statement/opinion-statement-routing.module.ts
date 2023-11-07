import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { AdditionalChangesComponent } from '@tasks/aer/verification-submit/opinion-statement/additional-changes/additional-changes.component';
import { CombustionSourceAddComponent } from '@tasks/aer/verification-submit/opinion-statement/combustion-sources/combustion-source-add/combustion-source-add.component';
import { CombustionSourceDeleteComponent } from '@tasks/aer/verification-submit/opinion-statement/combustion-sources/combustion-source-delete/combustion-source-delete.component';
import { CombustionSourceDeleteGuard } from '@tasks/aer/verification-submit/opinion-statement/combustion-sources/combustion-source-delete/combustion-source-delete.guard';
import { CombustionSourcesComponent } from '@tasks/aer/verification-submit/opinion-statement/combustion-sources/combustion-sources.component';
import { EmissionFactorsComponent } from '@tasks/aer/verification-submit/opinion-statement/emission-factors/emission-factors.component';
import { MonitoringApproachesComponent } from '@tasks/aer/verification-submit/opinion-statement/monitoring-approaches/monitoring-approaches.component';
import { ProcessSourceAddComponent } from '@tasks/aer/verification-submit/opinion-statement/process-sources/process-source-add/process-source-add.component';
import { ProcessSourceDeleteComponent } from '@tasks/aer/verification-submit/opinion-statement/process-sources/process-source-delete/process-source-delete.component';
import { ProcessSourceDeleteGuard } from '@tasks/aer/verification-submit/opinion-statement/process-sources/process-source-delete/process-source-delete.guard';
import { ProcessSourcesComponent } from '@tasks/aer/verification-submit/opinion-statement/process-sources/process-sources.component';
import { RegulatedActivitiesComponent } from '@tasks/aer/verification-submit/opinion-statement/regulated-activities/regulated-activities.component';
import { RegulatedActivityAddComponent } from '@tasks/aer/verification-submit/opinion-statement/regulated-activities/regulated-activity-add/regulated-activity-add.component';
import { RegulatedActivityDeleteComponent } from '@tasks/aer/verification-submit/opinion-statement/regulated-activities/regulated-activity-delete/regulated-activity-delete.component';
import { RegulatedActivityDeleteGuard } from '@tasks/aer/verification-submit/opinion-statement/regulated-activities/regulated-activity-delete/regulated-activity-delete.guard';
import { ReviewEmissionsComponent } from '@tasks/aer/verification-submit/opinion-statement/review-emissions/review-emissions.component';
import { InPersonVisitComponent } from '@tasks/aer/verification-submit/opinion-statement/site-visits/in-person-visit/in-person-visit.component';
import { NoVisitComponent } from '@tasks/aer/verification-submit/opinion-statement/site-visits/no-visit/no-visit.component';
import { SiteVisitsComponent } from '@tasks/aer/verification-submit/opinion-statement/site-visits/site-visits.component';
import { SiteVisitsGuard } from '@tasks/aer/verification-submit/opinion-statement/site-visits/site-visits.guard';
import { VirtualVisitComponent } from '@tasks/aer/verification-submit/opinion-statement/site-visits/virtual-visit/virtual-visit.component';
import { SummaryComponent } from '@tasks/aer/verification-submit/opinion-statement/summary/summary.component';
import { SummaryGuard } from '@tasks/aer/verification-submit/summary.guard';

const routes: Routes = [
  {
    path: '',
    data: { pageTitle: 'Regulated activities' },
    component: RegulatedActivitiesComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'add',
    data: { pageTitle: 'Add regulated activity' },
    component: RegulatedActivityAddComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'delete/:activityType',
    data: {
      pageTitle: 'Are you sure you want to regulated-activity-delete this regulated activity?',
      backlink: '../..',
    },
    component: RegulatedActivityDeleteComponent,
    canActivate: [RegulatedActivityDeleteGuard],
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'combustion-sources',
    children: [
      {
        path: '',
        data: { pageTitle: 'Combustion sources', backlink: '../' },
        component: CombustionSourcesComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'add',
        data: { pageTitle: 'Add combustion source' },
        component: CombustionSourceAddComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'delete/:combustionSource',
        data: { pageTitle: 'Delete combustion source', backlink: '../..' },
        component: CombustionSourceDeleteComponent,
        canActivate: [CombustionSourceDeleteGuard],
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
  {
    path: 'process-sources',
    children: [
      {
        path: '',
        data: { pageTitle: 'Process sources', backlink: '../combustion-sources' },
        component: ProcessSourcesComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'add',
        data: { pageTitle: 'Add process source' },
        component: ProcessSourceAddComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'delete/:processSource',
        data: { pageTitle: 'Delete process source', backlink: '../..' },
        component: ProcessSourceDeleteComponent,
        canActivate: [ProcessSourceDeleteGuard],
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
  {
    path: 'monitoring-approaches',
    data: { pageTitle: 'Monitoring approaches', backlink: '../process-sources' },
    component: MonitoringApproachesComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'emission-factors',
    data: { pageTitle: 'Emission factors', backlink: '../monitoring-approaches' },
    component: EmissionFactorsComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'review-emissions',
    data: { pageTitle: 'Review emissions', backlink: '../emission-factors' },
    component: ReviewEmissionsComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'additional-changes',
    data: { pageTitle: 'Additional changes', backlink: '../review-emissions' },
    component: AdditionalChangesComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'site-visits',
    data: { pageTitle: 'Conduction of site visits', backlink: '../additional-changes' },
    component: SiteVisitsComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'site-visits/in-person-visit',
    data: { pageTitle: 'In person site visit details', backlink: '../' },
    component: InPersonVisitComponent,
    canActivate: [SiteVisitsGuard],
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'site-visits/virtual-visit',
    data: { pageTitle: 'Virtual site visit details', backlink: '../' },
    component: VirtualVisitComponent,
    canActivate: [SiteVisitsGuard],
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'site-visits/no-visit',
    data: { pageTitle: 'No visit details', backlink: '../' },
    component: NoVisitComponent,
    canActivate: [SiteVisitsGuard],
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'summary',
    data: { pageTitle: 'Summary', breadcrumb: 'Opinion statement summary' },
    component: SummaryComponent,
    canActivate: [SummaryGuard],
    canDeactivate: [PendingRequestGuard],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class OpinionStatementRoutingModule {}
