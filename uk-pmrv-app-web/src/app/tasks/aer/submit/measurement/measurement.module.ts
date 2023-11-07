import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../shared/shared.module';
import { AerSharedModule } from '../../shared/aer-shared.module';
import { BiomassCalculationComponent } from './biomass-calculation/biomass-calculation.component';
import { CalculationReviewComponent } from './calculation-review/calculation-review.component';
import { DateRangeComponent } from './date-range/date-range.component';
import { DeleteComponent } from './delete/delete.component';
import { EmissionNetworkComponent } from './emission-network/emission-network.component';
import { GasFlowComponent } from './gas-flow/gas-flow.component';
import { GhgConcentrationComponent } from './ghg-concentration/ghg-concentration.component';
import { MeasurementComponent } from './measurement.component';
import { MeasurementRoutingModule } from './measurement-routing.module';
import { OperationHoursComponent } from './operation-hours/operation-hours.component';
import { SummaryComponent } from './summary/summary.component';
import { TiersReasonComponent } from './tiers-reason/tiers-reason.component';
import { TiersUsedComponent } from './tiers-used/tiers-used.component';
import { TransferredComponent } from './transferred/transferred.component';
import { TransferredDetailsComponent } from './transferred-details/transferred-details.component';

@NgModule({
  declarations: [
    BiomassCalculationComponent,
    CalculationReviewComponent,
    DateRangeComponent,
    DeleteComponent,
    EmissionNetworkComponent,
    GasFlowComponent,
    GhgConcentrationComponent,
    MeasurementComponent,
    OperationHoursComponent,
    SummaryComponent,
    TiersReasonComponent,
    TiersUsedComponent,
    TransferredComponent,
    TransferredDetailsComponent,
  ],
  imports: [AerSharedModule, CommonModule, MeasurementRoutingModule, SharedModule],
})
export class MeasurementModule {}
