import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { TaskTypeToBreadcrumbPipe } from '@shared/pipes/task-type-to-breadcrumb.pipe';
import { SharedModule } from '@shared/shared.module';

import { AlrService } from '../core';
import { ALRReturnLinkComponent, AlrReviewGroupDecisionComponent, AlrTaskComponent } from './components';
import { AlrTaskReviewComponent } from './components/alr-task-review/alr-task-review.component';

@NgModule({
  exports: [ALRReturnLinkComponent, AlrReviewGroupDecisionComponent, AlrTaskComponent, AlrTaskReviewComponent],
  imports: [
    ALRReturnLinkComponent,
    AlrReviewGroupDecisionComponent,
    AlrTaskComponent,
    AlrTaskReviewComponent,
    RouterModule,
    SharedModule,
  ],
  providers: [AlrService, CapitalizeFirstPipe, ItemNamePipe, TaskTypeToBreadcrumbPipe],
})
export class AlrTaskSharedModule {}
