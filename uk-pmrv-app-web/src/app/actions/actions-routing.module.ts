import { inject, NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { FileDownloadComponent } from '@shared/file-download/file-download.component';
import { ItemActionTypePipe } from '@shared/pipes/item-action-type.pipe';

import { ActionComponent } from './action.component';
import { ActionGuard } from './action.guard';
import { SubmittedComponent } from './permit-transfer-a/submitted/submitted.component';
import { CommonActionsStore } from './store/common-actions.store';

const routes: Routes = [
  { path: '', pathMatch: 'full', component: ActionComponent },
  {
    path: ':actionId',
    component: ActionComponent,
    data: {
      breadcrumb: {
        resolveText: ({ type }) => new ItemActionTypePipe().transform(type),
        skipLink: true,
      },
    },
    resolve: { type: () => inject(CommonActionsStore).requestActionType$ },
    canActivate: [ActionGuard],
    canDeactivate: [ActionGuard],
    children: [
      {
        path: 'permit-notification',
        loadChildren: () =>
          import('./permit-notification/permit-notification.module').then((m) => m.PermitNotificationModule),
      },
      {
        path: 'permit-transfer-a',
        children: [
          {
            path: 'submitted',
            data: { pageTitle: 'Full transfer of permit' },
            component: SubmittedComponent,
          },
        ],
      },
      {
        path: 'aer',
        loadChildren: () => import('./aer/aer.module').then((m) => m.AerModule),
      },
      {
        path: 'dre',
        loadChildren: () => import('./dre/dre.module').then((m) => m.DreModule),
      },
      {
        path: 'permit-batch-variation',
        loadChildren: () =>
          import('./permit-batch-reissue/permit-batch-reissue-actions.module').then(
            (m) => m.PermitBatchReissueActionsModule,
          ),
      },
      {
        path: 'variation',
        loadChildren: () => import('./reissue/reissue.module').then((m) => m.ReissueModule),
      },
      {
        path: 'non-compliance',
        loadChildren: () => import('./non-compliance/non-compliance.module').then((m) => m.NonComplianceModule),
      },
      {
        path: 'vir',
        children: [
          {
            path: 'submitted',
            loadChildren: () =>
              import('./vir/submitted/vir-action-submitted.module').then((m) => m.VirActionSubmittedModule),
          },
          {
            path: 'reviewed',
            loadChildren: () =>
              import('./vir/reviewed/vir-action-reviewed.module').then((m) => m.VirActionReviewedModule),
          },
          {
            path: 'responded',
            loadChildren: () =>
              import('./vir/responded/vir-action-responded.module').then((m) => m.VirActionRespondedModule),
          },
        ],
      },
      {
        path: 'air',
        children: [
          {
            path: 'submitted',
            loadChildren: () =>
              import('./air/submitted/air-action-submitted.module').then((m) => m.AirActionSubmittedModule),
          },
          {
            path: 'reviewed',
            loadChildren: () =>
              import('./air/reviewed/air-action-reviewed.module').then((m) => m.AirActionReviewedModule),
          },
          {
            path: 'responded',
            loadChildren: () =>
              import('./air/responded/air-action-responded.module').then((m) => m.AirActionRespondedModule),
          },
        ],
      },
      {
        path: 'withholding-allowances',
        loadChildren: () =>
          import('./withholding-allowances/withholding-allowances.module').then((m) => m.WithholdingAllowancesModule),
      },
      {
        path: 'doal',
        loadChildren: () => import('./doal/doal-action.module').then((m) => m.DoalActionModule),
      },
      {
        path: 'file-download/:fileType/:uuid',
        component: FileDownloadComponent,
      },
      {
        path: 'return-of-allowances',
        loadChildren: () =>
          import('./return-of-allowances/return-of-allowances.module').then((m) => m.ReturnOfAllowancesModule),
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ActionsRoutingModule {}
