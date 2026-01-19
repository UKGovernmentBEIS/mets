import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import {
  QdrReviewComponent,
  WasteQdrCompleteTaskComponent,
  WasteQdrReturnForAmendsComponent,
} from '@tasks/waste-qdr/review';

import { SendReportGuard, wizardStepGuard } from './core';
import { WasteQdrSendReportComponent, WasteQdrTaskListComponent } from './shared';
import {
  WasteQdrChangesRequestedComponent,
  WasteQdrProvideQdrComponent,
  WasteQdrSummaryComponent,
  WasteQdrUploadQdrComponent,
} from './submit';

const routes: Routes = [
  {
    path: 'submit',
    children: [
      {
        path: '',
        data: { pageTitle: 'Complete quarterly data report' },
        component: WasteQdrTaskListComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'qdr',
        children: [
          {
            path: '',
            data: { pageTitle: 'Provide a quarterly data report' },
            component: WasteQdrProvideQdrComponent,
            canActivate: [wizardStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'upload',
            data: { pageTitle: 'Upload your quarterly report and supporting data', backlink: '../' },
            component: WasteQdrUploadQdrComponent,
            canActivate: [wizardStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Check your answers', breadcrumb: 'Quarterly data report - Summary' },
            component: WasteQdrSummaryComponent,
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
            providers: [SendReportGuard],
            canActivate: [SendReportGuard],
            canDeactivate: [PendingRequestGuard],
            data: { pageTitle: 'Send report to regulator' },
            component: WasteQdrSendReportComponent,
          },
        ],
      },
      {
        path: 'changes-requested',
        data: { pageTitle: 'Changes requested by the regulator', breadcrumb: true },
        component: WasteQdrChangesRequestedComponent,
      },
    ],
  },
  {
    path: 'review',
    children: [
      {
        path: '',
        data: { pageTitle: 'Review quarterly data report' },
        component: WasteQdrTaskListComponent,
      },
      {
        path: 'qdr',
        data: { pageTitle: 'Review quarterly data report', groupKey: 'qdr', breadcrumb: true },
        component: QdrReviewComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'complete-task',
        data: { pageTitle: 'Complete quarterly data report review', breadcrumb: true },
        canDeactivate: [PendingRequestGuard],
        component: WasteQdrCompleteTaskComponent,
      },
      {
        path: 'return-for-amends',
        data: { pageTitle: 'Return for amends', breadcrumb: true },
        component: WasteQdrReturnForAmendsComponent,
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class WasteQdrRoutingModule {}
