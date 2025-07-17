package uk.gov.pmrv.api.emissionsmonitoringplan.common.repo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.Month;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.persistence.EntityManager;
import jakarta.validation.Validator;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.pmrv.api.SpringValidatorConfiguration;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanEntity;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpProcedureForm;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments.EmpAdditionalDocuments;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.EmpAccountDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.AircraftTypeInfo;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.FuelConsumptionMeasuringMethod;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.enumeration.FuelType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentification;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentificationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.IndividualOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperationScope;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperatorType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.repository.EmissionsMonitoringPlanRepository;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.datagaps.EmpDataGapsCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.CertEmissionsType;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.CertMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachTypeCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources.AircraftTypeDetailsCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources.EmpEmissionSourcesCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources.enumeration.FuelTypeCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.flightaircraftprocedures.EmpFlightAndAircraftProceduresCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.flightaircraftprocedures.EmpOperatingStatePairsCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.flightaircraftprocedures.EmpOperatingStatePairsCorsiaDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.managementprocedures.EmpDataManagement;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.managementprocedures.EmpManagementProceduresCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.managementprocedures.EmpMonitoringReportingRoleCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.managementprocedures.EmpMonitoringReportingRolesCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.managementprocedures.EmpProcedureDescription;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.ActivitiesDescriptionCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AirOperatingCertificateCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.SubsidiaryCompanyCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.applicationtimeframe.EmpApplicationTimeframeInfo;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.SmallEmittersMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsources.AircraftTypeDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsources.EmpEmissionSources;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsreductionclaim.EmpEmissionsReductionClaim;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.flightaircraftprocedures.EmpFlightAndAircraftProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.managementprocedures.EmpManagementProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.managementprocedures.EmpMonitoringReportingRole;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.managementprocedures.EmpMonitoringReportingRoles;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.ActivitiesDescription;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AirOperatingCertificate;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.OperatingLicense;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(value = {SpringValidatorConfiguration.class})
@Sql(statements = {
        "INSERT INTO ref_country (id, code, name, official_name) VALUES (1, 'GR', 'Greece', 'The Hellenic Republic')"
})
class EmissionsMonitoringPlanRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private EmissionsMonitoringPlanRepository repository;

    @Autowired

    private EntityManager entityManager;

    @Autowired
    private Validator validator;

    @Test
    void findByAccountId_UkEts() {
        final String empId = "empId";
        final Long accountId = 1L;
        EmissionsMonitoringPlanEntity empEntity = createEmpEntityUkEts(empId, accountId, "fileDocumentId");
        flushAndClear();
        final Optional<EmissionsMonitoringPlanEntity> actual = repository.findByAccountId(accountId);
        assertFalse(actual.isEmpty());
        assertEquals(empEntity.getId(), actual.get().getId());
        assertEquals(empEntity.getAccountId(), actual.get().getAccountId());
        assertEquals(empEntity.getFileDocumentUuid(), actual.get().getFileDocumentUuid());
        final EmissionsMonitoringPlanUkEtsContainer empContainer = (EmissionsMonitoringPlanUkEtsContainer) empEntity.getEmpContainer();
        assertNotNull(empContainer.getEmissionsMonitoringPlan().getFlightAndAircraftProcedures());
        assertNotNull(empContainer.getEmissionsMonitoringPlan().getEmissionSources());
    }

    @Test
    void findByAccountId_Corsia() {
        final String empId = "empId2";
        final Long accountId = 2L;
        EmissionsMonitoringPlanEntity empEntity = createEmpEntityCorsia(empId, accountId, "fileDocumentId");
        flushAndClear();
        final Optional<EmissionsMonitoringPlanEntity> actual = repository.findByAccountId(accountId);
        assertFalse(actual.isEmpty());
        assertEquals(empEntity.getId(), actual.get().getId());
        assertEquals(empEntity.getAccountId(), actual.get().getAccountId());
        assertEquals(empEntity.getFileDocumentUuid(), actual.get().getFileDocumentUuid());
        final EmissionsMonitoringPlanCorsiaContainer empContainer = (EmissionsMonitoringPlanCorsiaContainer) empEntity.getEmpContainer();
        assertNotNull(empContainer.getEmissionsMonitoringPlan().getAbbreviations());
        assertNotNull(empContainer.getEmissionsMonitoringPlan().getAdditionalDocuments());
    }

    @Test
    void findEmpIdByAccountId_UkEts() {
        final String empId = "empId";
        final Long accountId = 1L;
        EmissionsMonitoringPlanEntity empEntity = createEmpEntityUkEts(empId, accountId, "fileDocumentId");

        flushAndClear();

        Optional<String> actual = repository.findEmpIdByAccountId(accountId);

        assertFalse(actual.isEmpty());
        assertEquals(empEntity.getId(), actual.get());
    }

    @Test
    void findEmpIdByAccountId_Corsia() {
        final String empId = "empId2";
        final Long accountId = 2L;
        EmissionsMonitoringPlanEntity empEntity = createEmpEntityCorsia(empId, accountId, "fileDocumentId");

        flushAndClear();

        Optional<String> actual = repository.findEmpIdByAccountId(accountId);

        assertFalse(actual.isEmpty());
        assertEquals(empEntity.getId(), actual.get());
    }

    @Test
    void findEmpAccountByAttachmentUuid() {
        Long accountId = 1L;
        EmissionsMonitoringPlanEntity empEntity = createEmpEntityCorsia("empId", accountId, "uuid");

        EmissionsMonitoringPlanCorsiaContainer empContainer = (EmissionsMonitoringPlanCorsiaContainer) empEntity.getEmpContainer();
        UUID dataFlowDiagramUuid = empContainer.getEmissionsMonitoringPlan().getManagementProcedures().getDataManagement().getDataFlowDiagram();

        repository.save(empEntity);
        flushAndClear();

        Optional<EmpAccountDTO> res = repository.findEmpAccountByAttachmentUuid(dataFlowDiagramUuid.toString());

        assertThat(res).isNotEmpty();
        assertThat(res.get().getAccountId()).isEqualTo(accountId);
        assertThat(res.get().getEmpId()).isEqualTo(empEntity.getId());
    }

    @Test
    void findEmpAccountByAttachmentUuid_empty() {
        Optional<EmpAccountDTO> result = repository.findEmpAccountByAttachmentUuid(UUID.randomUUID().toString());
        assertThat(result).isEmpty();
    }
    
    @Test
    void findAllByAccountIdIn() {
    	createEmpEntityUkEts("emp1", 1L, "fileDocumentId1");
    	createEmpEntityUkEts("emp2", 2L, "fileDocumentId2");
    	createEmpEntityUkEts("emp3", 3L, "fileDocumentId3");
    	
        flushAndClear();
        
        Set<EmpAccountDTO> result = repository.findAllByAccountIdIn(Set.of(1L, 2L));
        
        assertThat(result).hasSize(2);
        
        Iterator<EmpAccountDTO> it = result.iterator();
        
        EmpAccountDTO dto1 = it.next();
        assertThat(dto1.getAccountId()).isEqualTo(1L);
        assertThat(dto1.getEmpId()).isEqualTo("emp1");
        
        EmpAccountDTO dto2 = it.next();
        assertThat(dto2.getAccountId()).isEqualTo(2L);
        assertThat(dto2.getEmpId()).isEqualTo("emp2");
    }

    private EmissionsMonitoringPlanEntity createEmpEntityUkEts(String empId, Long accountId, String fileDocumentId) {

        final String procedureDescription = "procedure description";
        final String procedureDocumentName = "procedure document name";
        final String procedureReference = "procedure reference";
        final String responsibleDepartment = "responsible department";
        final String locationOfRecords = "location of records";
        final String itSystem = "IT system";

        EmissionsMonitoringPlanUkEtsContainer container = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .abbreviations(EmpAbbreviations.builder().exist(false).build())
                        .additionalDocuments(EmpAdditionalDocuments.builder().exist(false).build())
                        .applicationTimeframeInfo(
                                EmpApplicationTimeframeInfo.builder()
                                        .dateOfStart(LocalDate.of(2023, Month.APRIL, 26))
                                        .submittedOnTime(false)
                                        .reasonForLateSubmission("a reason")
                                        .build()
                        )
                        .flightAndAircraftProcedures(EmpFlightAndAircraftProcedures.builder()
                                .ukEtsFlightsCoveredDetails(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                                        responsibleDepartment, locationOfRecords, itSystem))
                                .aircraftUsedDetails(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                                        responsibleDepartment, locationOfRecords, itSystem))
                                .flightListCompletenessDetails(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                                        responsibleDepartment, locationOfRecords, itSystem))
                                .build())
                        .emissionSources(EmpEmissionSources.builder()
                                .aircraftTypes(Set.of(AircraftTypeDetails.builder()
                                        .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                .manufacturer("manufacturer")
                                                .model("model")
                                                .designatorType("designator type")
                                                .build())
                                        .subtype("subtype")
                                        .numberOfAircrafts(10L)
                                        .fuelTypes(List.of(FuelType.JET_GASOLINE))
                                        .isCurrentlyUsed(true)
                                        .build()))
                                .build())
                        .emissionsMonitoringApproach(SmallEmittersMonitoringApproach.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS)
                                .explanation("explanation")
                                .build())
                        .managementProcedures(EmpManagementProcedures.builder()
                            .monitoringReportingRoles(EmpMonitoringReportingRoles.builder()
                                .monitoringReportingRoles(List.of(EmpMonitoringReportingRole.builder().jobTitle("title").mainDuties("duties").build()))
                                .build())
                            .recordKeepingAndDocumentation(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                                responsibleDepartment, locationOfRecords, itSystem))
                            .build())
                        .emissionsReductionClaim(EmpEmissionsReductionClaim.builder()
                            .exist(true)
                            .safMonitoringSystemsAndProcesses(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                                responsibleDepartment, locationOfRecords, itSystem))
                            .rtfoSustainabilityCriteria(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                                responsibleDepartment, locationOfRecords, itSystem))
                            .safDuplicationPrevention(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                                responsibleDepartment, locationOfRecords, itSystem))
                            .build())
                        .operatorDetails(EmpOperatorDetails.builder()
                                .operatorName("operator name")
                                .crcoCode("crco code")
                                .flightIdentification(FlightIdentification.builder()
                                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                                        .icaoDesignators("designator")
                                        .build())
                                .airOperatingCertificate(AirOperatingCertificate.builder()
                                        .certificateExist(Boolean.FALSE)
                                        .build())
                                .operatingLicense(OperatingLicense.builder()
                                        .licenseExist(Boolean.FALSE)
                                        .build())
                                .organisationStructure(IndividualOrganisation.builder()
                                        .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                                        .fullName("full name")
                                        .organisationLocation(LocationOnShoreStateDTO.builder()
                                                .type(LocationType.ONSHORE_STATE)
                                                .line1("line1")
                                                .city("city")
                                                .state("state")
                                                .postcode("postcode")
                                                .country("GR")
                                                .build())
                                        .build())
                                .activitiesDescription(ActivitiesDescription.builder()
                                        .operatorType(OperatorType.COMMERCIAL)
                                        .flightTypes(Set.of(FlightType.SCHEDULED))
                                        .operationScopes(Set.of(OperationScope.UK_DOMESTIC))
                                        .activityDescription("activity description")
                                        .build())
                                .build())
                        .build())
                .serviceContactDetails(ServiceContactDetails.builder()
                    .name("name")
                    .roleCode("admin")
                    .email("test@test.com")
                    .build())
                .scheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .build();
        final EmissionsMonitoringPlanEntity empEntity = new EmissionsMonitoringPlanEntity(empId, accountId, container, fileDocumentId);
        validator.validate(empEntity);
        entityManager.persist(empEntity);
        return empEntity;
    }

    private EmissionsMonitoringPlanEntity createEmpEntityCorsia(String empId, Long accountId, String fileDocumentId) {

        final String procedureDescription = "procedure description";
        final String procedureDocumentName = "procedure document name";
        final String procedureReference = "procedure reference";
        final String responsibleDepartment = "responsible department";
        final String locationOfRecords = "location of records";
        final String itSystem = "IT system";

        EmissionsMonitoringPlanCorsiaContainer container = EmissionsMonitoringPlanCorsiaContainer.builder()
            .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                .abbreviations(EmpAbbreviations.builder().exist(false).build())
                .additionalDocuments(EmpAdditionalDocuments.builder().exist(false).build())
                .operatorDetails(EmpCorsiaOperatorDetails.builder()
                    .operatorName("operator name")
                    .flightIdentification(FlightIdentification.builder()
                        .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                        .icaoDesignators("designator")
                        .build())
                    .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                        .certificateExist(Boolean.FALSE)
                        .build())
                    .organisationStructure(IndividualOrganisation.builder()
                        .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                        .fullName("full name")
                        .organisationLocation(LocationOnShoreStateDTO.builder()
                            .type(LocationType.ONSHORE_STATE)
                            .line1("line1")
                            .city("city")
                            .state("state")
                            .postcode("postcode")
                            .country("GR")
                            .build())
                        .build())
                    .activitiesDescription(ActivitiesDescriptionCorsia.builder()
                        .operatorType(OperatorType.COMMERCIAL)
                        .flightTypes(Set.of(FlightType.SCHEDULED))
                        .activityDescription("activity description")
                        .build())
                    .subsidiaryCompanyExist(Boolean.TRUE)
                    .subsidiaryCompanies(List.of(SubsidiaryCompanyCorsia.builder()
                            .operatorName("operator2")
                            .flightIdentification(FlightIdentification.builder()
                                .flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION)
                                .icaoDesignators("designator")
                                .build())
                            .airOperatingCertificate(AirOperatingCertificateCorsia.builder()
                                .certificateExist(Boolean.FALSE)
                                .build())
                            .companyRegistrationNumber("123456")
                            .registeredLocation(LocationOnShoreStateDTO.builder()
                                .type(LocationType.ONSHORE_STATE)
                                .line1("line1")
                                .city("city")
                                .state("state")
                                .postcode("postcode")
                                .country("GR")
                                .build())
                            .flightTypes(Set.of(FlightType.SCHEDULED))
                            .activityDescription("activity description")
                        .build()))
                    .build())
                .emissionsMonitoringApproach(CertMonitoringApproach.builder()
                    .monitoringApproachType(EmissionsMonitoringApproachTypeCorsia.CERT_MONITORING)
                    .certEmissionsType(CertEmissionsType.GREAT_CIRCLE_DISTANCE)
                    .explanation("explanation")
                    .supportingEvidenceFiles(Set.of(UUID.randomUUID()))
                    .build())
                .managementProcedures(EmpManagementProceduresCorsia.builder()
                    .dataManagement(EmpDataManagement.builder()
                        .dataFlowDiagram(UUID.randomUUID())
                        .description("data management")
                        .build())
                    .empRevisions(EmpProcedureDescription.builder()
                        .description("emp revisions")
                        .build())
                    .riskExplanation(EmpProcedureDescription.builder()
                        .description("risk explanation")
                        .build())
                    .recordKeepingAndDocumentation(EmpProcedureDescription.builder()
                        .description("record keeping")
                        .build())
                    .monitoringReportingRoles(EmpMonitoringReportingRolesCorsia.builder()
                        .monitoringReportingRoles(List.of(
                            EmpMonitoringReportingRoleCorsia.builder()
                                .jobTitle("title")
                                .mainDuties("duties")
                                .build()))
                        .build())
                    .build())
                .emissionSources(EmpEmissionSourcesCorsia.builder()
                    .aircraftTypes(Set.of(AircraftTypeDetailsCorsia.builder()
                        .aircraftTypeInfo(AircraftTypeInfo.builder()
                            .manufacturer("manufacturer")
                            .model("model")
                            .designatorType("designator type")
                            .build())
                        .subtype("subtype")
                        .numberOfAircrafts(10L)
                        .fuelTypes(List.of(FuelTypeCorsia.NO_3_JET_FUEL))
                        .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.METHOD_A)
                        .build()))
                    .build())
                .flightAndAircraftProcedures(EmpFlightAndAircraftProceduresCorsia.builder()
                    .aircraftUsedDetails(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                        responsibleDepartment, locationOfRecords, itSystem))
                    .flightListCompletenessDetails(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                        responsibleDepartment, locationOfRecords, itSystem))
                    .internationalFlightsDetermination(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                        responsibleDepartment, locationOfRecords, itSystem))
                    .operatingStatePairs(EmpOperatingStatePairsCorsia.builder()
                        .operatingStatePairsCorsiaDetails(Set.of(EmpOperatingStatePairsCorsiaDetails.builder()
                                .stateA("State 1")
                                .stateB("State 2")
                            .build(),
                            EmpOperatingStatePairsCorsiaDetails.builder()
                                .stateA("State 3")
                                .stateB("State 4")
                                .build()))
                        .build())
                    .internationalFlightsDeterminationOffset(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                        responsibleDepartment, locationOfRecords, itSystem))
                    .internationalFlightsDeterminationNoMonitoring(createProcedureForm(procedureDescription, procedureDocumentName, procedureReference,
                        responsibleDepartment, locationOfRecords, itSystem))
                    .build())
                .dataGaps(EmpDataGapsCorsia.builder()
                    .dataGaps("dataGaps")
                    .secondaryDataSources("secondaryDataSources")
                    .secondarySourcesDataGapsExist(Boolean.TRUE)
                    .secondarySourcesDataGapsConditions("secondaryDataSourcesDescription")
                    .build())
                .build())
            .serviceContactDetails(ServiceContactDetails.builder()
                .name("name")
                .roleCode("admin")
                .email("test@test.com")
                .build())
            .scheme(EmissionTradingScheme.CORSIA)
            .build();

        container.setEmpAttachments(
            container.getEmissionsMonitoringPlan().getEmpSectionAttachmentIds()
                .stream()
                .collect(Collectors.toMap(Function.identity(), UUID::toString)));

        final EmissionsMonitoringPlanEntity empEntity = new EmissionsMonitoringPlanEntity(empId, accountId, container, fileDocumentId);

        validator.validate(empEntity);
        entityManager.persist(empEntity);
        return empEntity;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    private EmpProcedureForm createProcedureForm(String procedureDescription, String procedureDocumentName, String procedureReference,
                                                 String responsibleDepartment, String locationOfRecords, String itSystem) {
        return EmpProcedureForm.builder()
            .procedureDescription(procedureDescription)
            .procedureDocumentName(procedureDocumentName)
            .procedureReference(procedureReference)
            .responsibleDepartmentOrRole(responsibleDepartment)
            .locationOfRecords(locationOfRecords)
            .itSystemUsed(itSystem)
            .build();
    }
}
