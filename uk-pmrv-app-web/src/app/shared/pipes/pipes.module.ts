import { NgModule } from '@angular/core';

import { InstallationCategoryPipe } from '@permit-application/shared/pipes/installation-category.pipe';
import { DeterminationAssessmentPipe } from '@shared/components/peer-review-decision/determination-assessment.pipe';
import { NotificationTypePipe } from '@shared/components/permit-notification/pipes/notification-type.pipe';
import { ReportingTypePipe } from '@shared/components/permit-notification/pipes/reporting-type.pipe';
import { AccreditationReferenceDocumentNamePipe } from '@shared/pipes/accreditation-reference-document-name.pipe';
import { ActivityCalculationMethodTypePipe } from '@shared/pipes/activity-calculation-method-type.pipe';
import { CrfActivityItemNamePipe } from '@shared/pipes/aer/crf-activity-item-name.pipe';
import { PrtrActivityItemNamePipe } from '@shared/pipes/aer/prtr-activity-item-name.pipe';
import { AuthorityDecisionTypePipe } from '@shared/pipes/authority-decision-type.pipe';
import { CalculationMethodTypePipe } from '@shared/pipes/calculation-method-type.pipe';
import { CapacityUnitPipe } from '@shared/pipes/capacity-unit.pipe';
import { CompetentAuthorityLocationPipe } from '@shared/pipes/competent-authority-location.pipe';
import { CoordinatePipe } from '@shared/pipes/coordinate.pipe';
import { DaysRemainingPipe } from '@shared/pipes/days-remaining.pipe';
import { DefaultIfEmptyPipe } from '@shared/pipes/default-if-empty.pipe';
import { DeterminationTypePipe } from '@shared/pipes/determination-type.pipe';
import { EtsSchemePipe } from '@shared/pipes/ets-scheme/ets-scheme.pipe';
import { FuelMeteringConditionTypePipe } from '@shared/pipes/fuel-metering-condition-type.pipe';
import { GasPipe } from '@shared/pipes/gas.pipe';
import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { IncludesPipe } from '@shared/pipes/include.pipe';
import { IncludesAnyPipe } from '@shared/pipes/includes-any.pipe';
import { ItemActionHeaderPipe } from '@shared/pipes/item-action-header.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { LegalEntityTypePipe } from '@shared/pipes/legal-entity-type.pipe';
import { MeasurementUnitTypePipe } from '@shared/pipes/measurement-unit-type.pipe';
import { MonitoringApproachDescriptionPipe } from '@shared/pipes/monitoring-approach-description.pipe';
import { MonitoringApproachEmissionDescriptionPipe } from '@shared/pipes/monitoring-approach-emission-description.pipe';
import { NotVerifiedReasonTypePipe } from '@shared/pipes/not-verified-reason-type.pipe';
import { ParameterTypePipe } from '@shared/pipes/parameter-type.pipe';
import { PhoneNumberPipe } from '@shared/pipes/phone-number.pipe';
import { RegulatedActivitiesSortPipe } from '@shared/pipes/regulated-activities-sort.pipe';
import { RegulatedActivityTypePipe } from '@shared/pipes/regulated-activity-type.pipe';
import { ReviewGroupDecisionPipe } from '@shared/pipes/review-group-decision.pipe';
import { SecondsToMinutesPipe } from '@shared/pipes/seconds-to-minutes.pipe';
import { SourceStreamDescriptionPipe } from '@shared/pipes/source-streams-description.pipe';
import { SourceStreamTypePipe } from '@shared/pipes/source-streams-type.pipe';
import { TagColorPipe } from '@shared/pipes/tag-color.pipe';
import { TemplateFilePipe } from '@shared/pipes/template-file.pipe';
import { TimelineItemLinkPipe } from '@shared/pipes/timeline-item-link.pipe';
import { UserContactPipe } from '@shared/pipes/user-contact.pipe';
import { UserFullNamePipe } from '@shared/pipes/user-full-name.pipe';
import { UserInfoResolverPipe } from '@shared/pipes/user-info-resolver.pipe';
import { UserRolePipe } from '@shared/pipes/user-role.pipe';

import { AccountStatusPipe } from './account-status.pipe';
import { AccountTypePipe } from './account-type.pipe';
import { AerTaskDescriptionPipe } from './aer/aer-task-description.pipe';
import { ApplicationTypePipe } from './application-type.pipe';
import { AppliedTierPipe } from './applied-tier.pipe';
import { AviationNamePipePipe } from './aviation-name-pipe.pipe';
import { StatusApplicationTypePipe } from './bdr/statusApplicationType.pipe';
import { BigNumberPipe } from './big-number.pipe';
import { CapitalizeFirstPipe } from './capitalize-first.pipe';
import { CompetentAuthorityPipe } from './competent-authority.pipe';
import { CountryPipe } from './country.pipe';
import { InherentCo2DirectionsPipe } from './inherent-co2-directions.pipe';
import { InherentCo2InstrumentsPipe } from './inherent-co2-instruments.pipe';
import { ItemActionTypePipe } from './item-action-type.pipe';
import { ItemLinkPipe } from './item-link.pipe';
import { NonComplianceReasonPipe } from './non-compliance-reason.pipe';
import { OverallDecisionTypePipe } from './overall-decision-type.pipe';
import { CessationScopePipePipe } from './permanent-cessation/cessation-scope.pipe.pipe';
import { PermitTransferPartyPipe } from './permit-transfer-party.pipe';
import { PfcCalculationMethodPipe } from './pfc-calculation-method.pipe';
import { SnakeToKebabPipe } from './snake-to-kebab.pipe';
import { TaskTypeToBreadcrumbPipe } from './task-type-to-breadcrumb.pipe';
import { TransferredCO2N2ODirectionsPipe } from './transferred-co2-n2o-directions.pipe';
import { TransportApproachDescriptionPipe } from './transport-approach-description.pipe';
import { WaReasonDescriptionPipe } from './wa-reason-description.pipe';
import { WorkflowStatusPipe } from './workflow-status.pipe';
import { WorkflowTypePipe } from './workflow-type.pipe';

