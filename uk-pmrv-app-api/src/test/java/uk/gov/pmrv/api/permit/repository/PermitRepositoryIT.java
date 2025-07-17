package uk.gov.pmrv.api.permit.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.pmrv.api.SpringValidatorConfiguration;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitEntity;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.domain.additionaldocuments.AdditionalDocuments;
import uk.gov.pmrv.api.permit.domain.common.MeasuredEmissionsSamplingFrequency;
import uk.gov.pmrv.api.permit.domain.common.MeasurementDeviceType;
import uk.gov.pmrv.api.permit.domain.common.ProcedureForm;
import uk.gov.pmrv.api.permit.domain.common.ProcedureOptionalForm;
import uk.gov.pmrv.api.permit.domain.confidentialitystatement.ConfidentialityStatement;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityAccountDTO;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoint;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoints;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSource;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSources;
import uk.gov.pmrv.api.permit.domain.emissionsummaries.EmissionSummaries;
import uk.gov.pmrv.api.permit.domain.emissionsummaries.EmissionSummary;
import uk.gov.pmrv.api.permit.domain.envmanagementsystem.EnvironmentalManagementSystem;
import uk.gov.pmrv.api.permit.domain.envpermitandlicences.EnvironmentalPermitsAndLicences;
import uk.gov.pmrv.api.permit.domain.estimatedannualemissions.EstimatedAnnualEmissions;
import uk.gov.pmrv.api.permit.domain.installationdesc.InstallationDescription;
import uk.gov.pmrv.api.permit.domain.managementprocedures.AssessAndControlRisk;
import uk.gov.pmrv.api.permit.domain.managementprocedures.DataFlowActivities;
import uk.gov.pmrv.api.permit.domain.managementprocedures.ManagementProcedures;
import uk.gov.pmrv.api.permit.domain.managementprocedures.ManagementProceduresDefinition;
import uk.gov.pmrv.api.permit.domain.measurementdevices.MeasurementDeviceOrMethod;
import uk.gov.pmrv.api.permit.domain.measurementdevices.MeasurementDevicesOrMethods;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationActivityData;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationActivityDataTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationAnalysisMethod;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationBiomassFraction;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationBiomassFractionTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationCarbonContent;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationCarbonContentTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationConversionFactor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationConversionFactorTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationEmissionFactor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationEmissionFactorStandardReferenceSource;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationEmissionFactorStandardReferenceSourceType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationEmissionFactorTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationLaboratory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationMethod;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationNetCalorificValue;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationNetCalorificValueTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOxidationFactor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOxidationFactorTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationSamplingFrequency;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.ProcedurePlan;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.ReducedSamplingFrequencyJustification;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.SamplingPlan;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.SamplingPlanDetails;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.ActivityDataTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.CalculationOfPFCMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.CellAndAnodeType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCActivityData;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCCalculationMethod;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCEmissionFactor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCEmissionFactorTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCTier2EmissionFactor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.AppliedStandard;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.CalculationAnalysisMethodData;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.CategoryType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.HighestRequiredTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.InstallationDetailsType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.InstallationEmitter;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.Laboratory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.MeteringUncertainty;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.NoHighestRequiredTierJustification;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.TransferCO2;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.TransferCO2Direction;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.TransferType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.MeasurementAnalysisMethodData;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentCO2Direction;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentReceivingTransferringInstallation;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentReceivingTransferringInstallationEmitter;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.MeasurementInstrumentOwnerType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementBiomassFraction;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2EmissionPointCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2EmissionPointCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementBiomassFractionTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.measuredemissions.MeasurementOfCO2MeasuredEmissions;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.measuredemissions.MeasurementOfCO2MeasuredEmissionsTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OEmissionPointCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OEmissionPointCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OEmissionType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OMonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.TransferN2O;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.TransferN2ODirection;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.measuredemissions.MeasurementOfN2OMeasuredEmissions;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.measuredemissions.MeasurementOfN2OMeasuredEmissionsTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2andn2o.MonitoringTransportNetworkApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2andn2o.TemperaturePressure;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2andn2o.TransferredCO2AndN2OMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2andn2o.TransportCO2AndN2OPipelineSystems;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.MonitoringMethodologyPlans;
import uk.gov.pmrv.api.permit.domain.monitoringreporting.MonitoringReporting;
import uk.gov.pmrv.api.permit.domain.monitoringreporting.MonitoringRole;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.CapacityUnit;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivities;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivity;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivityType;
import uk.gov.pmrv.api.permit.domain.sitediagram.SiteDiagrams;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;
import uk.gov.pmrv.api.permit.domain.uncertaintyanalysis.UncertaintyAnalysis;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(value = {ObjectMapper.class, SpringValidatorConfiguration.class})
@Sql(statements = {
    "INSERT INTO ref_country (id, code, name, official_name) VALUES (1, 'GR', 'Greece', 'The Hellenic Republic')"
})
class PermitRepositoryIT extends AbstractContainerBaseTest {

    private static final String SAMPLE_PERMIT_ID = "UK-1";
    private static final String SAMPLE_PERMIT_ID_2 = "UK-2";

