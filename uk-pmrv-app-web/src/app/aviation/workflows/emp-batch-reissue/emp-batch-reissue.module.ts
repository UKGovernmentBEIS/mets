import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { EmpBatchReissueFiltersTemplateComponent } from '@aviation/shared/components/emp-batch-reissue/filters-template/filters-template.component';
import { SharedModule } from '@shared/shared.module';

import { EmpBatchReissueRoutingModule } from './emp-batch-reissue-routing.module';
import { RequestsComponent } from './requests/requests.component';
import { FiltersComponent } from './submit/filters/filters.component';
import { SignatoryComponent } from './submit/signatory/signatory.component';
import { SummaryComponent } from './submit/summary/summary.component';

@NgModule({
  declarations: [FiltersComponent, RequestsComponent, SignatoryComponent, SummaryComponent],
  imports: [CommonModule, EmpBatchReissueFiltersTemplateComponent, EmpBatchReissueRoutingModule, SharedModule],
})
export class EmpBatchReissueModule {}
