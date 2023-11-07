import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ResponseItemResolver } from '../core/response-item.resolver';
import { ReportSummaryComponent } from './response-list/report-summary/report-summary.component';
import { ResponseListComponent } from './response-list/response-list.component';
import { SummaryComponent } from './response-list/summary/summary.component';
import { ReviewedComponent } from './reviewed.component';

const routes: Routes = [
  {
    path: '',
    data: { pageTitle: 'Verifier improvement report reviewed' },
    component: ReviewedComponent,
  },
  {
    path: 'response-list',
    children: [
      {
        path: '',
        data: { pageTitle: 'Verifier improvement report reviewed' },
        component: ResponseListComponent,
      },
      {
        path: 'report-summary',
        data: { pageTitle: 'Report summary' },
        component: ReportSummaryComponent,
      },
      {
        path: ':id',
        resolve: { verificationDataItem: ResponseItemResolver },
        children: [
          {
            path: 'summary',
            data: { pageTitle: "Regulator's response" },
            component: SummaryComponent,
          },
        ],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class VirActionReviewedRoutingModule {}
