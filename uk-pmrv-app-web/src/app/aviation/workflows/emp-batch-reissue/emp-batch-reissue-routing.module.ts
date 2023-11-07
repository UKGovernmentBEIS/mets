import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { BatchReissueSubmitGuard } from '@shared/guards/batch-reissue-submit.guard';

import { RegulatorsGuard } from '../../../regulators/regulators.guard';
import { RequestsComponent } from './requests/requests.component';
import { EmpBatchReissueSubmitGuard } from './submit/emp-batch-reissue-submit.guard';
import { FiltersComponent } from './submit/filters/filters.component';
import { SignatoryComponent } from './submit/signatory/signatory.component';
import { SignatoryGuard } from './submit/signatory/signatory.guard';
import { SummaryComponent } from './submit/summary/summary.component';
import { SummaryGuard } from './submit/summary/summary.guard';

const routes: Routes = [
  {
    path: '',
    component: RequestsComponent,
  },
  {
    path: 'submit',
    redirectTo: 'submit/filters',
    pathMatch: 'full',
  },
  {
    path: 'submit',
    data: { accountType: 'AVIATION' },
    canActivate: [BatchReissueSubmitGuard],
    canDeactivate: [EmpBatchReissueSubmitGuard],
    children: [
      {
        path: 'filters',
        data: { pageTitle: 'Select filters - Emp batch reissue' },
        component: FiltersComponent,
      },
      {
        path: 'signatory',
        data: {
          backlink: '../filters',
          pageTitle: 'Select name and signature for official notice - Emp batch reissue',
        },
        resolve: { regulators: RegulatorsGuard },
        canActivate: [SignatoryGuard],
        component: SignatoryComponent,
      },
      {
        path: 'summary',
        data: { pageTitle: 'Confirm details - Emp batch reissue' },
        resolve: { regulators: RegulatorsGuard },
        canActivate: [SummaryGuard],
        component: SummaryComponent,
      },
    ],
  },
  {
    path: ':request-id',
    data: { breadcrumb: ({ requestId }) => requestId },
    resolve: { requestId: (route) => route.paramMap.get('request-id') },
    children: [
      {
        path: '',
        loadChildren: () => import('../../../workflow-item/workflow-item.module').then((m) => m.WorkflowItemModule),
      },
      {
        path: 'actions',
        loadChildren: () => import('../../request-action/request-action.module').then((m) => m.RequestActionModule),
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class EmpBatchReissueRoutingModule {}
