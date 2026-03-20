import { NgModule } from '@angular/core';

import { SharedModule } from '../shared/shared.module';
import { SharedUserModule } from '../shared-user/shared-user.module';
import { PermitBatchReissueRoutingModule } from './permit-batch-reissue-routing.module';
import { RequestsComponent } from './requests/requests.component';
import { FiltersComponent } from './submit/filters/filters.component';
import { SignatoryComponent } from './submit/signatory/signatory.component';
import { SummaryComponent } from './submit/summary/summary.component';

@NgModule({
  imports: [PermitBatchReissueRoutingModule, SharedModule, SharedUserModule],
  declarations: [FiltersComponent, RequestsComponent, SignatoryComponent, SummaryComponent],
})
export class PermitBatchReissueModule {}
