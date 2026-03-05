import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';
import { AerSharedModule } from '@tasks/aer/shared/aer-shared.module';
import { DeleteComponent } from '@tasks/aer/verification-submit/non-compliances/delete/delete.component';
import { ListComponent } from '@tasks/aer/verification-submit/non-compliances/list/list.component';
import { NonCompliancesComponent } from '@tasks/aer/verification-submit/non-compliances/non-compliances.component';
import { NonCompliancesItemComponent } from '@tasks/aer/verification-submit/non-compliances/non-compliances-item.component';
import { NonCompliancesRoutingModule } from '@tasks/aer/verification-submit/non-compliances/non-compliances-routing.module';
import { SummaryComponent } from '@tasks/aer/verification-submit/non-compliances/summary/summary.component';

@NgModule({
  declarations: [
    DeleteComponent,
    ListComponent,
    NonCompliancesComponent,
    NonCompliancesItemComponent,
    SummaryComponent,
  ],
  imports: [AerSharedModule, NonCompliancesRoutingModule, SharedModule],
})
export class NonCompliancesModule {}
