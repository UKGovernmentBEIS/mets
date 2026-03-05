import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';
import { AerSharedModule } from '@tasks/aer/shared/aer-shared.module';
import { DeleteComponent } from '@tasks/aer/submit/inherent-co2/delete/delete.component';
import { DetailsComponent } from '@tasks/aer/submit/inherent-co2/details/details.component';
import { DirectionComponent } from '@tasks/aer/submit/inherent-co2/direction/direction.component';
import { EmissionsComponent } from '@tasks/aer/submit/inherent-co2/emissions/emissions.component';
import { InherentCo2Component } from '@tasks/aer/submit/inherent-co2/inherent-co2.component';
import { InherentCo2RoutingModule } from '@tasks/aer/submit/inherent-co2/inherent-co2-routing.module';
import { InstrumentsComponent } from '@tasks/aer/submit/inherent-co2/instruments/instruments.component';

@NgModule({
  declarations: [
    DeleteComponent,
    DetailsComponent,
    DirectionComponent,
    EmissionsComponent,
    InherentCo2Component,
    InstrumentsComponent,
  ],
  imports: [AerSharedModule, InherentCo2RoutingModule, SharedModule],
})
export class InherentCo2Module {}
