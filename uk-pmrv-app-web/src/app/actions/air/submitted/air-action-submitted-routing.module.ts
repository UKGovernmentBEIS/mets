import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AirImprovementItemResolver } from '../core/air-improvement-item.resolver';
import { SubmittedComponent } from './submitted.component';
import { SummaryComponent } from './summary/summary.component';

const routes: Routes = [
  {
    path: '',
    data: { pageTitle: 'Annual improvement report' },
    component: SubmittedComponent,
  },
  {
    path: ':id',
    resolve: { airImprovement: AirImprovementItemResolver },
    children: [
      {
        path: 'summary',
        data: { pageTitle: "Operator's response" },
        component: SummaryComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AirActionSubmittedRoutingModule {}
