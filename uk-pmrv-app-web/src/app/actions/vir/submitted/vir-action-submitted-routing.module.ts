import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ResponseItemResolver } from '../core/response-item.resolver';
import { SubmittedComponent } from './submitted.component';
import { SummaryComponent } from './summary/summary.component';

const routes: Routes = [
  {
    path: '',
    data: { pageTitle: 'Verifier improvement report' },
    component: SubmittedComponent,
  },
  {
    path: ':id',
    resolve: { verificationDataItem: ResponseItemResolver },
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
export class VirActionSubmittedRoutingModule {}
