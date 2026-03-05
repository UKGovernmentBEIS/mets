import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';
import { AerSharedModule } from '@tasks/aer/shared/aer-shared.module';
import { NonConformitiesRoutingModule } from '@tasks/aer/verification-submit/non-conformities/non-conformities-routing.module';
import { DeleteComponent as PerPlanDeleteComponent } from '@tasks/aer/verification-submit/non-conformities/per-plan/delete/delete.component';
import { ListComponent as PerPlanListComponent } from '@tasks/aer/verification-submit/non-conformities/per-plan/list/list.component';
import { PerPlanComponent } from '@tasks/aer/verification-submit/non-conformities/per-plan/per-plan.component';
import { PerPlanItemComponent } from '@tasks/aer/verification-submit/non-conformities/per-plan/per-plan-item.component';
import { DeleteComponent as PreviousYearDeleteComponent } from '@tasks/aer/verification-submit/non-conformities/previous-year/delete/delete.component';
import { ListComponent as PreviousYearListComponent } from '@tasks/aer/verification-submit/non-conformities/previous-year/list/list.component';
import { PreviousYearComponent } from '@tasks/aer/verification-submit/non-conformities/previous-year/previous-year.component';
import { PreviousYearItemComponent } from '@tasks/aer/verification-submit/non-conformities/previous-year/previous-year-item.component';

import { SummaryComponent } from './summary/summary.component';

@NgModule({
  declarations: [
    PerPlanComponent,
    PerPlanDeleteComponent,
    PerPlanItemComponent,
    PerPlanListComponent,
    PreviousYearComponent,
    PreviousYearDeleteComponent,
    PreviousYearItemComponent,
    PreviousYearListComponent,
    SummaryComponent,
  ],
  imports: [AerSharedModule, NonConformitiesRoutingModule, SharedModule],
})
export class NonConformitiesModule {}
