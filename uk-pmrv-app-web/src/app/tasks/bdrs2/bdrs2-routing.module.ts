import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DetailsComponent, FreeAllocationComponent, SubmitComponent } from './submit';
import { CBAMComponent } from './submit/cbam/cbam.component';
import { BDRS2UploadMmpComponent } from './submit/upload-mmp/bdrs2-upload-mmp.component';
import { BDRS2UploadReportComponent } from './submit/upload-report/bdrs2-upload-report.component';
import { bdrs2UploadFileBacklinkResolver } from './utils';

const routes: Routes = [
  {
    path: 'submit',
    children: [
      {
        path: '',
        data: { pageTitle: 'Stage 2 baseline data report`;' },
        component: SubmitComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'baseline',
        children: [
          {
            path: '',
            data: { pageTitle: 'Do you want to continue with your application for free allocation?' },
            component: FreeAllocationComponent,
          },
          {
            path: 'details',
            data: { pageTitle: 'Free allocation and emitter status', backlink: '../' },
            component: DetailsComponent,
          },
          {
            path: 'cbam',
            data: { pageTitle: 'UK Carbon Border Adjustment Mechanism (CBAM)', backlink: '../details' },
            component: CBAMComponent,
          },
          {
            path: 'upload-report',
            resolve: { backlink: bdrs2UploadFileBacklinkResolver },
            data: { pageTitle: 'Upload stage 2 baseline data report' },
            component: BDRS2UploadReportComponent,
          },
          {
            path: 'upload-mmp',
            data: { pageTitle: 'Upload monitoring methodology plan', backlink: '../upload-report' },
            component: BDRS2UploadMmpComponent,
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
export class BdrS2RoutingModule {}
