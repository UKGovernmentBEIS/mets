import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { ActionTaskComponent } from './action-task/action-task.component';
import { ActionLayoutComponent } from './components/action-layout/action-layout.component';
import { BaseActionContainerComponent } from './components/base-task-container-component/base-action-container.component';
import { ReviewGroupDecisionSummaryComponent } from './components/review-group-decision-summary/review-group-decision-summary.component';

@NgModule({
  declarations: [
    ActionLayoutComponent,
    ActionTaskComponent,
    BaseActionContainerComponent,
    ReviewGroupDecisionSummaryComponent,
  ],
  exports: [
    ActionLayoutComponent,
    ActionTaskComponent,
    BaseActionContainerComponent,
    ReviewGroupDecisionSummaryComponent,
  ],
  imports: [RouterModule, SharedModule],
})
export class ActionSharedModule {}