    @Autowired
    private PermitRepository repo;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private Validator validator;

    @Test
    void validate_invalid_permit_should_return_violations() {
        PermitEntity permitEntity = PermitEntity.builder()
            .accountId(1L)
            .permitContainer(PermitContainer.builder()
                .permit(Permit.builder().build())
                .installationOperatorDetails(buildInstallationOperatorDetails())
                .build())
            .build();

        Set<ConstraintViolation<PermitEntity>> violations = validator.validate(permitEntity);
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().allMatch(v -> v.getPropertyPath().toString().contains("permit"))).isTrue();
    }

    @Test
    void validate_valid_permit_should_return_no_violations() {
        PermitEntity permitEntity = PermitEntity.builder()
            .accountId(1L)
            .permitContainer(buildFullPermit())
            .build();

        Set<ConstraintViolation<PermitEntity>> violations = validator.validate(permitEntity);
        assertThat(violations).isEmpty();
    }

    @Test
    void saveAndQuery() {
        Long accountId = 1L;

        PermitContainer permit = buildFullPermit();

        PermitEntity permitEntity = PermitEntity.builder()
            .id(SAMPLE_PERMIT_ID)
            .accountId(accountId)
            .permitContainer(permit)
            .build();

        validator.validate(permitEntity);

        repo.save(permitEntity);

        entityManager.flush();
        entityManager.clear();

        PermitEntity permitEntityFound = repo.getReferenceById(permitEntity.getId());

        assertThat(permitEntityFound.getPermitContainer()).isEqualTo(permit);
        assertThat(permitEntityFound).isNotNull();
    }

    @Test
    void findPermitEntityAccountByAttachmentUuid() {
        Long accountId1 = 1L;
        PermitContainer permit1 = buildFullPermit();
        PermitEntity permitEntity1 = PermitEntity.builder()
            .id(SAMPLE_PERMIT_ID)
            .accountId(accountId1)
            .permitContainer(permit1)
            .build();
        repo.save(permitEntity1);

        Long accountId2 = 2L;
        PermitContainer permit2 = buildFullPermit();
        PermitEntity permitEntity2 = PermitEntity.builder()
            .id(SAMPLE_PERMIT_ID_2)
            .accountId(accountId2)
            .permitContainer(permit2)
            .build();
        repo.save(permitEntity2);

        UUID siteDiagram1 =
            permitEntity1.getPermitContainer().getPermit().getSiteDiagrams().getSiteDiagrams().iterator().next();
        entityManager.flush();
        entityManager.clear();

        Optional<PermitEntityAccountDTO> result1 =
            repo.findPermitEntityAccountByAttachmentUuid(siteDiagram1.toString());
        assertThat(result1).isNotEmpty();
        assertThat(result1.get().getAccountId()).isEqualTo(accountId1);
        assertThat(result1.get().getPermitEntityId()).isEqualTo(permitEntity1.getId());
    }

    @Test
    void findPermitEntityAccountByAttachmentUuid_empty_result() {
        Optional<PermitEntityAccountDTO> result =
            repo.findPermitEntityAccountByAttachmentUuid(UUID.randomUUID().toString());
        assertThat(result).isEmpty();
    }

    private PermitContainer buildFullPermit() {
        PermitContainer permitContainer = PermitContainer.builder()
            .permitType(PermitType.GHGE)
            .permit(Permit.builder()
                .additionalDocuments(buildAdditionalDocuments())
                .environmentalPermitsAndLicences(buildEnvPermitsAndLicences())
                .estimatedAnnualEmissions(buildEstimatedAnnualEmissions())
                .installationDescription(buildInstallationDescription())
                .regulatedActivities(buildRegulatedActivities())
                .monitoringMethodologyPlans(buildMonitoringMethodologyPlan())
                .measurementDevicesOrMethods(buildMeasurementDevicesOrMethods())
                .sourceStreams(buildSourceStreams())
                .emissionSources(buildEmissionSources())
                .emissionPoints(buildEmissionPoints())
                .emissionSummaries(buildEmissionSummaries())
                .siteDiagrams(buildSiteDiagrams())
                .abbreviations(buildAbbreviations())
                .confidentialityStatement(buildConfidentialityStatement())
                .monitoringApproaches(buildMonitoringApproaches())
                .uncertaintyAnalysis(UncertaintyAnalysis.builder().exist(false).attachments(Set.of()).build())
                .managementProcedures(buildManagementProcedures())
                .build())
            .installationOperatorDetails(buildInstallationOperatorDetails())
            .build();
        permitContainer.setPermitAttachments(
            permitContainer.getPermit().getPermitSectionAttachmentIds()
                .stream()
                .collect(Collectors.toMap(Function.identity(), UUID::toString)));
        return permitContainer;
    }

    private RegulatedActivities buildRegulatedActivities() {
        return RegulatedActivities.builder()
            .regulatedActivities(List.of(
                RegulatedActivity.builder()
                    .id(UUID.randomUUID().toString())
                    .type(RegulatedActivityType.COMBUSTION)
                    .capacity(BigDecimal.valueOf(1L))
                    .capacityUnit(CapacityUnit.TONNES_PER_DAY).build(),
                    RegulatedActivity.builder()
                            .id(UUID.randomUUID().toString())
                            .type(RegulatedActivityType.UPSTREAM_GHG_REMOVAL)
                            .capacity(null)
                            .capacityUnit(null).build()
                    ))
            .build();
    }

    private EstimatedAnnualEmissions buildEstimatedAnnualEmissions() {
        return EstimatedAnnualEmissions.builder()
            .quantity(BigDecimal.valueOf(25000.1))
            .build();
    }

    private EnvironmentalPermitsAndLicences buildEnvPermitsAndLicences() {
        return EnvironmentalPermitsAndLicences.builder()
            .exist(false)
            .build();
    }

    private InstallationOperatorDetails buildInstallationOperatorDetails() {
        return InstallationOperatorDetails.builder()
            .installationName("installationName")
            .siteName("siteName")
            .installationLocation(LocationOnShoreDTO.builder()
                .type(LocationType.ONSHORE)
                .gridReference("ST330000")
                .address(AddressDTO.builder()
                    .line1("line1")
                    .city("city")
                    .country("GR")
                    .postcode("postcode")
                    .build())
                .build())
            .operator("operator")
            .operatorType(LegalEntityType.LIMITED_COMPANY)
            .companyReferenceNumber("408812")
            .operatorDetailsAddress(AddressDTO.builder()
                .line1("line1")
                .city("city")
                .country("GR")
                .postcode("postcode")
                .build())
            .build();
    }

    private InstallationDescription buildInstallationDescription() {
        return InstallationDescription.builder()
            .mainActivitiesDesc("mainActivitiesDesc")
            .siteDescription("siteDescription")
            .build();
    }

    private ManagementProcedures buildManagementProcedures() {
        ManagementProceduresDefinition managementProceduresDefinition = buildManagementAndProceduresSection();
        AssessAndControlRisk assessAndControlRisk = buildAssessAndControlRiskSection();
        return ManagementProcedures.builder()
            .monitoringReporting(buildMonitoringReporting())
            .dataFlowActivities(buildDataFlowActivitiesSection())
            .assessAndControlRisk(assessAndControlRisk)
            .assignmentOfResponsibilities(managementProceduresDefinition)
            .controlOfOutsourcedActivities(managementProceduresDefinition)
            .correctionsAndCorrectiveActions(managementProceduresDefinition)
            .monitoringPlanAppropriateness(managementProceduresDefinition)
            .qaDataFlowActivities(managementProceduresDefinition)
            .qaMeteringAndMeasuringEquipment(managementProceduresDefinition)
            .recordKeepingAndDocumentation(managementProceduresDefinition)
            .reviewAndValidationOfData(managementProceduresDefinition)
            .environmentalManagementSystem(buildEnvironmentalManagementSystem())
            .build();
    }

    private ManagementProceduresDefinition buildManagementAndProceduresSection() {
        return ManagementProceduresDefinition.builder()
            .procedureDocumentName("procDocName")
            .procedureReference("procRef")
            .diagramReference("diagramRef")
            .procedureDescription("procDesc")
            .responsibleDepartmentOrRole("dep")
            .locationOfRecords("loc")
            .itSystemUsed("system")
            .appliedStandards("standards")
            .build();
    }

    private AssessAndControlRisk buildAssessAndControlRiskSection() {
        return AssessAndControlRisk.builder()
            .procedureDocumentName("procDocName")
            .procedureReference("procRef")
            .diagramReference("diagramRef")
            .procedureDescription("procDesc")
            .responsibleDepartmentOrRole("dep")
            .locationOfRecords("loc")
            .itSystemUsed("system")
            .appliedStandards("standards")
            .riskAssessmentAttachments(new HashSet<>(Arrays.asList(UUID.randomUUID(), UUID.randomUUID())))
            .build();
    }

    private DataFlowActivities buildDataFlowActivitiesSection() {
        return DataFlowActivities.builder()
            .procedureDocumentName("procDocName")
            .procedureReference("procRef")
            .diagramReference("diagramRef")
            .procedureDescription("procDesc")
            .responsibleDepartmentOrRole("dep")
            .locationOfRecords("loc")
            .itSystemUsed("system")
            .appliedStandards("standards")
            .primaryDataSources("primaryDs")
            .processingSteps("steps")
            .build();
    }

    private MonitoringReporting buildMonitoringReporting() {
        return MonitoringReporting.builder()
            .monitoringRoles(List.of(MonitoringRole.builder().jobTitle("jobTitle").mainDuties("mainDuties").build()))
            .organisationCharts(Set.of(UUID.randomUUID()))
            .build();
    }

    private EnvironmentalManagementSystem buildEnvironmentalManagementSystem() {
        return EnvironmentalManagementSystem.builder()
            .exist(true)
            .certified(true)
            .certificationStandard("ISO-3476")
            .build();
    }

    private MonitoringMethodologyPlans buildMonitoringMethodologyPlan() {
        return MonitoringMethodologyPlans.builder()
            .exist(true)
            .plans(Set.of(UUID.randomUUID()))
            .build();
    }

    private MeasurementDevicesOrMethods buildMeasurementDevicesOrMethods() {
        return MeasurementDevicesOrMethods.builder()
            .measurementDevicesOrMethods(List.of(MeasurementDeviceOrMethod
                .builder()
                .id(UUID.randomUUID().toString())
                .reference("reference")
                .type(MeasurementDeviceType.LEVEL_GAUGE)
                .measurementRange("range")
                .meteringRangeUnits("units")
                .location("location")
                .build()))
            .build();
    }

    private SourceStreams buildSourceStreams() {
        return SourceStreams.builder()
            .sourceStreams(List.of(SourceStream
                .builder()
                .id(UUID.randomUUID().toString())
                .reference("ref")
                .type(SourceStreamType.AMMONIA_FUEL_AS_PROCESS_INPUT)
                .description(SourceStreamDescription.BIODIESELS)
                .build()))
            .build();
    }

    private EmissionSources buildEmissionSources() {
        return EmissionSources.builder()
            .emissionSources(List.of(
                EmissionSource.builder()
                    .id(UUID.randomUUID().toString())
                    .reference("reference")
                    .description("description")
                    .build()))
            .build();
    }

    private EmissionPoints buildEmissionPoints() {
        return EmissionPoints.builder()
            .emissionPoints(List.of(EmissionPoint.builder()
                .id(UUID.randomUUID().toString())
                .reference("reference")
                .description("description")
                .build()))
            .build();
    }

    private EmissionSummaries buildEmissionSummaries() {
        return EmissionSummaries.builder()
            .emissionSummaries(List.of(EmissionSummary.builder()
                .sourceStream(UUID.randomUUID().toString())
                .emissionSources(Set.of(UUID.randomUUID().toString()))
                .emissionPoints(Set.of(UUID.randomUUID().toString()))
                .regulatedActivity(UUID.randomUUID().toString())
                .excludedRegulatedActivity(false)
                .build()))
            .build();
    }

    private SiteDiagrams buildSiteDiagrams() {
        return SiteDiagrams.builder()
            .siteDiagrams(Set.of(UUID.randomUUID()))
            .build();
    }

    private Abbreviations buildAbbreviations() {
        return Abbreviations.builder()
            .exist(false)
            .build();
    }

    private ConfidentialityStatement buildConfidentialityStatement() {
        return ConfidentialityStatement.builder().exist(false).build();
    }

    private AdditionalDocuments buildAdditionalDocuments() {
        return AdditionalDocuments.builder().exist(false).build();
    }

    private MonitoringApproaches buildMonitoringApproaches() {
        EnumMap<MonitoringApproachType, PermitMonitoringApproachSection> monitoringApproaches =
            new EnumMap<>(MonitoringApproachType.class);

        monitoringApproaches.put(MonitoringApproachType.CALCULATION_CO2, buildCalculationMonitorApproach());
        monitoringApproaches.put(MonitoringApproachType.INHERENT_CO2, buildInherentCO2MonitoringApproach());
        monitoringApproaches.put(MonitoringApproachType.TRANSFERRED_CO2_N2O, buildTransferredCO2MonitoringApproach());
        monitoringApproaches.put(MonitoringApproachType.FALLBACK, buildFallbackMonitoringApproach());
        monitoringApproaches.put(MonitoringApproachType.MEASUREMENT_N2O, buildN2OMonitoringApproach());
        monitoringApproaches.put(MonitoringApproachType.MEASUREMENT_CO2, buildMeasurementMonitoringApproach());
        monitoringApproaches.put(MonitoringApproachType.CALCULATION_PFC, buildPFCMonitoringApproach());

        return MonitoringApproaches.builder().monitoringApproaches(monitoringApproaches).build();
    }

    private CalculationOfPFCMonitoringApproach buildPFCMonitoringApproach() {
        return CalculationOfPFCMonitoringApproach.builder()
            .type(MonitoringApproachType.CALCULATION_PFC)
            .approachDescription("pfc approach description")
            .cellAndAnodeTypes(List.of(
                CellAndAnodeType.builder()
                    .cellType("cell type")
                    .anodeType("anode type")
                    .build()
            ))
            .tier2EmissionFactor(PFCTier2EmissionFactor.builder().exist(false).build())
            .collectionEfficiency(buildProcedureForm("collectionEfficiency"))
            .sourceStreamCategoryAppliedTiers(List.of(PFCSourceStreamCategoryAppliedTier.builder()
                .sourceStreamCategory(PFCSourceStreamCategory.builder()
                    .sourceStream(UUID.randomUUID().toString())
                    .emissionSources(Set.of(UUID.randomUUID().toString()))
                    .emissionPoints(Set.of(UUID.randomUUID().toString()))
                    .annualEmittedCO2Tonnes(BigDecimal.valueOf(2600.12))
                    .categoryType(CategoryType.MAJOR)
                    .calculationMethod(PFCCalculationMethod.OVERVOLTAGE)
                    .build())
                .activityData(PFCActivityData.builder()
                    .massBalanceApproachUsed(true)
                    .tier(ActivityDataTier.TIER_4)
                    .highestRequiredTier(HighestRequiredTier.builder().build())
                    .build())
                .emissionFactor(PFCEmissionFactor.builder()
                    .tier(PFCEmissionFactorTier.TIER_1)
                    .highestRequiredTier(HighestRequiredTier.builder()
                        .isHighestRequiredTier(Boolean.FALSE)
                        .noHighestRequiredTierJustification(NoHighestRequiredTierJustification.builder()
                            .isTechnicallyInfeasible(Boolean.TRUE)
                            .technicalInfeasibilityExplanation("explain")
                            .isCostUnreasonable(Boolean.TRUE)
                            .build())
                        .build())
                    .build())
                .build()))
            .build();
    }

    private MeasurementOfCO2MonitoringApproach buildMeasurementMonitoringApproach() {
        return MeasurementOfCO2MonitoringApproach.builder()
            .hasTransfer(true)
            .type(MonitoringApproachType.MEASUREMENT_CO2)
            .approachDescription("measurementApproachDescription")
            .emissionDetermination(buildProcedureForm("emissionDetermination"))
            .referencePeriodDetermination(buildProcedureForm("referencePeriodDetermination"))
            .gasFlowCalculation(ProcedureOptionalForm.builder().exist(false).build())
            .biomassEmissions(ProcedureOptionalForm.builder().exist(false).build())
            .corroboratingCalculations(buildProcedureForm("corroboratingCalculations"))
            .emissionPointCategoryAppliedTiers(List.of(MeasurementOfCO2EmissionPointCategoryAppliedTier.builder()
                .emissionPointCategory(MeasurementOfCO2EmissionPointCategory.builder()
                    .transfer(TransferCO2.builder()
                        .transferDirection(TransferCO2Direction.RECEIVED_FROM_ANOTHER_INSTALLATION)
                        .entryAccountingForTransfer(true)
                        .transferType(TransferType.TRANSFER_CO2)
                        .installationDetailsType(InstallationDetailsType.INSTALLATION_EMITTER)
                        .installationEmitter(InstallationEmitter.builder()
                            .emitterId("EM123")
                            .email("test@test.com")
                            .build())
                        .build())
                    .emissionPoint(UUID.randomUUID().toString())
                    .sourceStreams(Set.of(UUID.randomUUID().toString()))
                    .emissionSources(Set.of(UUID.randomUUID().toString()))
                    .annualEmittedCO2Tonnes(BigDecimal.valueOf(26000.123))
                    .categoryType(CategoryType.MAJOR).build())
                .measuredEmissions(buildMeasMeasuredEmissions())
                .appliedStandard(buildAppliedStandard())
                .biomassFraction(buildMeasurementBiomassFraction())
                .build()))
            .build();
    }

    private MeasurementOfN2OMonitoringApproach buildN2OMonitoringApproach() {
        return MeasurementOfN2OMonitoringApproach.builder()
            .hasTransfer(true)
            .type(MonitoringApproachType.MEASUREMENT_N2O)
            .approachDescription("approachDescription")
            .emissionDetermination(buildProcedureForm("emissionDetermination"))
            .referenceDetermination(buildProcedureForm("determinationReference"))
            .operationalManagement(buildProcedureForm("operationalManagement"))
            .nitrousOxideEmissionsDetermination(buildProcedureForm("determinationNitrousOxideEmissions"))
            .nitrousOxideConcentrationDetermination(buildProcedureForm("determinationNitrousOxideConcentration"))
            .quantityProductDetermination(buildProcedureForm("determinationQuantityProduct"))
            .quantityMaterials(buildProcedureForm("quantityMaterials"))
            .gasFlowCalculation(ProcedureOptionalForm.builder().exist(false).build())
            .emissionPointCategoryAppliedTiers(List.of(MeasurementOfN2OEmissionPointCategoryAppliedTier.builder()
                .emissionPointCategory(MeasurementOfN2OEmissionPointCategory.builder()
                    .transfer(TransferN2O.builder()
                        .transferDirection(TransferN2ODirection.EXPORTED_TO_LONG_TERM_FACILITY)
                        .entryAccountingForTransfer(true)
                        .transferType(TransferType.TRANSFER_N2O)
                        .installationDetailsType(InstallationDetailsType.INSTALLATION_EMITTER)
                        .installationEmitter(InstallationEmitter.builder()
                            .emitterId("EM123")
                            .email("test@test.com")
                            .build())
                        .build())
                    .emissionPoint(UUID.randomUUID().toString())
                    .sourceStreams(Set.of(UUID.randomUUID().toString()))
                    .emissionSources(Set.of(UUID.randomUUID().toString()))
                    .emissionType(MeasurementOfN2OEmissionType.ABATED)
                    .monitoringApproachType(MeasurementOfN2OMonitoringApproachType.CALCULATION)
                    .annualEmittedCO2Tonnes(BigDecimal.valueOf(26000.1))
                    .categoryType(CategoryType.MAJOR)
                    .build())
                .measuredEmissions(buildN2OMeasuredEmissions())
                .appliedStandard(buildAppliedStandard())
                .build()))
            .build();
    }

    private FallbackMonitoringApproach buildFallbackMonitoringApproach() {
        return FallbackMonitoringApproach.builder()
            .type(MonitoringApproachType.FALLBACK)
            .approachDescription("fallbackApproachDescription")
            .justification("fallbackApproachJustification")
            .annualUncertaintyAnalysis(buildProcedureForm("annualUncertaintyAnalysis"))
            .sourceStreamCategoryAppliedTiers(List.of(FallbackSourceStreamCategoryAppliedTier.builder()
                .sourceStreamCategory(FallbackSourceStreamCategory.builder()
                    .sourceStream(UUID.randomUUID().toString())
                    .emissionSources(Set.of(UUID.randomUUID().toString()))
                    .annualEmittedCO2Tonnes(BigDecimal.valueOf(26000.1))
                    .categoryType(CategoryType.MAJOR)
                    .measurementDevicesOrMethods(Set.of(UUID.randomUUID().toString()))
                    .uncertainty(MeteringUncertainty.LESS_OR_EQUAL_2_5)
                    .build())
                .build()))
            .build();
    }

    private TransferredCO2AndN2OMonitoringApproach buildTransferredCO2MonitoringApproach() {
        return TransferredCO2AndN2OMonitoringApproach.builder().type(MonitoringApproachType.TRANSFERRED_CO2_N2O)
            .deductionsToAmountOfTransferredCO2(ProcedureOptionalForm.builder()
                .exist(false)
                .build())
            .monitoringTransportNetworkApproach(MonitoringTransportNetworkApproach.METHOD_A)
            .geologicalStorage(ProcedureOptionalForm.builder().exist(false).build())
            .transportCO2AndN2OPipelineSystems(TransportCO2AndN2OPipelineSystems.builder()
                .exist(true)
                .procedureForLeakageEvents(ProcedureOptionalForm.builder()
                    .exist(false)
                    .build())
                .temperaturePressure(TemperaturePressure.builder()
                    .exist(false)
                    .build())
                .proceduresForTransferredCO2AndN2O(buildProcedureForm("transferOfCO2"))
                .build()
            ).build();
    }

    private InherentCO2MonitoringApproach buildInherentCO2MonitoringApproach() {
        return InherentCO2MonitoringApproach.builder().type(MonitoringApproachType.INHERENT_CO2)
            .inherentReceivingTransferringInstallations(List.of(
                InherentReceivingTransferringInstallation.builder()
                    .inherentCO2Direction(InherentCO2Direction.EXPORTED_TO_ETS_INSTALLATION)
                    .installationDetailsType(InstallationDetailsType.INSTALLATION_EMITTER)
                    .inherentReceivingTransferringInstallationDetailsType(InherentReceivingTransferringInstallationEmitter.builder()
                        .installationEmitter(InstallationEmitter.builder()
                            .email("test@test.com")
                            .emitterId("emitter")
                            .build())
                        .build())
                    .measurementInstrumentOwnerTypes(Set.of(
                        MeasurementInstrumentOwnerType.INSTRUMENTS_BELONGING_TO_THE_OTHER_INSTALLATION,
                        MeasurementInstrumentOwnerType.INSTRUMENTS_BELONGING_TO_YOUR_INSTALLATION)
                    )
                    .totalEmissions(BigDecimal.valueOf(200.32))
                    .build()
            )).build();
    }

    private CalculationOfCO2MonitoringApproach buildCalculationMonitorApproach() {
        return CalculationOfCO2MonitoringApproach.builder()
            .hasTransfer(true)
            .type(MonitoringApproachType.CALCULATION_CO2)
            .samplingPlan(SamplingPlan.builder().exist(true)
                .details(SamplingPlanDetails.builder()
                    .analysis(buildProcedureForm("analysis"))
                    .procedurePlan(buildProcedurePlan("samplingplanprocedure"))
                    .appropriateness(buildProcedureForm("appropriateness"))
                    .yearEndReconciliation(ProcedureOptionalForm.builder().exist(false).build())
                    .build())
                .build())
            .approachDescription("description")
            .sourceStreamCategoryAppliedTiers(List.of(CalculationSourceStreamCategoryAppliedTier.builder()
                .sourceStreamCategory(CalculationSourceStreamCategory.builder()
                    .sourceStream(UUID.randomUUID().toString())
                    .emissionSources(Set.of(UUID.randomUUID().toString()))
                    .annualEmittedCO2Tonnes(BigDecimal.valueOf(26000.1))
                    .categoryType(CategoryType.MAJOR)
                    .calculationMethod(CalculationMethod.MASS_BALANCE)
                    .transfer(TransferCO2.builder()
                        .transferDirection(TransferCO2Direction.EXPORTED_FOR_PRECIPITATED_CALCIUM)
                        .entryAccountingForTransfer(true)
                        .transferType(TransferType.TRANSFER_CO2)
                        .installationDetailsType(InstallationDetailsType.INSTALLATION_EMITTER)
                        .installationEmitter(InstallationEmitter.builder()
                            .emitterId("EM123")
                            .email("test@test.com")
                            .build())
                        .build())
                    .build())
                .emissionFactor(CalculationEmissionFactor.builder()
                    .exist(true)
                    .tier(CalculationEmissionFactorTier.TIER_3)
                    .highestRequiredTier(HighestRequiredTier.builder().build())
                    .oneThirdRule(Boolean.FALSE)
                    .defaultValueApplied(Boolean.TRUE)
                    .standardReferenceSource(CalculationEmissionFactorStandardReferenceSource.builder().type(CalculationEmissionFactorStandardReferenceSourceType.IN_HOUSE_CALCULATION).build())
                    .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder()
                        .analysisMethodUsed(Boolean.TRUE)
                        .analysisMethods(List.of(CalculationAnalysisMethod.builder()
                            .analysis("dfdfd")
                            .samplingFrequency(CalculationSamplingFrequency.OTHER)
                            .samplingFrequencyOtherDetails("other")
                            .frequencyMeetsMinRequirements(false)
                            .laboratory(
                                CalculationLaboratory.builder().laboratoryName("fdfdfd").laboratoryAccredited(true)
                                    .build())
                            .reducedSamplingFrequencyJustification(ReducedSamplingFrequencyJustification.builder()
                                .isCostUnreasonable(Boolean.TRUE)
                                .build())
                            .build()))
                        .build())
                    .build())
                .oxidationFactor(CalculationOxidationFactor.builder()
                    .exist(true)
                    .tier(CalculationOxidationFactorTier.TIER_3)
                    .highestRequiredTier(HighestRequiredTier.builder().build())
                    .defaultValueApplied(false)
                    .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder()
                        .analysisMethodUsed(true)
                        .analysisMethods(List.of(CalculationAnalysisMethod.builder()
                            .analysis("dfdfd")
                            .samplingFrequency(CalculationSamplingFrequency.OTHER)
                            .samplingFrequencyOtherDetails("other")
                            .frequencyMeetsMinRequirements(false)
                            .laboratory(
                                CalculationLaboratory.builder().laboratoryName("fdfdfd").laboratoryAccredited(true)
                                    .build())
                            .reducedSamplingFrequencyJustification(ReducedSamplingFrequencyJustification.builder()
                                .isCostUnreasonable(Boolean.TRUE)
                                .build())
                            .build()))
                        .build())
                    .build())
                .carbonContent(CalculationCarbonContent.builder()
                    .exist(true)
                    .tier(CalculationCarbonContentTier.TIER_2A)
                    .highestRequiredTier(HighestRequiredTier.builder().isHighestRequiredTier(Boolean.TRUE).build())
                    .defaultValueApplied(Boolean.FALSE)
                    .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder()
                        .analysisMethodUsed(Boolean.TRUE)
                        .analysisMethods(List.of(CalculationAnalysisMethod.builder()
                            .analysis("Carbon content analysis method desc")
                            .samplingFrequency(CalculationSamplingFrequency.DAILY)
                            .frequencyMeetsMinRequirements(false)
                            .laboratory(
                                CalculationLaboratory.builder().laboratoryName("fdfdfd").laboratoryAccredited(true)
                                    .build())
                            .reducedSamplingFrequencyJustification(ReducedSamplingFrequencyJustification.builder()
                                .isOneThirdRuleAndSampling(Boolean.TRUE)
                                .build())
                            .build()))
                        .build()).build())
                .conversionFactor(CalculationConversionFactor.builder()
                    .exist(true)
                    .tier(CalculationConversionFactorTier.TIER_2)
                    .highestRequiredTier(HighestRequiredTier.builder().build())
                    .defaultValueApplied(false)
                    .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder()
                        .analysisMethodUsed(true)
                        .analysisMethods(List.of(CalculationAnalysisMethod.builder()
                            .analysis("Conversion factor analysis method desc")
                            .samplingFrequency(CalculationSamplingFrequency.CONTINUOUS)
                            .frequencyMeetsMinRequirements(false)
                            .laboratory(
                                CalculationLaboratory.builder().laboratoryName("fdfdfd").laboratoryAccredited(true)
                                    .build())
                            .reducedSamplingFrequencyJustification(ReducedSamplingFrequencyJustification.builder()
                                .isCostUnreasonable(Boolean.TRUE)
                                .build())
                            .build()))
                        .build()).build())
                .activityData(CalculationActivityData.builder()
                    .measurementDevicesOrMethods(Set.of(UUID.randomUUID().toString()))
                    .uncertainty(MeteringUncertainty.LESS_OR_EQUAL_5_0)
                    .tier(CalculationActivityDataTier.TIER_1)
                    .highestRequiredTier(HighestRequiredTier.builder()
                        .isHighestRequiredTier(Boolean.FALSE)
                        .noHighestRequiredTierJustification(NoHighestRequiredTierJustification.builder()
                            .isTechnicallyInfeasible(Boolean.TRUE)
                            .technicalInfeasibilityExplanation("explain")
                            .isCostUnreasonable(Boolean.TRUE)
                            .build())
                        .build())
                    .build())
                .netCalorificValue(CalculationNetCalorificValue.builder()
                    .exist(true)
                    .tier(CalculationNetCalorificValueTier.TIER_3)
                    .highestRequiredTier(HighestRequiredTier.builder().build())
                    .defaultValueApplied(false)
                    .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder()
                        .analysisMethodUsed(true)
                        .analysisMethods(List.of(CalculationAnalysisMethod.builder()
                            .analysis("dfdfd")
                            .samplingFrequency(CalculationSamplingFrequency.OTHER)
                            .samplingFrequencyOtherDetails("other")
                            .frequencyMeetsMinRequirements(false)
                            .laboratory(
                                CalculationLaboratory.builder().laboratoryName("fdfdfd").laboratoryAccredited(true)
                                    .build())
                            .reducedSamplingFrequencyJustification(ReducedSamplingFrequencyJustification.builder()
                                .isCostUnreasonable(Boolean.TRUE)
                                .build())
                            .build()))
                        .build()).build())
                .biomassFraction(CalculationBiomassFraction.builder()
                    .exist(true)
                    .tier(CalculationBiomassFractionTier.TIER_3)
                    .highestRequiredTier(HighestRequiredTier.builder().build())
                    .defaultValueApplied(false)
                    .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder().analysisMethodUsed(true)
                        .analysisMethods(List.of(CalculationAnalysisMethod.builder()
                            .analysis("analysis")
                            .samplingFrequency(CalculationSamplingFrequency.OTHER)
                            .samplingFrequencyOtherDetails("other")
                            .frequencyMeetsMinRequirements(false)
                            .laboratory(
                                CalculationLaboratory.builder().laboratoryName("fdfdfd").laboratoryAccredited(true)
                                    .build())
                            .reducedSamplingFrequencyJustification(ReducedSamplingFrequencyJustification.builder()
                                .isCostUnreasonable(Boolean.TRUE)
                                .build())
                            .build()))
                        .build()).build())
                .build()))
            .build();
    }

    private ProcedureForm buildProcedureForm(String value) {
        return ProcedureForm.builder()
            .procedureDescription("procedureDescription" + value)
            .procedureDocumentName("procedureDocumentName" + value)
            .procedureReference("procedureReference" + value)
            .diagramReference("diagramReference" + value)
            .responsibleDepartmentOrRole("responsibleDepartmentOrRole" + value)
            .locationOfRecords("locationOfRecords" + value)
            .itSystemUsed("itSystemUsed" + value)
            .appliedStandards("appliedStandards" + value)
            .build();
    }

    private MeasurementOfN2OMeasuredEmissions buildN2OMeasuredEmissions() {
        return MeasurementOfN2OMeasuredEmissions.builder()
            .measurementDevicesOrMethods(Set.of(UUID.randomUUID().toString()))
            .samplingFrequency(MeasuredEmissionsSamplingFrequency.ANNUALLY)
            .tier(MeasurementOfN2OMeasuredEmissionsTier.TIER_2)
            .highestRequiredTier(HighestRequiredTier.builder()
                .isHighestRequiredTier(Boolean.FALSE)
                .noHighestRequiredTierJustification(NoHighestRequiredTierJustification.builder()
                    .isCostUnreasonable(Boolean.TRUE)
                    .files(Set.of(UUID.randomUUID()))
                    .build())
                .build())
            .build();
    }

    private MeasurementOfCO2MeasuredEmissions buildMeasMeasuredEmissions() {
        return MeasurementOfCO2MeasuredEmissions.builder()
            .measurementDevicesOrMethods(Set.of(UUID.randomUUID().toString()))
            .samplingFrequency(MeasuredEmissionsSamplingFrequency.ANNUALLY)
            .tier(MeasurementOfCO2MeasuredEmissionsTier.TIER_4)
            .highestRequiredTier(HighestRequiredTier.builder().build())
            .build();
    }

    private ProcedurePlan buildProcedurePlan(String value) {
        return ProcedurePlan.builder()
            .procedureDescription("procedureDescription" + value)
            .procedureDocumentName("procedureDocumentName" + value)
            .procedureReference("procedureReference" + value)
            .diagramReference("diagramReference" + value)
            .responsibleDepartmentOrRole("responsibleDepartmentOrRole" + value)
            .locationOfRecords("locationOfRecords" + value)
            .itSystemUsed("itSystemUsed" + value)
            .appliedStandards("appliedStandards" + value)
            .procedurePlanIds(Set.of(UUID.randomUUID(), UUID.randomUUID()))
            .build();
    }

    private AppliedStandard buildAppliedStandard() {
        return AppliedStandard.builder()
            .parameter("parameter")
            .appliedStandard("applied standard")
            .deviationFromAppliedStandardExist(true)
            .deviationFromAppliedStandardDetails("deviation details")
            .laboratory(Laboratory.builder().laboratoryName("lab").laboratoryAccredited(true).build())
            .build();
    }

    private MeasurementBiomassFraction buildMeasurementBiomassFraction() {
        return MeasurementBiomassFraction.builder()
                .exist(true)
                .tier(MeasurementBiomassFractionTier.TIER_1)
                .highestRequiredTier(HighestRequiredTier.builder()
                        .isHighestRequiredTier(Boolean.FALSE)
                        .noHighestRequiredTierJustification(NoHighestRequiredTierJustification.builder()
                                .isTechnicallyInfeasible(Boolean.TRUE)
                                .technicalInfeasibilityExplanation("explain")
                                .isCostUnreasonable(Boolean.TRUE)
                                .build())
                        .build())
                .calculationAnalysisMethodData(MeasurementAnalysisMethodData.builder().analysisMethodUsed(false).build())
                .build();
    }
}
