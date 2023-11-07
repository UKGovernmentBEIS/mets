import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { DeleteComponent } from './delete/delete.component';
import { DetailsComponent } from './details/details.component';
import { DirectionComponent } from './direction/direction.component';
import { EmissionsComponent } from './emissions/emissions.component';
import { InherentCO2Component } from './inherent-co2.component';
import { InherentCo2RoutingModule } from './inherent-co2-routing.module';
import { InstrumentsComponent } from './instruments/instruments.component';
import { InherentSummaryComponent } from './summary/inherent-summary.component';

@NgModule({
  declarations: [
    DeleteComponent,

    DetailsComponent,
    DirectionComponent,
    EmissionsComponent,
    InherentCO2Component,
    InherentSummaryComponent,
    InstrumentsComponent,
  ],
  imports: [InherentCo2RoutingModule, SharedModule, SharedPermitModule],
})
export class InherentCo2Module {}
