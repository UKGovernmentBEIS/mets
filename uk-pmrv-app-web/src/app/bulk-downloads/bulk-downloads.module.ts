import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { BulkDownloadsRoutingModule } from './bulk-downloads-routing.module';
import { BulkDownloadsService } from './core/bulk-downloads.service';

@NgModule({
  declarations: [],
  imports: [BulkDownloadsRoutingModule, CommonModule, RouterModule, SharedModule],
  providers: [BulkDownloadsService],
})
export class BulkDownloadsModule {}
