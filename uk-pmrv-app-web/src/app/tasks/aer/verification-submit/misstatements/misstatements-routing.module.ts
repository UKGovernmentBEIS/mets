import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { MisstatementsItemDeleteComponent } from '@tasks/aer/verification-submit/misstatements/delete/misstatements-item-delete.component';
import { MisstatementsItemDeleteGuard } from '@tasks/aer/verification-submit/misstatements/delete/misstatements-item-delete.guard';
import { MisstatementsListComponent } from '@tasks/aer/verification-submit/misstatements/list/misstatements-list.component';
import { MisstatementsListGuard } from '@tasks/aer/verification-submit/misstatements/list/misstatements-list.guard';
import { MisstatementsComponent } from '@tasks/aer/verification-submit/misstatements/misstatements.component';
import { MisstatementsItemComponent } from '@tasks/aer/verification-submit/misstatements/misstatements-item.component';
import { MisstatementsItemGuard } from '@tasks/aer/verification-submit/misstatements/misstatements-item.guard';
import { SummaryComponent } from '@tasks/aer/verification-submit/misstatements/summary/summary.component';
import { SummaryGuard } from '@tasks/aer/verification-submit/summary.guard';

const routes: Routes = [
  {
    path: '',
    data: { pageTitle: 'Are there any misstatements that were not corrected before completing this report' },
    component: MisstatementsComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'list',
    data: { pageTitle: 'Uncorrected misstatements not corrected before report', backlink: '../' },
    component: MisstatementsListComponent,
    canActivate: [MisstatementsListGuard],
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'delete/:index',
    data: { pageTitle: 'Are you sure you want to delete this item', backlink: '../../list' },
    component: MisstatementsItemDeleteComponent,
    canActivate: [MisstatementsItemDeleteGuard],
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'summary',
    data: { pageTitle: 'Check your answers', breadcrumb: 'Uncorrected misstatements summary' },
    component: SummaryComponent,
    canActivate: [SummaryGuard],
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: ':index',
    data: { pageTitle: 'Add a misstatement not corrected before completing this report', backlink: '../list' },
    component: MisstatementsItemComponent,
    canActivate: [MisstatementsItemGuard],
    canDeactivate: [PendingRequestGuard],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class MisstatementsRoutingModule {}
