import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';
import { AerSharedModule } from '@tasks/aer/shared/aer-shared.module';
import { DescriptionComponent } from '@tasks/aer/submit/fallback/description/description.component';
import { FallbackComponent } from '@tasks/aer/submit/fallback/fallback.component';
import { FallbackRoutingModule } from '@tasks/aer/submit/fallback/fallback-routing.module';
import { SummaryComponent } from '@tasks/aer/submit/fallback/summary/summary.component';
import { TotalEmissionsComponent } from '@tasks/aer/submit/fallback/total-emissions/total-emissions.component';
import { UploadDocumentsComponent } from '@tasks/aer/submit/fallback/upload-documents/upload-documents.component';

@NgModule({
  declarations: [
    DescriptionComponent,
    FallbackComponent,
    SummaryComponent,
    TotalEmissionsComponent,
    UploadDocumentsComponent,
  ],
  imports: [AerSharedModule, FallbackRoutingModule, SharedModule],
})
export class FallbackModule {}
