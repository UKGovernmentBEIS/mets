import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';

import { nerSendReportGuard, wizardStepGuard } from './core';

const routes: Routes = [
  {
    path: 'submit',
    children: [
      {
        path: '',
        data: { pageTitle: 'Complete new entrant reserve' },
        loadComponent: () => import('./shared').then((c) => c.NerTaskListComponent),
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'details',
        children: [
          {
            path: '',
            data: { pageTitle: 'Upload new entrant reserve' },
            loadComponent: () => import('./submit').then((c) => c.NerDetailsUploadNerComponent),
            canActivate: [wizardStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'upload-mmp',
            data: { pageTitle: 'Upload monitoring methodology plan', backlink: '../' },
            loadComponent: () => import('./submit').then((c) => c.NerDetailsUploadMmpComponent),
            canActivate: [wizardStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Check your answers', breadcrumb: 'New entrant reserve - Summary' },
            loadComponent: () => import('./submit').then((c) => c.NerDetailsSummaryComponent),
            canActivate: [wizardStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'send-report',
        children: [
          {
            path: '',
            canActivate: [nerSendReportGuard],
            loadComponent: () => import('./shared').then((c) => c.NerSendReportComponent),
          },
        ],
      },
    ],
  },
  {
    path: 'verification-submit',
    children: [
      {
        path: '',
        data: { pageTitle: 'Verify new entrant reserve' },
        loadComponent: () => import('./shared').then((c) => c.NerTaskListComponent),
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'details',
        data: { pageTitle: 'New entrant reserve', breadcrumb: true },
        loadComponent: () => import('./verification-submit').then((c) => c.NerDetailsVerificationComponent),
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class NerRoutingModule {}