@NgModule({
  imports: [CessationScopePipePipe, OverallDecisionTypePipe, StatusApplicationTypePipe, TaskTypeToBreadcrumbPipe],
  declarations: [
    AccountStatusPipe,
    AccountTypePipe,
    AccreditationReferenceDocumentNamePipe,
    ActivityCalculationMethodTypePipe,
    AerTaskDescriptionPipe,
    ApplicationTypePipe,
    AppliedTierPipe,
    AuthorityDecisionTypePipe,
    AviationNamePipePipe,
    BigNumberPipe,
    CalculationMethodTypePipe,
    CapacityUnitPipe,
    CapitalizeFirstPipe,
    CompetentAuthorityLocationPipe,
    CompetentAuthorityPipe,
    CoordinatePipe,
    CountryPipe,
    CrfActivityItemNamePipe,
    DaysRemainingPipe,
    DefaultIfEmptyPipe,
    DeterminationAssessmentPipe,
    DeterminationTypePipe,
    EtsSchemePipe,
    FuelMeteringConditionTypePipe,
    GasPipe,
    GovukDatePipe,
    IncludesAnyPipe,
    IncludesPipe,
    InherentCo2DirectionsPipe,
    InherentCo2InstrumentsPipe,
    InstallationCategoryPipe,
    ItemActionHeaderPipe,
    ItemActionTypePipe,
    ItemLinkPipe,
    ItemNamePipe,
    LegalEntityTypePipe,
    MeasurementUnitTypePipe,
    MonitoringApproachDescriptionPipe,
    MonitoringApproachEmissionDescriptionPipe,
    NonComplianceReasonPipe,
    NotificationTypePipe,
    NotVerifiedReasonTypePipe,
    ParameterTypePipe,
    PermitTransferPartyPipe,
    PfcCalculationMethodPipe,
    PhoneNumberPipe,
    PrtrActivityItemNamePipe,
    RegulatedActivitiesSortPipe,
    RegulatedActivityTypePipe,
    ReportingTypePipe,
    ReviewGroupDecisionPipe,
    SecondsToMinutesPipe,
    SnakeToKebabPipe,
    SourceStreamDescriptionPipe,
    SourceStreamTypePipe,
    TagColorPipe,
    TemplateFilePipe,
    TimelineItemLinkPipe,
    TransferredCO2N2ODirectionsPipe,
    TransportApproachDescriptionPipe,
    UserContactPipe,
    UserFullNamePipe,
    UserInfoResolverPipe,
    UserRolePipe,
    WaReasonDescriptionPipe,
    WorkflowStatusPipe,
    WorkflowTypePipe,
  ],
  exports: [
    AccountStatusPipe,
    AccountTypePipe,
    AccreditationReferenceDocumentNamePipe,
    ActivityCalculationMethodTypePipe,
    AerTaskDescriptionPipe,
    ApplicationTypePipe,
    AppliedTierPipe,
    AuthorityDecisionTypePipe,
    AviationNamePipePipe,
    BigNumberPipe,
    CalculationMethodTypePipe,
    CapacityUnitPipe,
    CapitalizeFirstPipe,
    CessationScopePipePipe,
    CompetentAuthorityLocationPipe,
    CompetentAuthorityPipe,
    CoordinatePipe,
    CountryPipe,
    CrfActivityItemNamePipe,
    DaysRemainingPipe,
    DefaultIfEmptyPipe,
    DeterminationAssessmentPipe,
    DeterminationTypePipe,
    EtsSchemePipe,
    FuelMeteringConditionTypePipe,
    GasPipe,
    GovukDatePipe,
    IncludesAnyPipe,
    IncludesPipe,
    InherentCo2DirectionsPipe,
    InherentCo2InstrumentsPipe,
    InstallationCategoryPipe,
    ItemActionHeaderPipe,
    ItemActionTypePipe,
    ItemLinkPipe,
    ItemNamePipe,
    LegalEntityTypePipe,
    MeasurementUnitTypePipe,
    MonitoringApproachDescriptionPipe,
    MonitoringApproachEmissionDescriptionPipe,
    NonComplianceReasonPipe,
    NotificationTypePipe,
    NotVerifiedReasonTypePipe,
    OverallDecisionTypePipe,
    ParameterTypePipe,
    PermitTransferPartyPipe,
    PfcCalculationMethodPipe,
    PhoneNumberPipe,
    PrtrActivityItemNamePipe,
    RegulatedActivitiesSortPipe,
    RegulatedActivityTypePipe,
    ReportingTypePipe,
    ReviewGroupDecisionPipe,
    SecondsToMinutesPipe,
    SnakeToKebabPipe,
    SourceStreamDescriptionPipe,
    SourceStreamTypePipe,
    StatusApplicationTypePipe,
    TagColorPipe,
    TaskTypeToBreadcrumbPipe,
    TemplateFilePipe,
    TimelineItemLinkPipe,
    TransferredCO2N2ODirectionsPipe,
    TransportApproachDescriptionPipe,
    UserContactPipe,
    UserFullNamePipe,
    UserInfoResolverPipe,
    UserRolePipe,
    WaReasonDescriptionPipe,
    WorkflowStatusPipe,
    WorkflowTypePipe,
  ],
})
export class PipesModule {}
