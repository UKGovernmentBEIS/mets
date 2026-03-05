import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { AnalysisMethodComponent } from './category-tier/analysis-method/analysis-method.component';
import { AnalysisMethodDeleteComponent } from './category-tier/analysis-method-delete/analysis-method-delete.component';
import { AnalysisMethodListComponent } from './category-tier/analysis-method-list/analysis-method-list.component';
import { AnalysisMethodListTemplateComponent } from './category-tier/analysis-method-list/analysis-method-list-template.component';
import { AnalysisMethodUsedComponent } from './category-tier/analysis-method-used/analysis-method-used.component';
import { AnswersComponent } from './category-tier/answers/answers.component';
import { AppliedStandardComponent } from './category-tier/applied-standard/applied-standard.component';
import { SummaryComponent as AppliedStandardSummaryComponent } from './category-tier/applied-standard/summary/summary.component';
import { CategoryComponent } from './category-tier/category/category.component';
import { CategorySummaryComponent } from './category-tier/category/summary/category-summary.component';
import { CategorySummaryOverviewComponent } from './category-tier/category/summary/category-summary-overview.component';
import { TransferredCo2DetailsComponent } from './category-tier/category/transferred-co2-details/transferred-co2-details.component';
import { CategoryTierComponent } from './category-tier/category-tier.component';
import { DefaultValueComponent } from './category-tier/default-value/default-value.component';
import { DeleteComponent } from './category-tier/delete/delete.component';
import { EntryStepComponent } from './category-tier/entry-step/entry-step.component';
import { AnswersGuard } from './category-tier/guards/answers.guard';
import { WizardStepGuard } from './category-tier/guards/wizard-step.guard';
import { SubtaskNamePipe } from './category-tier/pipes/subtask-name.pipe';
import { ReferenceComponent } from './category-tier/reference/reference.component';
import { SamplingJustificationComponent } from './category-tier/sampling-justification/sampling-justification.component';
import { SubtaskHelpComponent } from './category-tier/subtask-help/subtask-help.component';
import { SummaryComponent } from './category-tier/summary/summary.component';
import { SummaryTemplateComponent } from './category-tier/summary/summary-template.component';
import { TierComponent } from './category-tier/tier/tier.component';
import { TierJustificationComponent } from './category-tier/tier-justification/tier-justification.component';
import { DescriptionComponent } from './description/description.component';
import { HintComponent } from './description/hint.component';
import { SummaryComponent as DescriptionSummaryComponent } from './description/summary/summary.component';
import { MeasurementComponent } from './measurement.component';
import { MeasurementRoutingModule } from './measurement-routing.module';
import { OptionalComponent } from './optional/optional.component';
import { SummaryComponent as OptionalSummaryComponent } from './optional/summary/summary.component';
import { ProcedureComponent } from './procedure/procedure.component';
import { SummaryComponent as ProcedureSummaryComponent } from './procedure/summary/summary.component';

@NgModule({
  declarations: [
    AnalysisMethodComponent,
    AnalysisMethodDeleteComponent,
    AnalysisMethodListComponent,
    AnalysisMethodListTemplateComponent,
    AnalysisMethodUsedComponent,
    AnswersComponent,
    AppliedStandardComponent,
    AppliedStandardSummaryComponent,
    CategoryComponent,
    CategorySummaryComponent,
    CategorySummaryOverviewComponent,
    CategoryTierComponent,
    DefaultValueComponent,
    DeleteComponent,
    DescriptionComponent,
    DescriptionSummaryComponent,
    EntryStepComponent,
    HintComponent,
    MeasurementComponent,
    OptionalComponent,
    OptionalSummaryComponent,
    ProcedureComponent,
    ProcedureSummaryComponent,
    ReferenceComponent,
    SamplingJustificationComponent,
    SubtaskHelpComponent,
    SubtaskNamePipe,
    SummaryComponent,
    SummaryTemplateComponent,
    TierComponent,
    TierJustificationComponent,
    TransferredCo2DetailsComponent,
  ],
  imports: [MeasurementRoutingModule, SharedModule, SharedPermitModule],
  providers: [AnswersGuard, WizardStepGuard],
})
export class MeasurementModule {}
