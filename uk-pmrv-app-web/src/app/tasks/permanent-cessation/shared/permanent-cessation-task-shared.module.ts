import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { TaskTypeToBreadcrumbPipe } from '@shared/pipes/task-type-to-breadcrumb.pipe';
import { SharedModule } from '@shared/shared.module';

import { PermanentCessationService } from './services';

@NgModule({
  exports: [],
  imports: [RouterModule, SharedModule],
  providers: [ItemNamePipe, PermanentCessationService, TaskTypeToBreadcrumbPipe],
})
export class PermanentCessationTaskSharedModule {}
