import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { ACCOUNT_TYPE } from '@core/providers';
import { WorkflowItemsService } from '@shared/dashboard';

import { AviationAccountsModule } from './accounts/aviation-accounts.module';
import { AviationRoutingModule } from './aviation-routing.module';

@NgModule({
  declarations: [],
  imports: [AviationAccountsModule, AviationRoutingModule, CommonModule],
  providers: [WorkflowItemsService, { provide: ACCOUNT_TYPE, useValue: 'AVIATION' }],
})
export class AviationModule {}
