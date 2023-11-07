/* eslint-disable @angular-eslint/sort-ngmodule-metadata-arrays */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { AviationAccountsStore } from '@aviation/accounts/store';
import { BackLinkComponent } from '@core/navigation/backlink';
import { BreadcrumbsComponent } from '@core/navigation/breadcrumbs/breadcrumbs.component';
import { DecisionSummaryComponent } from '@permit-notification/follow-up/review/decision/decision-summary/decision-summary.component';
import { WorkflowsComponent } from '@shared/accounts';
import { AbbreviationsSummaryTemplateComponent } from '@shared/components/abbreviations/abbreviations-summary-template.component';
import { AbbreviationsTemplateComponent } from '@shared/components/abbreviations/abbreviations-template.component';
import { ApproachesUsedSummaryTemplateComponent } from '@shared/components/approaches/aer/approaches-used/approaches-used-summary-template.component';
import { CalculationEmissionsTierSummaryComponent } from '@shared/components/approaches/aer/calculation-emissions-tier/calculation-emissions-tier-summary.component';
import { ApproachesAddTemplateComponent } from '@shared/components/approaches/approaches-add/approaches-add-template.component';
import { ApproachesDeleteTemplateComponent } from '@shared/components/approaches/approaches-delete/approaches-delete-template.component';
import { ApproachesHelpTemplateComponent } from '@shared/components/approaches/approaches-help/approaches-help-template.component';
import { SourceStreamHelpTemplateComponent } from '@shared/components/approaches/approaches-help/source-stream-help-template.component';
import { ApproachesTemplateComponent } from '@shared/components/approaches/approaches-template.component';
import { ConfidentialityStatementSummaryTemplateComponent } from '@shared/components/confidentiality-statement/confidentiality-statement-summary/confidentiality-statement-summary-template.component';
import { ConfidentialityStatementTemplateComponent } from '@shared/components/confidentiality-statement/confidentiality-statement-template.component';
import { AlcInformationTemplateComponent } from '@shared/components/doal/alc-information-template/alc-information-template.component';
import { AuthorityDecisionTemplateComponent } from '@shared/components/doal/authority-decision-template/authority-decision-template.component';
import { DateSubmittedSummaryTemplateComponent } from '@shared/components/doal/date-submitted-summary-template/date-submitted-summary-template.component';
import { PreliminaryAllocationDetailsTemplateComponent } from '@shared/components/doal/preliminary-allocation-details/preliminary-allocation-details-template.component';
import { TotalPreliminaryAllocationListTemplateComponent } from '@shared/components/doal/total-preliminary-allocation-list-template/total-preliminary-allocation-list-template.component';
import { EmissionPointDeleteTemplateComponent } from '@shared/components/emission-points/emission-point-delete/emission-point-delete-template.component';
import { EmissionPointDetailsTemplateComponent } from '@shared/components/emission-points/emission-point-details/emission-point-details-template.component';
import { EmissionSourceDeleteTemplateComponent } from '@shared/components/emission-sources/emission-source-delete/emission-source-delete-template.component';
import { EmissionSourceDetailsTemplateComponent } from '@shared/components/emission-sources/emission-source-detail/emission-source-details-template.component';
import { MonitoringPlanSummaryTemplateComponent } from '@shared/components/monitoring-plan/monitoring-plan-summary-template.component';
import { MonitoringPlanVersionsComponent } from '@shared/components/monitoring-plan/monitoring-plan-versions.component';
import { NaceCodesSummaryTemplateComponent } from '@shared/components/nace-codes/nace-codes-summary-template.component';
import { PaymentNotCompletedComponent } from '@shared/components/payment-not-completed/payment-not-completed.component';
import { PrtrSummaryTemplateComponent } from '@shared/components/prtr/prtr-summary-template.component';
import { AerRegulatedActivitiesSummaryTemplateComponent } from '@shared/components/regulated-activities/aer-regulated-activities-summary-template.component';
import { RelatedActionsComponent } from '@shared/components/related-actions/related-actions.component';
import { AdditionalInfoGroupComponent } from '@shared/components/review-groups/additional-info-group/additional-info-group.component';
import { CalculationEmissionsGroupComponent } from '@shared/components/review-groups/calculation-emissions-group/calculation-emissions-group.component';
import { ComplianceEtsGroupComponent } from '@shared/components/review-groups/compliance-ets-group/compliance-ets-group.component';
import { ComplianceMonitoringGroupComponent } from '@shared/components/review-groups/compliance-monitoring-group/compliance-monitoring-group.component';
import { DataGapsGroupComponent } from '@shared/components/review-groups/data-gaps-group/data-gaps-group.component';
import { EmissionsSummaryGroupComponent } from '@shared/components/review-groups/emissions-summary-group/emissions-summary-group.component';
import { FuelsGroupComponent } from '@shared/components/review-groups/fuels-group/fuels-group.component';
import { InherentCo2GroupComponent } from '@shared/components/review-groups/inherent-co2-group/inherent-co2-group.component';
import { InstallationDetailsGroupComponent } from '@shared/components/review-groups/installation-details-group/installation-details-group.component';
import { MaterialityLevelGroupComponent } from '@shared/components/review-groups/materiality-level-group/materiality-level-group.component';
import { MisstatementsGroupComponent } from '@shared/components/review-groups/misstatements-group/misstatements-group.component';
import { NonConformitiesGroupComponent } from '@shared/components/review-groups/non-conformities-group/non-conformities-group.component';
import { AerRegulatedActivitiesGroupComponent } from '@shared/components/review-groups/opinion-statement-group/aer-regulated-activities-group/aer-regulated-activities-group.component';
import { OverallDecisionGroupComponent } from '@shared/components/review-groups/overall-decision-group/overall-decision-group.component';
import { OverallDecisionReasonListComponent } from '@shared/components/review-groups/overall-decision-group/overall-decision-reason-list.component';
import { SummaryOfConditionsGroupComponent } from '@shared/components/review-groups/summary-of-conditions-group/summary-of-conditions-group.component';
import { SummaryOfConditionsListComponent } from '@shared/components/review-groups/summary-of-conditions-group/summary-of-conditions-list.component';
import { VerifierDetailsGroupComponent } from '@shared/components/review-groups/verifier-details-group/verifier-details-group.component';
import { SelectOtherComponent } from '@shared/components/select-other/select-other.component';
import { SourceStreamDeleteTemplateComponent } from '@shared/components/source-streams/source-stream-delete/source-stream-delete-template.component';
import { SourceStreamDetailsTemplateComponent } from '@shared/components/source-streams/source-stream-details/source-streams-details-template.component';
import { TaskHeaderInfoComponent } from '@shared/components/task-header-info/task-header-info.component';
import { CategoryTypeNamePipe } from '@shared/pipes/category-type-name.pipe';
import { OverallAssessmentTypePipe } from '@shared/pipes/overall-assessment-type.pipe';
import { PipesModule } from '@shared/pipes/pipes.module';
import { SourceStreamDescriptionPipe } from '@shared/pipes/source-streams-description.pipe';
import { VerificationBodyTypePipe } from '@shared/pipes/verification-body-type.pipe';
import { SubmitComponent } from '@shared/submit/submit.component';
import { MarkdownModule } from 'ngx-markdown';

