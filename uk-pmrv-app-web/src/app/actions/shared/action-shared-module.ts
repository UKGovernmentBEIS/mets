import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { ActionTaskComponent } from './action-task/action-task.component';
import {
  ActionLayoutComponent,
  BaseActionContainerComponent,
  RecipientsTemplateComponent,
  ReviewGroupDecisionSummaryComponent,
} from './components';

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
    RecipientsTemplateComponent,
    ReviewGroupDecisionSummaryComponent,
  ],
  imports: [RecipientsTemplateComponent, RouterModule, SharedModule],
})
export class ActionSharedModule {}
