import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { DeleteComponent as PerPlanDeleteComponent } from '@tasks/aer/verification-submit/non-conformities/per-plan/delete/delete.component';
import { DeleteGuard as PerPlanDeleteGuard } from '@tasks/aer/verification-submit/non-conformities/per-plan/delete/delete.guard';
import { ListComponent as PerPlanListComponent } from '@tasks/aer/verification-submit/non-conformities/per-plan/list/list.component';
import { ListGuard as PerPlanListGuard } from '@tasks/aer/verification-submit/non-conformities/per-plan/list/list.guard';
import { PerPlanComponent } from '@tasks/aer/verification-submit/non-conformities/per-plan/per-plan.component';
import { PerPlanItemComponent } from '@tasks/aer/verification-submit/non-conformities/per-plan/per-plan-item.component';
import { PerPlanItemGuard } from '@tasks/aer/verification-submit/non-conformities/per-plan/per-plan-item.guard';
import { DeleteComponent as PreviousYearDeleteComponent } from '@tasks/aer/verification-submit/non-conformities/previous-year/delete/delete.component';
import { DeleteGuard as PreviousYearDeleteGuard } from '@tasks/aer/verification-submit/non-conformities/previous-year/delete/delete.guard';
import { ListComponent as PreviousYearListComponent } from '@tasks/aer/verification-submit/non-conformities/previous-year/list/list.component';
import { ListGuard as PreviousYearListGuard } from '@tasks/aer/verification-submit/non-conformities/previous-year/list/list.guard';
import { PreviousYearComponent } from '@tasks/aer/verification-submit/non-conformities/previous-year/previous-year.component';
import { PreviousYearItemComponent } from '@tasks/aer/verification-submit/non-conformities/previous-year/previous-year-item.component';
import { PreviousYearItemGuard } from '@tasks/aer/verification-submit/non-conformities/previous-year/previous-year-item.guard';
import { SummaryComponent } from '@tasks/aer/verification-submit/non-conformities/summary/summary.component';
import { SummaryGuard } from '@tasks/aer/verification-submit/summary.guard';

const routes: Routes = [
  {
    path: '',
    data: { pageTitle: 'Have there been any uncorrected non-conformities with the approved monitoring plan?' },
    component: PerPlanComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'per-plan',
    children: [
      {
        path: 'list',
        data: { pageTitle: 'Non-conformities with the approved monitoring plan', backlink: '../..' },
        component: PerPlanListComponent,
        canActivate: [PerPlanListGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: ':index',
        data: { pageTitle: 'Add/Edit a non-conformity with the approved monitoring plan', backlink: '../' },
        component: PerPlanItemComponent,
        canActivate: [PerPlanItemGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'delete/:index',
        data: { pageTitle: 'Delete a non-conformity with the approved monitoring plan', backlink: '../..' },
        component: PerPlanDeleteComponent,
        canActivate: [PerPlanDeleteGuard],
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
  {
    path: 'previous-year',
    children: [
      {
        path: '',
        data: {
          pageTitle: 'Are there any unresolved non-conformities from a previous year?',
          backlink: '../per-plan/list',
        },
        component: PreviousYearComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'list',
        data: { pageTitle: 'Non-conformities from the previous year that have not been resolved', backlink: '../' },
        component: PreviousYearListComponent,
        canActivate: [PreviousYearListGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: ':index',
        data: {
          pageTitle: 'Add/Edit a non-conformity from the previous year that has not been resolved',
          backlink: '../list',
        },
        component: PreviousYearItemComponent,
        canActivate: [PreviousYearItemGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'delete/:index',
        data: {
          pageTitle: 'Delete a non-conformity from the previous year that has not been resolved',
          backlink: '../../list',
        },
        component: PreviousYearDeleteComponent,
        canActivate: [PreviousYearDeleteGuard],
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
  {
    path: 'summary',
    data: { pageTitle: 'Check your answers', breadcrumb: 'Non-conformities summary' },
    component: SummaryComponent,
    canActivate: [SummaryGuard],
    canDeactivate: [PendingRequestGuard],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class NonConformitiesRoutingModule {}
