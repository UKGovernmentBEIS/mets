import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { FileDownloadComponent } from '@shared/file-download/file-download.component';

import { AlrBulkDownloadsComponent } from './alr/alr.component';
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
    path: ':fileType/:workflow/:period',
    component: FileDownloadComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class BulkDownloadsRoutingModule {}