import { GovukComponentsModule } from 'govuk-components';

import {
  AccountNoteComponent,
  AccountNotesComponent,
  AccountsListComponent,
  AccountsPageComponent,
  AccountsStore,
  DeleteAccountNoteComponent,
  ProcessActionsComponent,
  ReportsComponent,
} from './accounts';
import {
  AddComponent,
  AppointComponent,
  ConfirmationComponent as VerBodyConfirmationComponent,
  DeleteComponent,
  DetailsComponent as OperatorDetailsComponent,
  OperatorsComponent,
} from './accounts/components/operators';
import { AddAnotherDirective } from './add-another/add-another.directive';
import { AddressInputComponent } from './address-input/address-input.component';
import { AssignmentConfirmationComponent } from './assignment-confirmation/assignment-confirmation.component';
import { AttachmentComponent } from './attachment/attachment.component';
import { BackToTopComponent } from './back-to-top/back-to-top.component';
import { BaseSuccessComponent } from './base-success/base-success.component';
import { BooleanRadioGroupComponent } from './boolean-radio-group/boolean-radio-group.component';
import { AdditionalDocumentsSharedComponent } from './components/additional-documents/additional-documents.component';
import { AdditionalDocumentsSummaryTemplateComponent } from './components/additional-documents/additional-documents-summary/documents-summary-template.component';
import { MeasurementTierSummaryComponent } from './components/approaches/aer/measurement-tier-summary/measurement-tier-summary.component';
import { PfcTierSummaryComponent } from './components/approaches/aer/pfc-tier-summary/pfc-tier-summary.component';
import { ArchiveComponent } from './components/archive/archive.component';
import { AssignmentComponent } from './components/assignment/assignment.component';
import { BatchReissueRequestsComponent } from './components/batch-reissue-requests/batch-reissue-requests.component';
import { ConfirmationComponent } from './components/confirmation/confirmation.component';
import { DecisionComponent } from './components/decision/decision.component';
import { DecisionConfirmationComponent } from './components/decision/decision-confirmation/decision-confirmation.component';
import { ActivityLevelListTemplateComponent as DoalActivityLevelListTemplateComponent } from './components/doal/activity-level-list-template/activity-level-list-template.component';
import { AdditionalDocumentsSummaryTemplateComponent as DoalAdditionalDocumentsSummaryTemplateComponent } from './components/doal/additional-documents-summary-template/additional-documents-summary-template.component';
import { DeterminationCloseSummaryTemplateComponent as DoalDeterminationCloseSummaryTemplateComponent } from './components/doal/determination-close-summary-template/determination-close-summary-template.component';
import { DeterminationProceedAuthorityReasonTemplateComponent as DoalDeterminationProceedAuthorityReasonTemplateComponent } from './components/doal/determination-proceed-authority-reason-template/determination-proceed-authority-reason-template.component';
import { DeterminationProceedAuthoritySummaryTemplateComponent as DoalDeterminationProceedAuthoritySummaryTemplateComponent } from './components/doal/determination-proceed-authority-summary-template/determination-proceed-authority-summary-template.component';
import { DeterminationProceedAuthorityWithholdingTemplateComponent as DoalDeterminationProceedAuthorityWithholdingTemplateComponent } from './components/doal/determination-proceed-authority-withholding-template/determination-proceed-authority-withholding-template.component';
import { DeterminationSummaryTemplateComponent as DoalDeterminationSummaryTemplateComponent } from './components/doal/determination-summary-template/determination-summary-template.component';
import { OperatorReportSummaryTemplateComponent as DoalOperatorReportSummaryTemplateComponent } from './components/doal/operator-report-summary-template/operator-report-summary-template.component';
import { PreliminaryAllocationListTemplateComponent as DoalPreliminaryAllocationListTemplateComponent } from './components/doal/preliminary-allocation-list-template/preliminary-allocation-list-template.component';
import { VerificationReportSummaryTemplateComponent as DoalVerificationReportSummaryTemplateComponent } from './components/doal/verification-report-summary-template/verification-report-summary-template.component';
import { DeterminationReasonSummaryTemplateComponent } from './components/dre/determination-reason-summary-template/determination-reason-summary-template.component';
import { DreSummaryComponent } from './components/dre/dre-summary-template/dre-summary-template.component';
import { FeeSummaryTemplateComponent } from './components/dre/fee-summary-template/fee-summary-template.component';
import { InformationSourcesSummaryTemplateComponent } from './components/dre/information-sources-summary-template/information-sources-summary-template.component';
import { MonitoringApproachesSummaryTemplateComponent } from './components/dre/monitoring-approaches-summary-template/monitoring-approaches-summary-template.component';
import { ReportableEmissionsSummaryTemplateComponent } from './components/dre/reportable-emissions-summary-template/reportable-emissions-summary-template.component';
import { InstallationDetailsSummaryComponent } from './components/installation-details/installation-details-summary.component';
import { ChooseWorkflowSummaryTemplateComponent } from './components/non-compliance/choose-workflow-summary-template/choose-workflow-summary-template.component';
import { NotifyOperatorComponent } from './components/notify-operator/notify-operator.component';
import { PeerReviewComponent } from './components/peer-review/peer-review.component';
import { AnswersComponent as PeerReviewDecisionAnswersComponent } from './components/peer-review-decision/answers/answers.component';
import { ConfirmationComponent as PeerReviewDecisionConfirmationComponent } from './components/peer-review-decision/confirmation/confirmation.component';
import { PeerReviewDecisionComponent } from './components/peer-review-decision/peer-review-decision.component';
import { PeerReviewSubmittedComponent } from './components/peer-review-decision/timeline/peer-review-submitted.component';
import { FiltersTemplateComponent } from './components/permit-batch-reissue/filters-template/filters-template.component';
import { PermitTransferDetailsSummaryTemplateComponent } from './components/permit-transfer/transfer-details-summary-template/transfer-details-summary-template.component';
import { RelatedContentComponent } from './components/related-content/related-content.component';
import { RelatedTasksComponent } from './components/related-tasks/related-tasks.component';
import { RequestActionHeadingComponent } from './components/request-action-heading/request-action-heading.component';
import { RaSummaryTemplateComponent } from './components/return-of-allowances/ra-summary-template/ra-summary-template.component';
import { ActivityLevelReportGroupComponent } from './components/review-groups/activity-level-report-group/activity-level-report-group.component';
import { EtsComplianceRulesGroupComponent } from './components/review-groups/aviation-aer-compliance-ets-rules-group/ets-compliance-rules-group.component';
import { FallbackEmissionsGroupComponent } from './components/review-groups/fallback-emissions-group/fallback-emissions-group.component';
import { MeasurementGroupComponent } from './components/review-groups/measurement-group/measurement-group.component';
import { NonCompliancesGroupComponent } from './components/review-groups/non-compliances-group/non-compliances-group.component';
import { NonConformitiesPerPlanGroupComponent } from './components/review-groups/non-conformities-group/non-conformities-per-plan-group/non-conformities-per-plan-group.component';
import { NonConformitiesPreviousYearGroupComponent } from './components/review-groups/non-conformities-group/non-conformities-previous-year-group/non-conformities-previous-year-group.component';
import { AerCombustionSourcesGroupComponent } from './components/review-groups/opinion-statement-group/aer-combustion-sources-group/aer-combustion-sources-group.component';
import { AerEmissionDetailsGroupComponent } from './components/review-groups/opinion-statement-group/aer-emission-details-group/aer-emission-details-group.component';
import { AerProcessSourcesGroupComponent } from './components/review-groups/opinion-statement-group/aer-process-sources-group/aer-process-sources-group.component';
import { AerSiteVisitGroupComponent } from './components/review-groups/opinion-statement-group/aer-site-visit-group/aer-site-visit-group.component';
import { AerVerifiersEmissionsAssessmentGroupComponent } from './components/review-groups/opinion-statement-group/aer-verifiers-emissions-assessment-group/aer-verifiers-emissions-assessment-group.component';
import { OpinionStatementGroupComponent } from './components/review-groups/opinion-statement-group/opinion-statement-group.component';
import { PfcGroupComponent } from './components/review-groups/pfc-group/pfc-group.component';
import { RecommendedImprovementsGroupComponent } from './components/review-groups/recommended-improvements-group/recommended-improvements-group.component';
import { VerifyEmissionsReductionClaimGroupComponent } from './components/review-groups/verify-emissions-reduction-claim-group/verify-emissions-reduction-claim-group.component';
import { SourceStreamsSummaryTableComponent } from './components/source-streams-summary-table/source-streams-summary-table.component';
import { SummaryDownloadFilesComponent } from './components/summary-download-files/summary-download-files.component';
import { TimelineComponent } from './components/timeline/timeline.component';
import { TimelineItemComponent } from './components/timeline/timeline-item.component';
import { TransferInstallationSummaryTemplateComponent } from './components/transfer-installation-summary-template/transfer-installation-summary-template.component';
import { TransferDetailsTemplateComponent } from './components/transferred-co2/transfer-details-template/transfer-details-template.component';
import { WaSummaryTemplateComponent } from './components/withholding-of-allowances/wa-summary-template/wa-summary-template.component';
import { WaWithdrawSummaryTemplateComponent } from './components/withholding-of-allowances/wa-withdraw-summary-template/wa-withdraw-summary-template.component';
import { DashboardPageComponent, DashboardStore, ItemTypePipe, WorkflowItemsListComponent } from './dashboard';
import { AsyncValidationFieldDirective } from './directives/async-validation-field.directive';
import { CountriesDirective } from './directives/countries.directive';
import { UsersTableDirective } from './directives/users-table.directive';
import { DropdownButtonComponent } from './dropdown-button/dropdown-button.component';
import { DropdownButtonItemComponent } from './dropdown-button/item/dropdown-button-item.component';
import { ErrorPageComponent } from './error-page/error-page.component';
import { FileDownloadComponent } from './file-download/file-download.component';
import { FileInputComponent } from './file-input/file-input.component';
import { FileUploadListComponent } from './file-upload-list/file-upload-list.component';
import { GroupedSummaryListDirective } from './grouped-summary-list/grouped-summary-list.directive';
import { HighlightDiffComponent } from './highlight-diff/highlight-diff.component';
import { HoldingCompanyFormComponent } from './holding-company-form';
import { IdentityBarComponent } from './identity-bar/identity-bar.component';
import { IncorporateHeaderComponent } from './incorporate-header/incorporate-header.component';
import { ConvertLinksDirective } from './markdown/convert-links.directive';
import { RouterLinkComponent } from './markdown/router-link.component';
import { MultiSelectComponent } from './multi-select/multi-select.component';
import { MultiSelectItemComponent } from './multi-select/multi-select-item/multi-select-item.component';
import { MultipleFileInputComponent } from './multiple-file-input/multiple-file-input.component';
import { NavigationComponent } from './navigation/navigation.component';
import { NavigationLinkDirective } from './navigation/navigation-link.directive';
import { PageHeadingComponent } from './page-heading/page-heading.component';
import { PaginationComponent } from './pagination/pagination.component';
import { PendingButtonDirective } from './pending-button.directive';
import { PhaseBarComponent } from './phase-bar/phase-bar.component';
import { PhoneInputComponent } from './phone-input/phone-input.component';
import { ItemLinkPipe } from './pipes/item-link.pipe';
import { NegativeNumberPipe } from './pipes/negative-number.pipe';
import { ReportingSubheadingPipe } from './pipes/reporting-subheading.pipe';
import { TextEllipsisPipe } from './pipes/text-ellipsis.pipe';
import { RadioOptionComponent } from './radio-option/radio-option.component';
import { SkipLinkFocusDirective } from './skip-link-focus.directive';
import { SummaryHeaderComponent } from './summary-header/summary-header.component';
import { TaskItemComponent } from './task-list/task-item/task-item.component';
import { TaskItemListComponent } from './task-list/task-item-list/task-item-list.component';
import { TaskListComponent } from './task-list/task-list.component';
import { TaskSectionComponent } from './task-list/task-section/task-section.component';
import { TwoFaLinkComponent } from './two-fa-link/two-fa-link.component';
import { UserInputComponent } from './user-input/user-input.component';
import { UserLockedComponent } from './user-locked/user-locked.component';
import { WizardStepComponent } from './wizard/wizard-step.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    GovukComponentsModule,
    PipesModule,
    MarkdownModule.forChild(),
    ReactiveFormsModule,
    RouterModule,
  ],
  declarations: [
    AbbreviationsSummaryTemplateComponent,
    AbbreviationsTemplateComponent,
    AccountNoteComponent,
    AccountNotesComponent,
    AccountsListComponent,
    AccountsPageComponent,
    ActivityLevelReportGroupComponent,
    AddAnotherDirective,
    AddComponent,
    AdditionalDocumentsSharedComponent,
    AdditionalDocumentsSummaryTemplateComponent,
    AdditionalInfoGroupComponent,
    AddressInputComponent,
    AerCombustionSourcesGroupComponent,
    AerEmissionDetailsGroupComponent,
    AerProcessSourcesGroupComponent,
    AerRegulatedActivitiesGroupComponent,
    AerRegulatedActivitiesSummaryTemplateComponent,
    AerSiteVisitGroupComponent,
    AerVerifiersEmissionsAssessmentGroupComponent,
    AlcInformationTemplateComponent,
    AppointComponent,
    ApproachesAddTemplateComponent,
    ApproachesDeleteTemplateComponent,
    ApproachesHelpTemplateComponent,
    ApproachesTemplateComponent,
    ApproachesUsedSummaryTemplateComponent,
    ArchiveComponent,
    AssignmentComponent,
    AssignmentConfirmationComponent,
    AsyncValidationFieldDirective,
    AttachmentComponent,
    AuthorityDecisionTemplateComponent,
    BackLinkComponent,
    BackToTopComponent,
    BaseSuccessComponent,
    BatchReissueRequestsComponent,
    BooleanRadioGroupComponent,
    BreadcrumbsComponent,
    CalculationEmissionsGroupComponent,
    CalculationEmissionsTierSummaryComponent,
    CategoryTypeNamePipe,
    ChooseWorkflowSummaryTemplateComponent,
    ComplianceEtsGroupComponent,
    ComplianceMonitoringGroupComponent,
    ConfidentialityStatementSummaryTemplateComponent,
    ConfidentialityStatementTemplateComponent,
    ConfirmationComponent,
    ConvertLinksDirective,
    CountriesDirective,
    DashboardPageComponent,
    DataGapsGroupComponent,
    DateSubmittedSummaryTemplateComponent,
    DecisionComponent,
    DecisionConfirmationComponent,
    DecisionSummaryComponent,
    DeleteAccountNoteComponent,
    DeleteComponent,
    DeterminationReasonSummaryTemplateComponent,
    DoalActivityLevelListTemplateComponent,
    DoalAdditionalDocumentsSummaryTemplateComponent,
    DoalDeterminationCloseSummaryTemplateComponent,
    DoalDeterminationProceedAuthorityReasonTemplateComponent,
    DoalDeterminationProceedAuthoritySummaryTemplateComponent,
    DoalDeterminationProceedAuthorityWithholdingTemplateComponent,
    DoalDeterminationSummaryTemplateComponent,
    DoalOperatorReportSummaryTemplateComponent,
    DoalPreliminaryAllocationListTemplateComponent,
    DoalVerificationReportSummaryTemplateComponent,
    DreSummaryComponent,
    DropdownButtonComponent,
    DropdownButtonItemComponent,
    EmissionPointDeleteTemplateComponent,
    EmissionPointDetailsTemplateComponent,
    EmissionSourceDeleteTemplateComponent,
    EmissionSourceDetailsTemplateComponent,
    EmissionsSummaryGroupComponent,
    ErrorPageComponent,
    EtsComplianceRulesGroupComponent,
    FallbackEmissionsGroupComponent,
    FeeSummaryTemplateComponent,
    FileDownloadComponent,
    FileInputComponent,
    FileUploadListComponent,
    FiltersTemplateComponent,
    FuelsGroupComponent,
    GroupedSummaryListDirective,
    HighlightDiffComponent,
    HoldingCompanyFormComponent,
    IdentityBarComponent,
    VerifyEmissionsReductionClaimGroupComponent,
    IncorporateHeaderComponent,
    InformationSourcesSummaryTemplateComponent,
    InherentCo2GroupComponent,
    InstallationDetailsGroupComponent,
    InstallationDetailsSummaryComponent,
    ItemTypePipe,
    MaterialityLevelGroupComponent,
    MeasurementGroupComponent,
    MeasurementTierSummaryComponent,
    MisstatementsGroupComponent,
    MonitoringApproachesSummaryTemplateComponent,
    MonitoringPlanSummaryTemplateComponent,
    MonitoringPlanVersionsComponent,
    MultipleFileInputComponent,
    MultiSelectComponent,
    MultiSelectItemComponent,
    NaceCodesSummaryTemplateComponent,
    NavigationComponent,
    NavigationLinkDirective,
    NegativeNumberPipe,
    NonCompliancesGroupComponent,
    NonConformitiesGroupComponent,
    NonConformitiesPerPlanGroupComponent,
    NonConformitiesPreviousYearGroupComponent,
    NotifyOperatorComponent,
    OperatorDetailsComponent,
    OperatorsComponent,
    OpinionStatementGroupComponent,
    OverallAssessmentTypePipe,
    OverallDecisionGroupComponent,
    OverallDecisionReasonListComponent,
    PageHeadingComponent,
    PaginationComponent,
    PaymentNotCompletedComponent,
    PeerReviewComponent,
    PeerReviewDecisionAnswersComponent,
    PeerReviewDecisionComponent,
    PeerReviewDecisionConfirmationComponent,
    PeerReviewSubmittedComponent,
    PendingButtonDirective,
    PermitTransferDetailsSummaryTemplateComponent,
    PfcGroupComponent,
    PfcTierSummaryComponent,
    PhaseBarComponent,
    PhoneInputComponent,
    PreliminaryAllocationDetailsTemplateComponent,
    ProcessActionsComponent,
    PrtrSummaryTemplateComponent,
    RadioOptionComponent,
    RaSummaryTemplateComponent,
    RecommendedImprovementsGroupComponent,
    RelatedActionsComponent,
    RelatedContentComponent,
    RelatedTasksComponent,
    ReportableEmissionsSummaryTemplateComponent,
    ReportingSubheadingPipe,
    ReportsComponent,
    RequestActionHeadingComponent,
    RouterLinkComponent,
    SelectOtherComponent,
    SkipLinkFocusDirective,
    SourceStreamDeleteTemplateComponent,
    SourceStreamDetailsTemplateComponent,
    SourceStreamHelpTemplateComponent,
    SourceStreamsSummaryTableComponent,
    SubmitComponent,
    SummaryDownloadFilesComponent,
    SummaryHeaderComponent,
    SummaryOfConditionsGroupComponent,
    SummaryOfConditionsListComponent,
    TaskHeaderInfoComponent,
    TaskItemComponent,
    TaskItemListComponent,
    TaskListComponent,
    TaskSectionComponent,
    TextEllipsisPipe,
    TimelineComponent,
    TimelineItemComponent,
    TotalPreliminaryAllocationListTemplateComponent,
    TransferDetailsTemplateComponent,
    TransferInstallationSummaryTemplateComponent,
    TwoFaLinkComponent,
    UserInputComponent,
    UserLockedComponent,
    UsersTableDirective,
    VerBodyConfirmationComponent,
    VerificationBodyTypePipe,
    VerifierDetailsGroupComponent,
    WaSummaryTemplateComponent,
    WaWithdrawSummaryTemplateComponent,
    WizardStepComponent,
    WorkflowItemsListComponent,
    WorkflowsComponent,
  ],
  exports: [
    AbbreviationsSummaryTemplateComponent,
    AbbreviationsTemplateComponent,
    AccountNotesComponent,
    AccountsListComponent,
    AccountsPageComponent,
    ActivityLevelReportGroupComponent,
    AddAnotherDirective,
    AdditionalDocumentsSharedComponent,
    AdditionalDocumentsSummaryTemplateComponent,
    AdditionalInfoGroupComponent,
    AddressInputComponent,
    AerCombustionSourcesGroupComponent,
    AerEmissionDetailsGroupComponent,
    AerProcessSourcesGroupComponent,
    AerRegulatedActivitiesGroupComponent,
    AerRegulatedActivitiesSummaryTemplateComponent,
    AerSiteVisitGroupComponent,
    AerVerifiersEmissionsAssessmentGroupComponent,
    AlcInformationTemplateComponent,
    ApproachesAddTemplateComponent,
    ApproachesDeleteTemplateComponent,
    ApproachesHelpTemplateComponent,
    ApproachesTemplateComponent,
    ApproachesUsedSummaryTemplateComponent,
    ArchiveComponent,
    AssignmentComponent,
    AssignmentConfirmationComponent,
    AsyncValidationFieldDirective,
    AttachmentComponent,
    AuthorityDecisionTemplateComponent,
    BackLinkComponent,
    BackToTopComponent,
    BaseSuccessComponent,
    BatchReissueRequestsComponent,
    BooleanRadioGroupComponent,
    BreadcrumbsComponent,
    CalculationEmissionsGroupComponent,
    CalculationEmissionsTierSummaryComponent,
    CategoryTypeNamePipe,
    ChooseWorkflowSummaryTemplateComponent,
    CommonModule,
    ComplianceEtsGroupComponent,
    ComplianceMonitoringGroupComponent,
    ConfidentialityStatementSummaryTemplateComponent,
    ConfidentialityStatementTemplateComponent,
    ConfirmationComponent,
    ConvertLinksDirective,
    CountriesDirective,
    DashboardPageComponent,
    DataGapsGroupComponent,
    DateSubmittedSummaryTemplateComponent,
    DecisionComponent,
    DecisionConfirmationComponent,
    DecisionSummaryComponent,
    DeterminationReasonSummaryTemplateComponent,
    DoalActivityLevelListTemplateComponent,
    DoalAdditionalDocumentsSummaryTemplateComponent,
    DoalDeterminationCloseSummaryTemplateComponent,
    DoalDeterminationProceedAuthorityReasonTemplateComponent,
    DoalDeterminationProceedAuthoritySummaryTemplateComponent,
    DoalDeterminationProceedAuthorityWithholdingTemplateComponent,
    DoalDeterminationSummaryTemplateComponent,
    DoalOperatorReportSummaryTemplateComponent,
    DoalPreliminaryAllocationListTemplateComponent,
    DoalVerificationReportSummaryTemplateComponent,
    DreSummaryComponent,
    DropdownButtonComponent,
    DropdownButtonItemComponent,
    EmissionPointDeleteTemplateComponent,
    EmissionPointDetailsTemplateComponent,
    EmissionSourceDeleteTemplateComponent,
    EmissionSourceDetailsTemplateComponent,
    EmissionsSummaryGroupComponent,
    ErrorPageComponent,
    EtsComplianceRulesGroupComponent,
    FallbackEmissionsGroupComponent,
    FeeSummaryTemplateComponent,
    FileDownloadComponent,
    FileInputComponent,
    FiltersTemplateComponent,
    FormsModule,
    FuelsGroupComponent,
    GovukComponentsModule,
    GroupedSummaryListDirective,
    HighlightDiffComponent,
    HoldingCompanyFormComponent,
    VerifyEmissionsReductionClaimGroupComponent,
    IdentityBarComponent,
    IncorporateHeaderComponent,
    InformationSourcesSummaryTemplateComponent,
    InherentCo2GroupComponent,
    InstallationDetailsGroupComponent,
    InstallationDetailsSummaryComponent,
    ItemTypePipe,
    MaterialityLevelGroupComponent,
    MeasurementGroupComponent,
    MeasurementTierSummaryComponent,
    MisstatementsGroupComponent,
    MonitoringApproachesSummaryTemplateComponent,
    MonitoringPlanSummaryTemplateComponent,
    MonitoringPlanVersionsComponent,
    MultipleFileInputComponent,
    MultiSelectComponent,
    MultiSelectItemComponent,
    NaceCodesSummaryTemplateComponent,
    NavigationComponent,
    NavigationLinkDirective,
    NegativeNumberPipe,
    NonCompliancesGroupComponent,
    NonConformitiesGroupComponent,
    NonConformitiesPerPlanGroupComponent,
    NonConformitiesPreviousYearGroupComponent,
    NotifyOperatorComponent,
    OperatorsComponent,
    OpinionStatementGroupComponent,
    OverallAssessmentTypePipe,
    OverallDecisionGroupComponent,
    OverallDecisionReasonListComponent,
    PageHeadingComponent,
    PaginationComponent,
    PaymentNotCompletedComponent,
    PeerReviewComponent,
    PeerReviewDecisionAnswersComponent,
    PeerReviewDecisionComponent,
    PeerReviewDecisionConfirmationComponent,
    PeerReviewSubmittedComponent,
    PendingButtonDirective,
    PermitTransferDetailsSummaryTemplateComponent,
    PfcGroupComponent,
    PfcTierSummaryComponent,
    PhaseBarComponent,
    PhoneInputComponent,
    PipesModule,
    PreliminaryAllocationDetailsTemplateComponent,
    ProcessActionsComponent,
    PrtrSummaryTemplateComponent,
    RadioOptionComponent,
    RaSummaryTemplateComponent,
    ReactiveFormsModule,
    RecommendedImprovementsGroupComponent,
    RelatedActionsComponent,
    RelatedContentComponent,
    RelatedTasksComponent,
    ReportableEmissionsSummaryTemplateComponent,
    ReportingSubheadingPipe,
    ReportsComponent,
    RequestActionHeadingComponent,
    SelectOtherComponent,
    SkipLinkFocusDirective,
    SourceStreamDeleteTemplateComponent,
    SourceStreamDetailsTemplateComponent,
    SourceStreamHelpTemplateComponent,
    SourceStreamsSummaryTableComponent,
    SubmitComponent,
    SummaryDownloadFilesComponent,
    SummaryHeaderComponent,
    SummaryOfConditionsGroupComponent,
    SummaryOfConditionsListComponent,
    TaskHeaderInfoComponent,
    TaskItemComponent,
    TaskItemListComponent,
    TaskListComponent,
    TaskSectionComponent,
    TextEllipsisPipe,
    TimelineComponent,
    TimelineItemComponent,
    TotalPreliminaryAllocationListTemplateComponent,
    TransferDetailsTemplateComponent,
    TransferInstallationSummaryTemplateComponent,
    TwoFaLinkComponent,
    UserInputComponent,
    UserLockedComponent,
    UsersTableDirective,
    VerificationBodyTypePipe,
    VerifierDetailsGroupComponent,
    WaSummaryTemplateComponent,
    WaWithdrawSummaryTemplateComponent,
    WizardStepComponent,
    WorkflowItemsListComponent,
    WorkflowsComponent,
  ],
  providers: [
    AccountsStore,
    AviationAccountsStore,
    CategoryTypeNamePipe,
    DashboardStore,
    ItemLinkPipe,
    SourceStreamDescriptionPipe,
  ],
})
export class SharedModule {}
