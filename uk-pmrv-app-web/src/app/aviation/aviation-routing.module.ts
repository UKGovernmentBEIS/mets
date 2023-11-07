import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AviationAuthGuard } from '@core/guards/aviation-auth.guard';
import { DashboardPageComponent } from '@shared/dashboard';

import { RoleTypeGuard } from '../shared/guards/role-type.guard';

const routes: Routes = [
  {
    path: 'dashboard',
    data: { pageTitle: 'UK Emissions Trading Scheme reporting dashboard' },
    component: DashboardPageComponent,
  },
  {
    path: 'tasks',
    loadChildren: () => import('./request-task/request-task.module').then((m) => m.RequestTaskModule),
  },
  {
    path: 'actions',
    loadChildren: () => import('./request-action/request-action.module').then((m) => m.RequestActionModule),
  },
  {
    path: 'payment',
    loadChildren: () => import('../payment/payment.module').then((m) => m.PaymentModule),
  },
  {
    path: 'accounts',
    loadChildren: () => import('./accounts/aviation-accounts.module').then((m) => m.AviationAccountsModule),
  },
  {
    path: 'mi-reports',
    data: { breadcrumb: 'MI Reports' },
    loadChildren: () => import('../mi-reports/mi-reports.module').then((m) => m.MiReportsModule),
  },
  {
    path: 'templates',
    data: { breadcrumb: 'Templates' },
    loadChildren: () => import('../templates/templates.module').then((m) => m.TemplatesModule),
  },
  {
    path: 'user',
    canActivate: [AviationAuthGuard],
    children: [
      {
        path: 'regulators',
        data: { breadcrumb: 'Regulator users and contacts' },
        loadChildren: () => import('../regulators/regulators.module').then((m) => m.RegulatorsModule),
      },
      {
        path: 'verifiers',
        data: { breadcrumb: 'Verifiers' },
        loadChildren: () => import('../verifiers/verifiers.module').then((m) => m.VerifiersModule),
      },
    ],
  },
  {
    path: 'verification-bodies',
    canActivate: [AviationAuthGuard],
    data: { pageTitle: 'Manage verification bodies', breadcrumb: true },
    loadChildren: () =>
      import('../verification-bodies/verification-bodies.module').then((m) => m.VerificationBodiesModule),
  },
  {
    path: 'rfi',
    canActivate: [AviationAuthGuard],
    loadChildren: () => import('../rfi/rfi.module').then((m) => m.RfiModule),
  },
  {
    path: 'rde',
    canActivate: [AviationAuthGuard],
    loadChildren: () => import('../rde/rde.module').then((m) => m.RdeModule),
  },
  {
    path: 'workflows',
    canActivate: [AviationAuthGuard],
    children: [
      {
        path: 'batch-variations',
        data: {
          roleTypeGuards: 'REGULATOR',
          requestType: 'batch-variation',
          requestTypeUrlPath: 'batch-variations',
          breadcrumb: 'Batch variations',
        },
        canActivate: [RoleTypeGuard],
        loadChildren: () =>
          import('./workflows/emp-batch-reissue/emp-batch-reissue.module').then((m) => m.EmpBatchReissueModule),
      },
    ],
  },
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full',
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AviationRoutingModule {}
