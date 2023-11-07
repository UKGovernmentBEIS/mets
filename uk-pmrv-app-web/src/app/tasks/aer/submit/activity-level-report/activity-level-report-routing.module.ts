import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { ActivityLevelReportComponent } from '@tasks/aer/submit/activity-level-report/activity-level-report.component';
import { SummaryComponent } from '@tasks/aer/submit/activity-level-report/summary/summary.component';
import { SummaryGuard } from '@tasks/aer/submit/summary.guard';

const routes: Routes = [
  {
    path: '',
    data: { pageTitle: 'Activity level report' },
    component: ActivityLevelReportComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'summary',
    data: {
      pageTitle: 'Activity level report - Check your answers',
      breadcrumb: 'Activity level report',
    },
    component: SummaryComponent,
    canActivate: [SummaryGuard],
    canDeactivate: [PendingRequestGuard],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ActivityLevelReportRoutingModule {}
