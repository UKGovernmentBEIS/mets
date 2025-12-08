import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { wizardStepGuard } from './core';
import { WasteQdrTaskListComponent } from './shared';
import { WasteQdrProvideQdrComponent, WasteQdrSummaryComponent, WasteQdrUploadQdrComponent } from './submit';

const routes: Routes = [
  {
    path: 'submit',
    children: [
      {
        path: '',
        data: { pageTitle: 'Complete emission factors report' },
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
          },
          {
            path: 'upload',
            data: { pageTitle: 'Upload your quarterly report and supporting data', backlink: '../' },
            component: WasteQdrUploadQdrComponent,
            canActivate: [wizardStepGuard],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Check your answers', breadcrumb: 'Quarterly data report - Summary' },
            component: WasteQdrSummaryComponent,
            canActivate: [wizardStepGuard],
          },
        ],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class WasteQdrRoutingModule {}
