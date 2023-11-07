import { Location } from '@angular/common';
import { NgModule } from '@angular/core';
import { Router } from '@angular/router';

import { DeterminationGuard } from '@permit-application/review/determination/determination.guard';
import { ReviewModule } from '@permit-application/review/review.module';
import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { PermitApplicationStoreFactory } from '@permit-application/store/permit-store.provider';
import { SharedModule } from '@shared/shared.module';
import { StoreContextResolver } from '@shared/store-resolver/store-context.resolver';

import { PermitTransferRoutingModule } from './permit-transfer-routing.module';
import { TransferDecisionSummaryComponent } from './review/decision-summary/decision-summary.component';
import { ReviewSectionsContainerComponent } from './review/sections-container/review-sections-container.component';
import { TransferDetailsReviewComponent } from './review/transfer-details/transfer-details.component';
import { SectionsContainerComponent } from './sections-container/sections-container.component';
import { PermitTransferSummaryComponent } from './summary/summary.component';
import { TransferDetailsConfirmationSummaryComponent } from './transfer-details/summary/summary.component';
import { SummaryDetailsComponent } from './transfer-details/summary/summary-details/summary-details.component';
import { TransferDetailsComponent } from './transfer-details/transfer-details.component';

@NgModule({
  declarations: [
    PermitTransferSummaryComponent,
    ReviewSectionsContainerComponent,
    SectionsContainerComponent,
    SummaryDetailsComponent,
    TransferDecisionSummaryComponent,
    TransferDetailsComponent,
    TransferDetailsConfirmationSummaryComponent,
    TransferDetailsReviewComponent,
  ],
  imports: [PermitTransferRoutingModule, ReviewModule, SharedModule, SharedPermitModule],
  providers: [
    {
      provide: PermitApplicationStore,
      useFactory: PermitApplicationStoreFactory,
      deps: [StoreContextResolver, Location, Router],
    },
    DeterminationGuard,
  ],
})
export class PermitTransferModule {}
