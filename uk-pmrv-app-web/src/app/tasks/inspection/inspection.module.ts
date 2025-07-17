import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PeerReviewDecisionGuard } from '@shared/components/peer-review-decision/peer-review-decision.guard';
import { TaskTypeToBreadcrumbPipe } from '@shared/pipes/task-type-to-breadcrumb.pipe';
import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { InspectionRoutingModule } from './inspection-routing.module';
import { FollowUpRespondSummaryGuard } from './respond/follow-up-response-summary/follow-up-summary.guard';
import { FollowUpItemComponent } from './shared/follow-up-item/follow-up-item.component';
import { InspectionTaskComponent } from './shared/inspection-task/inspection-task.component';
import { FollowUpActionTypePipe } from './shared/pipes/follow-up-action-type.pipe';
import { InspectionItemResolver } from './shared/resolvers/follow-up-action.resolver';
import { TaskListSubmitContainerComponent } from './shared/task-list-submit/task-list-submit-container.component';
import { FollowUpActionSubmitComponent } from './submit/follow-up-action/follow-up-action.component';
import { FollowUpActionsComponent } from './submit/follow-up-actions/follow-up-actions.component';
import { FollowUpSubmitSummaryGuard } from './submit/follow-up-summary/follow-up-summary.guard';
import { ResponseDeadlineComponent } from './submit/response-deadline/response-deadline.component';

@NgModule({
  imports: [
    CommonModule,
    FollowUpActionsComponent,
    FollowUpActionSubmitComponent,
    FollowUpActionTypePipe,
    FollowUpItemComponent,
    InspectionRoutingModule,
    InspectionTaskComponent,
    ResponseDeadlineComponent,
    ReturnToLinkComponent,
    SharedModule,
    TaskListSubmitContainerComponent,
    TaskSharedModule,
  ],
  providers: [
    FollowUpRespondSummaryGuard,
    FollowUpSubmitSummaryGuard,
    InspectionItemResolver,
    PeerReviewDecisionGuard,
    TaskTypeToBreadcrumbPipe,
  ],
})
export class InspectionModule {}
