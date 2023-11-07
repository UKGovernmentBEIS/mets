import { Location } from '@angular/common';
import { NgModule } from '@angular/core';
import { Router } from '@angular/router';

import { DeterminationGuard } from '../permit-application/review/determination/determination.guard';
import { SharedPermitModule } from '../permit-application/shared/shared-permit.module';
import { PermitApplicationStore } from '../permit-application/store/permit-application.store';
import { PermitApplicationStoreFactory } from '../permit-application/store/permit-store.provider';
import { SharedModule } from '../shared/shared.module';
import { StoreContextResolver } from '../shared/store-resolver/store-context.resolver';
import { AboutVariationComponent } from './about-variation/about-variation.component';
import { AnswersComponent as AboutVariationAnswersComponent } from './about-variation/answers/answers.component';
import { ChangesComponent as AboutVariationChangesComponent } from './about-variation/changes/changes.component';
import { SummaryComponent as AboutVariationSummaryComponent } from './about-variation/summary/summary.component';
import { SummaryDetailsComponent } from './about-variation/summary/summary-details.component';
import { PermitVariationRoutingModule } from './permit-variation-routing.module';
import { AboutVariationComponent as ReviewAboutVariation } from './review/about-variation/about-variation.component';
import { DecisionSummaryComponent } from './review/decision-summary/decision-summary.component';
import { LogChangesComponent } from './review/determination/log-changes/log-changes.component';
import { ReasonTemplateComponent } from './review/determination/reason-template/reason-template.component';
import { ReviewGroupStatusPermitVariationPipe } from './review/review-group-status-permit-variation.pipe';
import { ReviewGroupStatusPermitVariationRegulatorLedPipe } from './review/review-group-status-permit-variation-regulator-led.pipe';
import { ReviewSectionsContainerComponent } from './review/sections-container/review-sections-container.component';
import { SectionsContainerComponent } from './sections-container/sections-container.component';
import { SharedPermitVariationModule } from './shared/shared-permit-variation.module';
import { SummaryComponent } from './summary/summary.component';

@NgModule({
  declarations: [
    AboutVariationAnswersComponent,
    AboutVariationChangesComponent,
    AboutVariationComponent,
    AboutVariationSummaryComponent,
    DecisionSummaryComponent,
    LogChangesComponent,
    ReasonTemplateComponent,
    ReviewAboutVariation,
    ReviewGroupStatusPermitVariationPipe,
    ReviewGroupStatusPermitVariationRegulatorLedPipe,
    ReviewSectionsContainerComponent,
    SectionsContainerComponent,
    SummaryComponent,

    SummaryDetailsComponent,
  ],
  imports: [PermitVariationRoutingModule, SharedModule, SharedPermitModule, SharedPermitVariationModule],
  providers: [
    {
      provide: PermitApplicationStore,
      useFactory: PermitApplicationStoreFactory,
      deps: [StoreContextResolver, Location, Router],
    },
    DeterminationGuard,
  ],
})
export class PermitVariationModule {}
