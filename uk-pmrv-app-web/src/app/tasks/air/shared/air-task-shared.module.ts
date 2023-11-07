import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AirSharedModule } from '@shared/air-shared/air-shared.module';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { TaskTypeToBreadcrumbPipe } from '@shared/pipes/task-type-to-breadcrumb.pipe';
import { SharedModule } from '@shared/shared.module';
import { AirImprovementDataGroupComponent } from '@tasks/air/shared/components/air-improvement-data-group/air-improvement-data-group.component';
import { AirImprovementDataGroupReviewComponent } from '@tasks/air/shared/components/air-improvement-data-group-review/air-improvement-data-group-review.component';
import { AirTaskComponent } from '@tasks/air/shared/components/air-task/air-task.component';
import { AirImprovementResponseGuard } from '@tasks/air/shared/guards/air-improvement-response.guard';
import { SubmitRespondStatusPipe } from '@tasks/air/shared/pipes/submit-respond-status.pipe';
import { TaskStatusPipe } from '@tasks/air/shared/pipes/task-status.pipe';
import { AirImprovementItemResolver } from '@tasks/air/shared/resolvers/air-improvement-item.resolver';
import { AirService } from '@tasks/air/shared/services/air.service';
import { AirImprovementResponseService } from '@tasks/air/shared/services/air-improvement-response.service';

@NgModule({
  declarations: [
    AirImprovementDataGroupComponent,
    AirImprovementDataGroupReviewComponent,
    AirTaskComponent,
    SubmitRespondStatusPipe,
    TaskStatusPipe,
  ],
  exports: [
    AirImprovementDataGroupComponent,
    AirImprovementDataGroupReviewComponent,
    AirTaskComponent,
    SubmitRespondStatusPipe,
    TaskStatusPipe,
  ],
  imports: [AirSharedModule, RouterModule, SharedModule],
  providers: [
    AirImprovementItemResolver,
    AirImprovementResponseGuard,
    AirImprovementResponseService,
    AirService,
    ItemNamePipe,
    TaskTypeToBreadcrumbPipe,
  ],
})
export class AirTaskSharedModule {}
