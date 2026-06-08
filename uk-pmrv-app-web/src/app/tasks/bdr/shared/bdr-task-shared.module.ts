import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { TaskTypeToBreadcrumbPipe } from '@shared/pipes/task-type-to-breadcrumb.pipe';
import { SharedModule } from '@shared/shared.module';

import { BdrTaskComponent } from './components/bdr-task/bdr-task.component';
import { BdrTaskReviewComponent } from './components/bdr-task-review/bdr-task-review.component';
import { BDRReturnLinkComponent } from './components/return-link/return-link.component';
import { TaskStatusPipe } from './pipes/task-status.pipe';
import { BdrService } from './services/bdr.service';

@NgModule({
  imports: [
    BDRReturnLinkComponent,
    BdrTaskComponent,
    BdrTaskReviewComponent,
    RouterModule,
    SharedModule,
    TaskStatusPipe,
  ],
  providers: [BdrService, CapitalizeFirstPipe, ItemNamePipe, TaskTypeToBreadcrumbPipe],
  exports: [BDRReturnLinkComponent, BdrTaskComponent, BdrTaskReviewComponent, TaskStatusPipe],
})
export class BdrTaskSharedModule {}
