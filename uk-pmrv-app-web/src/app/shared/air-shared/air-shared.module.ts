import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { AirImprovementItemComponent } from './components/air-improvement-item/air-improvement-item.component';
import { AirOperatorFollowupItemComponent } from './components/air-operator-followup-item/air-operator-followup-item.component';
import { AirOperatorResponseDataItemComponent } from './components/air-operator-response-data-item/air-operator-response-data-item.component';
import { AirOperatorResponseItemComponent } from './components/air-operator-response-item/air-operator-response-item.component';
import { AirRegulatorProvideSummaryComponent } from './components/air-regulator-provide-summary/air-regulator-provide-summary.component';
import { AirRegulatorResponseItemComponent } from './components/air-regulator-response-item/air-regulator-response-item.component';
import { AirImprovementTitlePipe } from './pipes/air-improvement-title.pipe';
import { OperatorAirResponseTypePipe } from './pipes/operator-air-response-type.pipe';

@NgModule({
  declarations: [
    AirImprovementItemComponent,
    AirImprovementTitlePipe,
    AirOperatorFollowupItemComponent,
    AirOperatorResponseDataItemComponent,
    AirOperatorResponseItemComponent,
    AirRegulatorProvideSummaryComponent,
    AirRegulatorResponseItemComponent,
    OperatorAirResponseTypePipe,
  ],
  exports: [
    AirImprovementItemComponent,
    AirImprovementTitlePipe,
    AirOperatorFollowupItemComponent,
    AirOperatorResponseDataItemComponent,
    AirOperatorResponseItemComponent,
    AirRegulatorProvideSummaryComponent,
    AirRegulatorResponseItemComponent,
    OperatorAirResponseTypePipe,
  ],
  imports: [RouterModule, SharedModule],
})
export class AirSharedModule {}
