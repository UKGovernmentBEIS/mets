import { Location } from '@angular/common';
import { NgModule } from '@angular/core';
import { Router } from '@angular/router';

import { DeterminationGuard } from '../permit-application/review/determination/determination.guard';
import { SharedPermitModule } from '../permit-application/shared/shared-permit.module';
import { PermitApplicationStore } from '../permit-application/store/permit-application.store';
import { PermitApplicationStoreFactory } from '../permit-application/store/permit-store.provider';
import { SharedModule } from '../shared/shared.module';
import { StoreContextResolver } from '../shared/store-resolver/store-context.resolver';
import { ApplicationSubmittedComponent } from './application-submitted/application-submitted.component';
import { PermitIssuanceRoutingModule } from './permit-issuance-routing.module';
import { DecisionSummaryComponent } from './review/decision-summary/decision-summary.component';
import { ReviewGroupStatusPermitIssuancePipe } from './review/review-group-status-permit-issuance.pipe';
import { ReviewSectionsContainerComponent } from './review/sections-container/review-sections-container.component';
import { SectionsContainerComponent } from './sections-container/sections-container.component';
import { SummaryComponent } from './summary/summary.component';

@NgModule({
  declarations: [
    ApplicationSubmittedComponent,
    DecisionSummaryComponent,
    ReviewGroupStatusPermitIssuancePipe,
    ReviewSectionsContainerComponent,
    SectionsContainerComponent,
    SummaryComponent,
  ],
  imports: [PermitIssuanceRoutingModule, SharedModule, SharedPermitModule],
  providers: [
    {
      provide: PermitApplicationStore,
      useFactory: PermitApplicationStoreFactory,
      deps: [StoreContextResolver, Location, Router],
    },
    DeterminationGuard,
  ],
})
export class PermitIssuanceModule {}
