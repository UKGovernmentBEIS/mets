import { inject, NgModule } from '@angular/core';
import { CanDeactivateFn, RouterModule, Routes } from '@angular/router';

import { RequestActionPageComponent } from '@aviation/request-action/containers';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { FileDownloadComponent } from '@shared/file-download/file-download.component';
import { ItemActionTypePipe } from '@shared/pipes/item-action-type.pipe';

import { DecisionSummaryComponent } from './emp/decision-summary/decision-summary.component';
import { canActivateRequestAction, canDeactivateRequestAction } from './guards/request-action.guards';
import { RequestActionStore } from './store';

const canDeactivateRequestActionDelegates: CanDeactivateFn<any> = () => {
  inject(RequestActionStore).destroyDelegates();
  return true;
};

const routes: Routes = [
  {
    path: '',
    providers: [{ provide: TYPE_AWARE_STORE, useExisting: RequestActionStore }],
    children: [
      {
        path: ':actionId',
        canActivate: [canActivateRequestAction],
        canDeactivate: [canDeactivateRequestAction, canDeactivateRequestActionDelegates],
        data: { breadcrumb: ({ type }) => new ItemActionTypePipe().transform(type) },
        resolve: { type: () => inject(RequestActionStore).type$ },
        children: [
          {
            path: 'file-download/:fileType/:uuid',
            component: FileDownloadComponent,
          },
          {
            path: 'emp',
            loadChildren: () => import('./emp/ukets/emp.routes').then((r) => r.EMP_ROUTES),
          },
          {
            path: 'emp-corsia',
            loadChildren: () => import('./emp/corsia/emp.routes').then((r) => r.EMP_ROUTES),
          },
          {
            path: 'non-compliance',
            loadChildren: () => import('./non-compliance/non-compliance.routes').then((r) => r.NON_COMPLIANCE_ROUTES),
          },
          {
            path: 'decision-summary',
            component: DecisionSummaryComponent,
          },
          {
            path: 'aer',
            loadChildren: () => import('./aer/ukets/aer-ukets.routes').then((r) => r.AER_UKETS_ROUTES),
          },
          {
            path: 'aer-corsia',
            loadChildren: () => import('./aer/corsia/aer-corsia.routes').then((r) => r.AER_CORSIA_ROUTES),
          },
          {
            path: 'vir',
            loadChildren: () => import('./vir/vir.routes').then((r) => r.VIR_ROUTES),
          },
          {
            path: '',
            component: RequestActionPageComponent,
          },
        ],
      },
      {
        path: '',
        pathMatch: 'full',
        redirectTo: '/aviation/dashboard',
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class RequestActionRoutingModule {}
