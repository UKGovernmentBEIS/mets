import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { FileDownloadComponent } from '@shared/file-download/file-download.component';

import { AlrBulkDownloadsComponent } from './alr/alr.component';
import { Bdrs2BulkDownloadsComponent } from './bdrs2/bdrs2.component';
import { BulkDownloadsComponent } from './bulk-downloads.component';

const routes: Routes = [
  {
    path: '',
    component: BulkDownloadsComponent,
  },
  {
    path: 'alr',
    data: { pageTitle: 'Activity Level Report', breadcrumb: true },
    component: AlrBulkDownloadsComponent,
  },
  {
    path: 'bdrs2',
    data: { pageTitle: 'Stage 2 baseline data report', breadcrumb: true },
    component: Bdrs2BulkDownloadsComponent,
  },
  {
    path: ':fileType/:workflow/:period',
    component: FileDownloadComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class BulkDownloadsRoutingModule {}
