import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { TaskTypeToBreadcrumbPipe } from '@shared/pipes/task-type-to-breadcrumb.pipe';
import { SharedModule } from '@shared/shared.module';
import { HseTiTaskComponent } from '@tasks/hseti/shared/components/hseti-task/hseti-task.component';

import { HseTiService } from '../core/hseti.service';
import { HsetiTaskReviewComponent } from './components/hseti-review-task/hseti-review-task.component';
import { OverallDecisionSummaryTemplateComponent } from './components/overall-decision-summary-template/overall-decision-summary-template.component';
import { PageTitleResolver } from './resolvers/page-title.resolver';

@NgModule({
  exports: [HseTiTaskComponent, HsetiTaskReviewComponent, OverallDecisionSummaryTemplateComponent],
  imports: [
    HseTiTaskComponent,
    HsetiTaskReviewComponent,
    OverallDecisionSummaryTemplateComponent,
    RouterModule,
    SharedModule,
  ],
  providers: [CapitalizeFirstPipe, HseTiService, ItemNamePipe, PageTitleResolver, TaskTypeToBreadcrumbPipe],
})
export class HseTiTaskSharedModule {}
