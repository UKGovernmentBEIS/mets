import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { SummaryGuard } from '@tasks/aer/verification-submit/summary.guard';
import { SummaryComponent } from '@tasks/aer/verification-submit/verified-activity-level-report/summary/summary.component';
import { VerifiedActivityLevelReportComponent } from '@tasks/aer/verification-submit/verified-activity-level-report/verified-activity-level-report.component';

const routes: Routes = [
  {
    path: '',
    data: { pageTitle: 'Verification report of the activity level report' },
    component: VerifiedActivityLevelReportComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'summary',
    data: {
      pageTitle: 'Verification report of the activity level report - Check your answers',
      breadcrumb: 'Verification report of the activity level report summary',
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
export class VerifiedActivityLevelReportRoutingModule {}
