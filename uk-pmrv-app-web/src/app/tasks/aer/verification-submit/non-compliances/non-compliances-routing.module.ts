import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { DeleteComponent } from '@tasks/aer/verification-submit/non-compliances/delete/delete.component';
import { DeleteGuard } from '@tasks/aer/verification-submit/non-compliances/delete/delete.guard';
import { ListComponent } from '@tasks/aer/verification-submit/non-compliances/list/list.component';
import { ListGuard } from '@tasks/aer/verification-submit/non-compliances/list/list.guard';
import { NonCompliancesComponent } from '@tasks/aer/verification-submit/non-compliances/non-compliances.component';
import { NonCompliancesItemComponent } from '@tasks/aer/verification-submit/non-compliances/non-compliances-item.component';
import { NonCompliancesItemGuard } from '@tasks/aer/verification-submit/non-compliances/non-compliances-item.guard';
import { SummaryComponent } from '@tasks/aer/verification-submit/non-compliances/summary/summary.component';
import { SummaryGuard } from '@tasks/aer/verification-submit/summary.guard';

const routes: Routes = [
  {
    path: '',
    data: {
      pageTitle: 'Have there been any uncorrected non-compliances with the monitoring and reporting regulations?',
    },
    component: NonCompliancesComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'list',
    data: { pageTitle: 'Non-compliances that were not corrected before completing this report', backlink: '../' },
    component: ListComponent,
    canActivate: [ListGuard],
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'delete/:index',
    data: { pageTitle: 'Delete a non-compliance with the approved monitoring plan', backlink: '../../list' },
    component: DeleteComponent,
    canActivate: [DeleteGuard],
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'summary',
    data: { pageTitle: 'Check your answers', breadcrumb: 'Non-compliances summary' },
    component: SummaryComponent,
    canActivate: [SummaryGuard],
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: ':index',
    data: { pageTitle: 'Add/Edit a non-compliance with the monitoring and reporting regulations', backlink: '../list' },
    component: NonCompliancesItemComponent,
    canActivate: [NonCompliancesItemGuard],
    canDeactivate: [PendingRequestGuard],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class NonCompliancesRoutingModule {}
