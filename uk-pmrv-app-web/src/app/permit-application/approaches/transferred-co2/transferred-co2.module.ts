import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { DeductionsToAmountComponent } from './deductions-to-amount/deductions-to-amount.component';
import { SummaryComponent as DeductionsToAmountSummaryComponent } from './deductions-to-amount/summary/summary.component';
import { AnswersComponent } from './pipeline/answers/answers.component';
import { LeakageComponent } from './pipeline/leakage/leakage.component';
import { PipelineComponent } from './pipeline/pipeline.component';
import { SummaryComponent as PipelineSummaryComponent } from './pipeline/summary/summary.component';
import { TemperatureComponent } from './pipeline/temperature/temperature.component';
import { TransferCo2Component } from './pipeline/transfer-co2/transfer-co2.component';
import { StorageComponent } from './storage/storage.component';
import { SummaryComponent as StorageSummaryComponent } from './storage/summary/summary.component';
import { TransferredCO2Component } from './transferred-co2.component';
import { TransferredCO2RoutingModule } from './transferred-co2-routing.module';
import { SummaryComponent as TransportApproachSummaryComponent } from './transport-approach/summary/summary.component';
import { TransportApproachComponent } from './transport-approach/transport-approach.component';

@NgModule({
  declarations: [
    AnswersComponent,
    DeductionsToAmountComponent,
    DeductionsToAmountSummaryComponent,
    LeakageComponent,
    PipelineComponent,
    PipelineSummaryComponent,
    StorageComponent,
    StorageSummaryComponent,
    TemperatureComponent,
    TransferCo2Component,
    TransferredCO2Component,
    TransportApproachComponent,
    TransportApproachSummaryComponent,
  ],
  imports: [SharedModule, SharedPermitModule, TransferredCO2RoutingModule],
})
export class TransferredCO2Module {}
