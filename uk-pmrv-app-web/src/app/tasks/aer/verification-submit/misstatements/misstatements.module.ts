import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';
import { AerSharedModule } from '@tasks/aer/shared/aer-shared.module';
import { MisstatementsItemDeleteComponent } from '@tasks/aer/verification-submit/misstatements/delete/misstatements-item-delete.component';
import { MisstatementsListComponent } from '@tasks/aer/verification-submit/misstatements/list/misstatements-list.component';
import { MisstatementsComponent } from '@tasks/aer/verification-submit/misstatements/misstatements.component';
import { MisstatementsItemComponent } from '@tasks/aer/verification-submit/misstatements/misstatements-item.component';
import { MisstatementsRoutingModule } from '@tasks/aer/verification-submit/misstatements/misstatements-routing.module';
import { SummaryComponent } from '@tasks/aer/verification-submit/misstatements/summary/summary.component';

@NgModule({
  declarations: [
    MisstatementsComponent,
    MisstatementsItemComponent,
    MisstatementsItemDeleteComponent,
    MisstatementsListComponent,
    SummaryComponent,
  ],
  imports: [AerSharedModule, MisstatementsRoutingModule, SharedModule],
})
export class MisstatementsModule {}
