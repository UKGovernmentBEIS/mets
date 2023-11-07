import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { DeleteComponent } from '@tasks/aer/verification-submit/recommended-improvements/delete/delete.component';
import { DeleteGuard } from '@tasks/aer/verification-submit/recommended-improvements/delete/delete.guard';
import { ListComponent } from '@tasks/aer/verification-submit/recommended-improvements/list/list.component';
import { ListGuard } from '@tasks/aer/verification-submit/recommended-improvements/list/list.guard';
import { RecommendedImprovementsComponent } from '@tasks/aer/verification-submit/recommended-improvements/recommended-improvements.component';
import { RecommendedImprovementsItemComponent } from '@tasks/aer/verification-submit/recommended-improvements/recommended-improvements-item.component';
import { RecommendedImprovementsItemGuard } from '@tasks/aer/verification-submit/recommended-improvements/recommended-improvements-item.guard';
import { SummaryComponent } from '@tasks/aer/verification-submit/recommended-improvements/summary/summary.component';
import { SummaryGuard } from '@tasks/aer/verification-submit/summary.guard';

const routes: Routes = [
  {
    path: '',
    data: {
      pageTitle: 'Are there any recommended improvements?',
    },
    component: RecommendedImprovementsComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'list',
    data: { pageTitle: 'Recommended improvements', backlink: '../' },
    component: ListComponent,
    canActivate: [ListGuard],
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'delete/:index',
    data: { pageTitle: 'Delete a recommended improvement', backlink: '../../list' },
    component: DeleteComponent,
    canActivate: [DeleteGuard],
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'summary',
    data: { pageTitle: 'Check your answers' },
    component: SummaryComponent,
    canActivate: [SummaryGuard],
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: ':index',
    data: { pageTitle: 'Add/Edit a recommended improvement', backlink: '../list' },
    component: RecommendedImprovementsItemComponent,
    canActivate: [RecommendedImprovementsItemGuard],
    canDeactivate: [PendingRequestGuard],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class RecommendedImprovementsRoutingModule {}
