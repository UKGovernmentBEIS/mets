import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { RegulatorsGuard } from '../regulators/regulators.guard';
import { BatchReissueSubmitGuard } from '../shared/guards/batch-reissue-submit.guard';
import { RequestsComponent } from './requests/requests.component';
import { FiltersComponent } from './submit/filters/filters.component';
import { PermitBatchReissueSubmitGuard } from './submit/permit-batch-reissue-submit.guard';
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
    data: { accountType: 'INSTALLATION' },
    canActivate: [BatchReissueSubmitGuard],
    canDeactivate: [PermitBatchReissueSubmitGuard],
    children: [
      {
        path: 'filters',
        data: { pageTitle: 'Select filters - Permit batch reissue' },
        component: FiltersComponent,
      },
      {
        path: 'signatory',
        data: {
          backlink: '../filters',
          pageTitle: 'Select name and signature for official notice - Permit batch reissue',
        },
        resolve: { regulators: RegulatorsGuard },
        canActivate: [SignatoryGuard],
        component: SignatoryComponent,
      },
      {
        path: 'summary',
        data: { pageTitle: 'Confirm details - Permit batch reissue' },
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
        loadChildren: () => import('../workflow-item/workflow-item.module').then((m) => m.WorkflowItemModule),
      },
      {
        path: 'actions',
        loadChildren: () => import('../actions/actions.module').then((m) => m.ActionsModule),
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PermitBatchReissueRoutingModule {}
