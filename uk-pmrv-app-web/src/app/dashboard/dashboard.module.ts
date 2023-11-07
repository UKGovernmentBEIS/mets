import { NgModule } from '@angular/core';

import { ACCOUNT_TYPE } from '@core/providers';
import { WorkflowItemsService } from '@shared/dashboard';

import { SharedModule } from '../shared/shared.module';
import { DashboardRoutingModule } from './dashboard-routing.module';

@NgModule({
  imports: [DashboardRoutingModule, SharedModule],
  providers: [WorkflowItemsService, { provide: ACCOUNT_TYPE, useValue: 'INSTALLATION' }],
})
export class DashboardModule {}
