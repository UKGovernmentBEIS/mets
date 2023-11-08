package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.hanlder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.service.AccountContactQueryService;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.EmpCorsiaOriginatedData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach.AviationAerCorsiaMonitoringApproach;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsreductionclaim.AviationAerCorsiaEmissionsReductionClaim;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentification;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentificationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperatorType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationStructure;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.ActivitiesDescriptionCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AirOperatingCertificateCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AviationCorsiaOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerMonitoringPlanVersion;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerRequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.domain.AviationAerCorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.handler.AviationAerCorsiaApplicationSubmitInitializer;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationRequestInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.service.EmpVariationCorsiaRequestQueryService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaApplicationSubmitInitializerTest {

    @InjectMocks
    private AviationAerCorsiaApplicationSubmitInitializer initializer;

    @Mock
    private AviationAccountQueryService aviationAccountQueryService;

    @Mock
    private EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;

    @Mock
    private EmpVariationCorsiaRequestQueryService empVariationCorsiaRequestQueryService;

    @Mock
    private AviationAerRequestQueryService aerRequestQueryService;

    @Mock
    private AccountContactQueryService accountContactQueryService;

    @Test
    void initializePayload_when_request_payload_is_empty_and_emp_exists() {
        Year reportingYear = Year.now().minusYears(1);
        Long accountId = 1L;
        String empId = "empId";

        Request request = Request.builder()
                .type(RequestType.AVIATION_AER_CORSIA)
                .metadata(AviationAerCorsiaRequestMetadata.builder().year(reportingYear).build())
                .payload(AviationAerCorsiaRequestPayload.builder()
                    .payloadType(RequestPayloadType.AVIATION_AER_CORSIA_REQUEST_PAYLOAD)
                    .build())
                .accountId(accountId)
                .build();

        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder()
                .name("name")
                .email("email")
                .roleCode("role code")
                .build();

        UUID orgStructureEvidenceFile = UUID.randomUUID();
        UUID airOperatingCertificateFile = UUID.randomUUID();
        Map<UUID,String> empAttachments = Map.of(
            orgStructureEvidenceFile, "orgStructureEvidenceFileName",
            airOperatingCertificateFile, "airOperatingCertificateFileName",
            UUID.randomUUID(), "anotherFileName");
        EmissionsMonitoringPlanCorsiaDTO empDTO = EmissionsMonitoringPlanCorsiaDTO.builder()
            .empContainer(EmissionsMonitoringPlanCorsiaContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                    .operatorDetails(EmpCorsiaOperatorDetails.builder()
                        .operatorName("operator name")
                        .flightIdentification(createFlightIdentification(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION, "icao designators", Set.of()))
                        .airOperatingCertificate(createAirOperatingCertificate(Boolean.TRUE, "certificate number", "issuing authority", Set.of(airOperatingCertificateFile)))
                        .organisationStructure(createLimitedCompanyOrganisationStructure(orgStructureEvidenceFile))
                        .activitiesDescription(createActivitiesDescription())
                        .build())
                    .build())
                .empAttachments(empAttachments)
                .build())
            .build();
        AviationAerCorsiaApplicationSubmitRequestTaskPayload expectedRequestTaskPayload = AviationAerCorsiaApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_SUBMIT_PAYLOAD)
                .reportingYear(reportingYear)
                .aerMonitoringPlanVersions(List.of(
                        createAerMonitoringPlanVersion(empId, LocalDate.of(reportingYear.minusYears(1).getValue(), 7, 15), 7),
                        createAerMonitoringPlanVersion(empId, LocalDate.of(reportingYear.getValue(), 2, 15), 8),
                        createAerMonitoringPlanVersion(empId, LocalDate.of(reportingYear.getValue(), 4, 17), 9),
                        createAerMonitoringPlanVersion(empId, LocalDate.of(reportingYear.getValue(), 5, 17), 10)
                        ))
                .empOriginatedData(EmpCorsiaOriginatedData.builder()
                    .operatorDetails(AviationCorsiaOperatorDetails.builder()
                        .operatorName("account name")
                        .flightIdentification(createFlightIdentification(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION, "icao designators", Set.of()))
                        .airOperatingCertificate(createAirOperatingCertificate(Boolean.TRUE, "certificate number", "issuing authority", Set.of(airOperatingCertificateFile)))
                        .organisationStructure(createLimitedCompanyOrganisationStructure(orgStructureEvidenceFile))
                        .build())
                    .build())
                .serviceContactDetails(serviceContactDetails)
                .aerAttachments(Map.of(orgStructureEvidenceFile, "orgStructureEvidenceFileName", airOperatingCertificateFile, "airOperatingCertificateFileName"))
                .build();

        when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(AviationAccountInfoDTO.builder()
                .name("account name")
                .crcoCode("account crco code")
                .emissionTradingScheme(EmissionTradingScheme.CORSIA)
                .accountType(AccountType.AVIATION)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build());

        when(emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId)).thenReturn(Optional.of(empDTO));
        when(emissionsMonitoringPlanQueryService.getEmpIdByAccountId(accountId)).thenReturn(Optional.of(empId));
        when(empVariationCorsiaRequestQueryService.findApprovedVariationRequests(accountId)).thenReturn(
                List.of(createEmpVariationRequestInfo("requestId1", LocalDateTime.of(reportingYear.getValue(), 5, 17, 11, 15), 10),
                        createEmpVariationRequestInfo("requestId2", LocalDateTime.of(reportingYear.getValue(), 4, 17, 11, 15), 9),
                        createEmpVariationRequestInfo("requestId3", LocalDateTime.of(reportingYear.getValue(), 2, 15, 12, 20), 8),
                        createEmpVariationRequestInfo("requestId4", LocalDateTime.of(reportingYear.minusYears(1).getValue(), 7, 15, 12, 20), 7),
                        createEmpVariationRequestInfo("requestId5", LocalDateTime.of(reportingYear.minusYears(1).getValue(), 4, 16, 8, 25), 6)
                ));
        when(aerRequestQueryService.findEndDateOfApprovedEmpIssuanceByAccountId(accountId)).thenReturn(Optional.of(LocalDateTime.of(reportingYear.minusYears(2).getValue(), 2, 15, 12, 20)));
        when(accountContactQueryService.getServiceContactDetails(accountId)).thenReturn(Optional.of(serviceContactDetails));
        AviationAerCorsiaApplicationSubmitRequestTaskPayload requestTaskPayload =
                (AviationAerCorsiaApplicationSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertEquals(expectedRequestTaskPayload, requestTaskPayload);
    }

    @Test
    void initializePayload_when_request_payload_is_not_empty_and_emp_exists() {
        Year reportingYear = Year.now().minusYears(1);
        Long accountId = 1L;
        String empId = "empId";

        AviationAerCorsia aer = AviationAerCorsia.builder()
            .monitoringApproach(AviationAerCorsiaMonitoringApproach.builder().certUsed(Boolean.FALSE).build())
            .emissionsReductionClaim(AviationAerCorsiaEmissionsReductionClaim.builder().exist(Boolean.FALSE).build())
            .build();
        Map<String, List<Boolean>> aerSectionsCompleted = Map.of(
            "monitoringApproach", List.of(true),
            "emissionsReductionClaim", List.of(true));
        Map<UUID, String> aerAttachments = Map.of(UUID.randomUUID(), "attachment1");
        AviationCorsiaOperatorDetails empOriginatedOperatorDetails = AviationCorsiaOperatorDetails.builder()
            .operatorName("name")
            .flightIdentification(createFlightIdentification(FlightIdentificationType.AIRCRAFT_REGISTRATION_MARKINGS, "icao designators", Set.of()))
            .airOperatingCertificate(createAirOperatingCertificate(Boolean.FALSE, "certificate number", "issuing authority", Set.of(UUID.randomUUID())))
            .organisationStructure(createLimitedCompanyOrganisationStructure(UUID.randomUUID()))
            .build();
        AviationAerCorsiaRequestPayload requestPayload = AviationAerCorsiaRequestPayload.builder()
            .reportingRequired(Boolean.TRUE)
            .aer(aer)
            .aerSectionsCompleted(aerSectionsCompleted)
            .aerAttachments(aerAttachments)
            .empOriginatedData(EmpCorsiaOriginatedData.builder().operatorDetails(empOriginatedOperatorDetails).build())
            .build();
        Request request = Request.builder()
            .type(RequestType.AVIATION_AER_CORSIA)
            .metadata(AviationAerCorsiaRequestMetadata.builder().year(reportingYear).build())
            .payload(requestPayload)
            .accountId(accountId)
            .build();

        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder()
            .name("name")
            .email("email")
            .roleCode("role code")
            .build();

        UUID orgStructureEvidenceFile = UUID.randomUUID();
        UUID airOperatingCertificateFile = UUID.randomUUID();
        Map<UUID,String> empAttachments = Map.of(
            orgStructureEvidenceFile, "orgStructureEvidenceFileName",
            airOperatingCertificateFile, "airOperatingCertificateFileName",
            UUID.randomUUID(), "anotherFileName");
        EmissionsMonitoringPlanCorsiaDTO empDTO = EmissionsMonitoringPlanCorsiaDTO.builder()
            .empContainer(EmissionsMonitoringPlanCorsiaContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                    .operatorDetails(EmpCorsiaOperatorDetails.builder()
                        .operatorName("operator name")
                        .flightIdentification(createFlightIdentification(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION, "icao designators", Set.of()))
                        .airOperatingCertificate(createAirOperatingCertificate(Boolean.TRUE, "certificate number", "issuing authority", Set.of(airOperatingCertificateFile)))
                        .organisationStructure(createLimitedCompanyOrganisationStructure(orgStructureEvidenceFile))
                        .activitiesDescription(createActivitiesDescription())
                        .build())
                    .build())
                .empAttachments(empAttachments)
                .build())
            .build();
        AviationAerCorsiaApplicationSubmitRequestTaskPayload expectedRequestTaskPayload = AviationAerCorsiaApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_SUBMIT_PAYLOAD)
            .reportingYear(reportingYear)
            .reportingRequired(Boolean.TRUE)
            .aer(aer)
            .aerAttachments(aerAttachments)
            .aerSectionsCompleted(aerSectionsCompleted)
            .aerMonitoringPlanVersions(List.of(
                createAerMonitoringPlanVersion(empId, LocalDate.of(reportingYear.minusYears(1).getValue(), 7, 15), 7),
                createAerMonitoringPlanVersion(empId, LocalDate.of(reportingYear.getValue(), 2, 15), 8),
                createAerMonitoringPlanVersion(empId, LocalDate.of(reportingYear.getValue(), 4, 17), 9),
                createAerMonitoringPlanVersion(empId, LocalDate.of(reportingYear.getValue(), 5, 17), 10)
            ))
            .serviceContactDetails(serviceContactDetails)
            .empOriginatedData(EmpCorsiaOriginatedData.builder().operatorDetails(empOriginatedOperatorDetails).build())
            .build();

        when(emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId)).thenReturn(Optional.of(empDTO));
        when(emissionsMonitoringPlanQueryService.getEmpIdByAccountId(accountId)).thenReturn(Optional.of(empId));
        when(empVariationCorsiaRequestQueryService.findApprovedVariationRequests(accountId)).thenReturn(
            List.of(createEmpVariationRequestInfo("requestId1", LocalDateTime.of(reportingYear.getValue(), 5, 17, 11, 15), 10),
                createEmpVariationRequestInfo("requestId2", LocalDateTime.of(reportingYear.getValue(), 4, 17, 11, 15), 9),
                createEmpVariationRequestInfo("requestId3", LocalDateTime.of(reportingYear.getValue(), 2, 15, 12, 20), 8),
                createEmpVariationRequestInfo("requestId4", LocalDateTime.of(reportingYear.minusYears(1).getValue(), 7, 15, 12, 20), 7),
                createEmpVariationRequestInfo("requestId5", LocalDateTime.of(reportingYear.minusYears(1).getValue(), 4, 16, 8, 25), 6)
            ));
        when(aerRequestQueryService.findEndDateOfApprovedEmpIssuanceByAccountId(accountId)).thenReturn(Optional.of(LocalDateTime.of(reportingYear.minusYears(2).getValue(), 2, 15, 12, 20)));
        when(accountContactQueryService.getServiceContactDetails(accountId)).thenReturn(Optional.of(serviceContactDetails));
        AviationAerCorsiaApplicationSubmitRequestTaskPayload requestTaskPayload =
            (AviationAerCorsiaApplicationSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertEquals(expectedRequestTaskPayload, requestTaskPayload);

        verifyNoInteractions(aviationAccountQueryService);
    }

    @Test
    void initializePayload_no_emp_found() {
        Year reportingYear = Year.now().minusYears(1);
        Long accountId = 1L;

        Request request = Request.builder()
                .metadata(AviationAerCorsiaRequestMetadata.builder().year(reportingYear).build())
                .payload(AviationAerCorsiaRequestPayload.builder().build())
                .accountId(accountId)
                .build();

        AviationAerCorsiaApplicationSubmitRequestTaskPayload expectedRequestTaskPayload = AviationAerCorsiaApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_SUBMIT_PAYLOAD)
                .reportingYear(reportingYear)
                .empOriginatedData(EmpCorsiaOriginatedData.builder()
                    .operatorDetails(AviationCorsiaOperatorDetails.builder()
                        .operatorName("name")
                        .build())
                    .build())
                .build();

        when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(AviationAccountInfoDTO.builder()
                .name("name")
                .crcoCode("crco code")
                .emissionTradingScheme(EmissionTradingScheme.CORSIA)
                .accountType(AccountType.AVIATION)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build());

        when(emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId)).thenReturn(Optional.empty());
        when(emissionsMonitoringPlanQueryService.getEmpIdByAccountId(accountId)).thenReturn(Optional.empty());
        when(accountContactQueryService.getServiceContactDetails(accountId)).thenReturn(Optional.empty());

        RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        assertEquals(expectedRequestTaskPayload, requestTaskPayload);
        verifyNoInteractions(empVariationCorsiaRequestQueryService);
        verifyNoInteractions(aerRequestQueryService);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsOnly(RequestTaskType.AVIATION_AER_CORSIA_APPLICATION_SUBMIT);
    }

    private AviationAerMonitoringPlanVersion createAerMonitoringPlanVersion(String empId, LocalDate approvalDate, Integer consolidationNumber) {
        return AviationAerMonitoringPlanVersion.builder()
            .empId(empId)
            .empApprovalDate(approvalDate)
            .empConsolidationNumber(consolidationNumber)
            .build();
    }

    private FlightIdentification createFlightIdentification(FlightIdentificationType flightIdentificationType, String icaoDesignators,
                                                            Set<String> registrationMarkings) {
        return FlightIdentification.builder()
                .flightIdentificationType(flightIdentificationType)
                .icaoDesignators(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION.equals(flightIdentificationType) ? icaoDesignators : null)
                .aircraftRegistrationMarkings(FlightIdentificationType.AIRCRAFT_REGISTRATION_MARKINGS.equals(flightIdentificationType) ? registrationMarkings : null)
                .build();
    }

    private AirOperatingCertificateCorsia createAirOperatingCertificate(Boolean exists, String certificateNumber, String issuingAuthority, Set<UUID> files) {
        return AirOperatingCertificateCorsia.builder()
                .certificateExist(exists)
                .certificateNumber(exists ? certificateNumber : null)
                .issuingAuthority(exists ? issuingAuthority : null)
                .certificateFiles(exists ? files : null)
                .build();
    }

    private ActivitiesDescriptionCorsia createActivitiesDescription() {
        return ActivitiesDescriptionCorsia.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .activityDescription("activity description")
                .build();
    }

    private OrganisationStructure createLimitedCompanyOrganisationStructure(UUID evidenceFile) {
            return LimitedCompanyOrganisation.builder()
                    .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                    .registrationNumber("registration name")
                    .organisationLocation(createOrganisationLocation())
                    .differentContactLocationExist(Boolean.FALSE)
                    .evidenceFiles(Set.of(evidenceFile))
                    .build();
    }

    private LocationOnShoreStateDTO createOrganisationLocation() {
        return LocationOnShoreStateDTO.builder()
                .type(LocationType.ONSHORE_STATE)
                .line1("line 1")
                .country("GR")
                .city("city")
                .state("state")
                .postcode("postcode")
                .build();
    }

    private EmpVariationRequestInfo createEmpVariationRequestInfo(String requestId, LocalDateTime endDate, Integer consolidationNumber) {
        return EmpVariationRequestInfo.builder()
                .id(requestId)
                .endDate(endDate)
                .metadata(EmpVariationRequestMetadata.builder().type(RequestMetadataType.EMP_VARIATION).empConsolidationNumber(consolidationNumber).build())
                .build();
    }
}