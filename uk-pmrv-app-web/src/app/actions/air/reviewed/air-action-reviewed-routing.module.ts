import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AirImprovementItemResolver } from '../core/air-improvement-item.resolver';
import { ProvideSummaryComponent } from './response-list/provide-summary/provide-summary.component';
import { ResponseListComponent } from './response-list/response-list.component';
import { SummaryComponent } from './response-list/summary/summary.component';
import { ReviewedComponent } from './reviewed.component';

const routes: Routes = [
  {
    path: '',
    data: { pageTitle: 'Annual improvement report decision submitted' },
    component: ReviewedComponent,
  },
  {
    path: 'response-list',
    children: [
      {
        path: '',
        data: { pageTitle: 'Annual improvement report decision submitted' },
        component: ResponseListComponent,
      },
      {
        path: 'provide-summary',
        data: { pageTitle: 'Provide summary' },
        component: ProvideSummaryComponent,
      },
      {
        path: ':id',
        resolve: { airImprovement: AirImprovementItemResolver },
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
export class AirActionReviewedRoutingModule {}
