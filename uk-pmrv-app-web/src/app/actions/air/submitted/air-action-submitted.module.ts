import { NgModule } from '@angular/core';

import { AirSharedModule } from '@shared/air-shared/air-shared.module';
import { AirImprovementTitlePipe } from '@shared/air-shared/pipes/air-improvement-title.pipe';
import { SharedModule } from '@shared/shared.module';

import { ActionSharedModule } from '../../shared/action-shared-module';
import { AirActionSubmittedRoutingModule } from './air-action-submitted-routing.module';
import { SubmittedComponent } from './submitted.component';
import { SummaryComponent } from './summary/summary.component';

@NgModule({
  imports: [ActionSharedModule, AirActionSubmittedRoutingModule, AirSharedModule, SharedModule],
  declarations: [SubmittedComponent, SummaryComponent],
  providers: [AirImprovementTitlePipe],
})
export class AirActionSubmittedModule {}
