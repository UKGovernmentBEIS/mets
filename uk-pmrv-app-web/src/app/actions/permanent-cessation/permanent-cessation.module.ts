import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { PermanentCessationActionSubmittedComponent } from './submitted/submitted.component';

@NgModule({
  imports: [CommonModule, PermanentCessationActionSubmittedComponent, SharedModule],
  providers: [],
})
export class InspectionModule {}
