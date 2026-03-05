import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../shared/shared.module';
import { AerSharedModule } from '../../shared/aer-shared.module';
import { CalculationReviewComponent } from './calculation-review/calculation-review.component';
import { EmissionNetworkComponent } from './emission-network/emission-network.component';
import { MethodsComponent } from './methods/methods.component';
import { PfcComponent } from './pfc.component';
import { PfcRoutingModule } from './pfc-routing.module';
import { PrimaryAluminiumComponent } from './primary-aluminium/primary-aluminium.component';
import { SummaryComponent } from './summary/summary.component';
import { TiersUsedComponent } from './tiers-used/tiers-used.component';

@NgModule({
  declarations: [
    CalculationReviewComponent,
    EmissionNetworkComponent,
    MethodsComponent,
    PfcComponent,
    PrimaryAluminiumComponent,
    SummaryComponent,
    TiersUsedComponent,
  ],
  imports: [AerSharedModule, PfcRoutingModule, SharedModule],
})
export class PfcModule {}
