import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';
import { VirSharedModule } from '@shared/vir-shared/vir-shared.module';

import { ActionSharedModule } from '../../shared/action-shared-module';
import { SubmittedComponent } from './submitted.component';
import { SummaryComponent } from './summary/summary.component';
import { VirActionSubmittedRoutingModule } from './vir-action-submitted-routing.module';

@NgModule({
  imports: [ActionSharedModule, SharedModule, VirActionSubmittedRoutingModule, VirSharedModule],
  declarations: [SubmittedComponent, SummaryComponent],
})
export class VirActionSubmittedModule {}
