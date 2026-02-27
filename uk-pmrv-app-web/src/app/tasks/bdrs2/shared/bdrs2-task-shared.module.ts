import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { TaskTypeToBreadcrumbPipe } from '@shared/pipes/task-type-to-breadcrumb.pipe';
import { SharedModule } from '@shared/shared.module';

import { BdrS2Service } from '../core';
import { BdrS2ReviewGroupDecisionComponent } from './components';
import { BdrS2TaskComponent } from './components/bdrs2-task/bdrs2-task.component';
import { BdrS2TaskReviewComponent } from './components/bdrs2-task-review/bdrs2-task-review.component';
import { BDRS2ReturnLinkComponent } from './components/return-link/return-link.component';
import { TaskStatusPipe } from './pipes/task-status.pipe';

@NgModule({
  imports: [
    BDRS2ReturnLinkComponent,
    BdrS2ReviewGroupDecisionComponent,
    BdrS2TaskComponent,
    BdrS2TaskReviewComponent,
    RouterModule,
    SharedModule,
    TaskStatusPipe,
  ],
  providers: [BdrS2Service, CapitalizeFirstPipe, ItemNamePipe, TaskTypeToBreadcrumbPipe],
  exports: [
    BDRS2ReturnLinkComponent,
    BdrS2ReviewGroupDecisionComponent,
    BdrS2TaskComponent,
    BdrS2TaskReviewComponent,
    TaskStatusPipe,
  ],
})
export class BdrS2TaskSharedModule {}
