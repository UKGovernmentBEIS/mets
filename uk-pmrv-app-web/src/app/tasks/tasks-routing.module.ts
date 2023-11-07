import { inject, NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { FileDownloadComponent } from '@shared/file-download/file-download.component';
import { TaskTypeToBreadcrumbPipe } from '@shared/pipes/task-type-to-breadcrumb.pipe';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { TaskComponent } from './task.component';
import { TaskGuard } from './task.guard';

const routes: Routes = [
  { path: '', pathMatch: 'full', component: TaskComponent },
  {
    path: ':taskId',
    data: { breadcrumb: { resolveText: ({ breadcrumbText }) => breadcrumbText, skipLink: true } },
    resolve: {
      breadcrumbText: () => {
        const store = inject(CommonTasksStore);
        const breadcrumbTextPipe = new TaskTypeToBreadcrumbPipe();
        const type = store.getState().requestTaskItem.requestTask.type;
        return breadcrumbTextPipe.transform(type);
      },
    },
    component: TaskComponent,
    canActivate: [TaskGuard],
    canDeactivate: [TaskGuard],
    children: [
      {
        path: 'permit-notification',
        loadChildren: () =>
          import('./permit-notification/permit-notification.module').then((m) => m.PermitNotificationModule),
      },
      {
        path: 'permit-transfer-a',
        loadChildren: () => import('./permit-transfer-a/permit-transfer-a.module').then((m) => m.PermitTransferAModule),
      },
      {
        path: 'aer',
        loadChildren: () => import('./aer/aer.module').then((m) => m.AerModule),
      },
      {
        path: 'vir',
        children: [
          {
            path: 'submit',
            loadChildren: () => import('./vir/submit/vir-task-submit.module').then((m) => m.VirTaskSubmitModule),
          },
          {
            path: 'review',
            loadChildren: () => import('./vir/review/vir-task-review.module').then((m) => m.VirTaskReviewModule),
          },
          {
            path: 'review-wait',
            loadChildren: () =>
              import('./vir/review-wait/vir-task-review-wait.module').then((m) => m.VirTaskReviewWaitModule),
          },
          {
            path: 'comments-response',
            loadChildren: () =>
              import('./vir/comments-response/vir-task-comments-response.module').then(
                (m) => m.VirTaskCommentsResponseModule,
              ),
          },
        ],
      },
      {
        path: 'air',
        children: [
          {
            path: 'submit',
            loadChildren: () => import('./air/submit/air-task-submit.module').then((m) => m.AirTaskSubmitModule),
          },
          {
            path: 'review',
            loadChildren: () => import('./air/review/air-task-review.module').then((m) => m.AirTaskReviewModule),
          },
          {
            path: 'review-wait',
            loadChildren: () =>
              import('./air/review-wait/air-task-review-wait.module').then((m) => m.AirTaskReviewWaitModule),
          },
          {
            path: 'comments-response',
            loadChildren: () =>
              import('./air/comments-response/air-task-comments-response.module').then(
                (m) => m.AirTaskCommentsResponseModule,
              ),
          },
        ],
      },
      {
        path: 'dre',
        loadChildren: () => import('./dre/dre.module').then((m) => m.DreModule),
      },
      {
        path: 'doal',
        loadChildren: () => import('./doal/doal.module').then((m) => m.DoalModule),
      },
      {
        path: 'non-compliance',
        loadChildren: () => import('./non-compliance/non-compliance.module').then((m) => m.NonComplianceModule),
      },
      {
        path: 'payment',
        loadChildren: () => import('../payment/payment.module').then((m) => m.PaymentModule),
      },
      {
        path: 'rfi',
        loadChildren: () => import('../rfi/rfi.module').then((m) => m.RfiModule),
      },
      {
        path: 'rde',
        loadChildren: () => import('../rde/rde.module').then((m) => m.RdeModule),
      },
      {
        path: 'change-assignee',
        data: { pageTitle: 'Change assignee', backlink: '../' },
        loadChildren: () =>
          import('../change-task-assignee/change-task-assignee.module').then(m => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'withholding-allowances',
        loadChildren: () =>
          import('./withholding-allowances/withholding-allowances.module').then((m) => m.WithholdingAllowancesModule),
      },
      {
        path: 'return-of-allowances',
        loadChildren: () =>
          import('./return-of-allowances/return-of-allowances.module').then((m) => m.ReturnOfAllowancesModule),
      },
      {
        path: 'cancel',
        loadChildren: () => import('../cancel-task/cancel-task.module').then(m => m.CancelTaskModule),
      },
      {
        path: 'file-download/:uuid',
        component: FileDownloadComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TasksRoutingModule {}
