import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { TaskTypeToBreadcrumbPipe } from '@shared/pipes/task-type-to-breadcrumb.pipe';
import { SharedModule } from '@shared/shared.module';

import { BdrTaskComponent } from './components/bdr-task/bdr-task.component';
import { BdrTaskReviewComponent } from './components/bdr-task-review/bdr-task-review.component';
import { BdrReviewGroupDecisionComponent } from './components/decision/bdr-review-group-decision/bdr-review-group-decision.component';
import { BDRReturnLinkComponent } from './components/return-link/return-link.component';
import { ReviewBdrGroupDecisionPipe } from './pipes';
import { TaskStatusPipe } from './pipes/task-status.pipe';
import { BdrService } from './services/bdr.service';

@NgModule({
  exports: [
    BDRReturnLinkComponent,
    BdrReviewGroupDecisionComponent,
    BdrTaskComponent,
    BdrTaskReviewComponent,
    ReviewBdrGroupDecisionPipe,
    TaskStatusPipe,
  ],
  imports: [
    BDRReturnLinkComponent,
    BdrReviewGroupDecisionComponent,
    BdrTaskComponent,
    BdrTaskReviewComponent,
    ReviewBdrGroupDecisionPipe,
    RouterModule,
    SharedModule,
    TaskStatusPipe,
  ],
  providers: [BdrService, CapitalizeFirstPipe, ItemNamePipe, TaskTypeToBreadcrumbPipe],
})
export class BdrTaskSharedModule {}
