import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';
import { AerSharedModule } from '@tasks/aer/shared/aer-shared.module';

import { ActivityCalculationAggregationComponent } from './activity-calculation/activity-calculation-aggregation/activity-calculation-aggregation.component';
import { ActivityCalculationContinuousComponent } from './activity-calculation/activity-calculation-continuous/activity-calculation-continuous.component';
import { ActivityCalculationMethodComponent } from './activity-calculation-method/activity-calculation-method.component';
import { BiomassCalculationComponent } from './biomass-calculation/biomass-calculation.component';
import { CalculationEmissionsComponent } from './calculation-emissions.component';
import { CalculationEmissionsRoutingModule } from './calculation-emissions-routing.module';
import { CalculationMethodComponent } from './calculation-method/calculation-method.component';
import { InventoryDataReviewComponent } from './calculation-review/inventory-data-review/inventory-data-review.component';
import { ManualDataReviewComponent } from './calculation-review/manual-data-review/manual-data-review.component';
import { ConditionsMeteredComponent } from './conditions-metered/conditions-metered.component';
import { DeliveryZoneComponent } from './delivery-zone/delivery-zone.component';
import { EmissionNetworkComponent } from './emission-network/emission-network.component';
import { InstallationPostcodeComponent } from './installation-postcode/installation-postcode.component';
import { ManualCalculationValuesComponent } from './manual-calculation-values/manual-calculation-values.component';
import { RelevantCategoryComponent } from './relevant-category/relevant-category.component';
import { SummaryComponent } from './summary/summary.component';
import { TiersUsedComponent } from './tiers-used/tiers-used.component';
import { TransferredComponent } from './transferred/transferred.component';
import { TransferredDetailsComponent } from './transferred-details/transferred-details.component';

@NgModule({
  declarations: [
    ActivityCalculationAggregationComponent,
    ActivityCalculationContinuousComponent,
    ActivityCalculationMethodComponent,
    BiomassCalculationComponent,
    CalculationEmissionsComponent,
    CalculationMethodComponent,
    ConditionsMeteredComponent,
    DeliveryZoneComponent,
    EmissionNetworkComponent,
    InstallationPostcodeComponent,
    InventoryDataReviewComponent,
    ManualCalculationValuesComponent,
    ManualDataReviewComponent,
    RelevantCategoryComponent,
    SummaryComponent,
    TiersUsedComponent,
    TransferredComponent,
    TransferredDetailsComponent,
  ],
  imports: [AerSharedModule, CalculationEmissionsRoutingModule, SharedModule],
})
export class CalculationEmissionsModule {}
