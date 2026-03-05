import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';
import { AerSharedModule } from '@tasks/aer/shared/aer-shared.module';

import { DeleteComponent } from './delete/delete.component';
import { ListComponent } from './list/list.component';
import { RecommendedImprovementsComponent } from './recommended-improvements.component';
import { RecommendedImprovementsItemComponent } from './recommended-improvements-item.component';
import { RecommendedImprovementsRoutingModule } from './recommended-improvements-routing.module';
import { SummaryComponent } from './summary/summary.component';

@NgModule({
  declarations: [
    DeleteComponent,
    ListComponent,
    RecommendedImprovementsComponent,
    RecommendedImprovementsItemComponent,
    SummaryComponent,
  ],
  imports: [AerSharedModule, RecommendedImprovementsRoutingModule, SharedModule],
})
export class RecommendedImprovementsModule {}
