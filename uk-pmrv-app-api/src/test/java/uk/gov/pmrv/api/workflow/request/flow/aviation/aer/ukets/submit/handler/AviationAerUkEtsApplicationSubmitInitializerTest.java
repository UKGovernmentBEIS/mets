package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.handler;

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
import uk.gov.pmrv.api.aviationreporting.ukets.EmpUkEtsOriginatedData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.emissionsmonitoringapproach.AviationAerFuelMonitoringApproach;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.saf.AviationAerSaf;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentification;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentificationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperationScope;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OperatorType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationStructure;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.ActivitiesDescription;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AirOperatingCertificate;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AviationOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.OperatingLicense;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerMonitoringPlanVersion;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerRequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.domain.AviationAerUkEtsApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationRequestInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.service.EmpVariationUkEtsRequestQueryService;

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
class AviationAerUkEtsApplicationSubmitInitializerTest {

    @InjectMocks
    private AviationAerUkEtsApplicationSubmitInitializer initializer;

    @Mock
    private AviationAccountQueryService aviationAccountQueryService;

    @Mock
    private EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;

    @Mock
    private EmpVariationUkEtsRequestQueryService empVariationRequestQueryService;

    @Mock
    private AviationAerRequestQueryService aerRequestQueryService;

    @Mock
    private AccountContactQueryService accountContactQueryService;

    @Test
    void initializeTaskPayload_when_request_payload_is_empty_and_emp_exists() {
        Year reportingYear = Year.now().minusYears(1);
        Long accountId = 1L;
        String empId = "empId";

        AviationAerUkEtsRequestPayload requestPayload = AviationAerUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.AVIATION_AER_UKETS_REQUEST_PAYLOAD)
            .build();
        Request request = Request.builder()
            .type(RequestType.AVIATION_AER_UKETS)
            .metadata(AviationAerRequestMetadata.builder().year(reportingYear).build())
            .payload(requestPayload)
            .accountId(accountId)
            .build();

        final ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder()
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
        EmissionsMonitoringPlanUkEtsDTO empDTO = EmissionsMonitoringPlanUkEtsDTO.builder()
            .empContainer(EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                    .operatorDetails(EmpOperatorDetails.builder()
                        .operatorName("operator name")
                        .crcoCode("code")
                        .flightIdentification(createFlightIdentification(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION, "icao designators", Set.of()))
                        .airOperatingCertificate(createAirOperatingCertificate(Boolean.TRUE, "certificate number", "issuing authority", Set.of(airOperatingCertificateFile)))
                        .operatingLicense(createOperatingLicense(Boolean.TRUE, "license number", "issuing authority"))
                        .organisationStructure(createLimitedCompanyOrganisationStructure(orgStructureEvidenceFile))
                        .activitiesDescription(createActivitiesDescription())
                        .build())
                    .build())
                .empAttachments(empAttachments)
                .build())
            .build();
        AviationAerUkEtsApplicationSubmitRequestTaskPayload expectedRequestTaskPayload = AviationAerUkEtsApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_SUBMIT_PAYLOAD)
            .reportingYear(reportingYear)
            .aerMonitoringPlanVersions(List.of(
                createAerMonitoringPlanVersion(empId, LocalDate.of(reportingYear.minusYears(1).getValue(), 7, 15), 7),
                createAerMonitoringPlanVersion(empId, LocalDate.of(reportingYear.getValue(), 2, 15), 8),
                createAerMonitoringPlanVersion(empId, LocalDate.of(reportingYear.getValue(), 4, 17), 9),
                createAerMonitoringPlanVersion(empId, LocalDate.of(reportingYear.getValue(), 5, 17), 10)
            ))
            .empOriginatedData(EmpUkEtsOriginatedData.builder()
                .operatorDetails(AviationOperatorDetails.builder()
                    .operatorName("account name")
                    .crcoCode("account crco code")
                    .flightIdentification(createFlightIdentification(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION, "icao designators", Set.of()))
                    .airOperatingCertificate(createAirOperatingCertificate(Boolean.TRUE, "certificate number", "issuing authority", Set.of(airOperatingCertificateFile)))
                    .operatingLicense(createOperatingLicense(Boolean.TRUE, "license number", "issuing authority"))
                    .organisationStructure(createLimitedCompanyOrganisationStructure(orgStructureEvidenceFile))
                    .build())
                .build())
            .serviceContactDetails(serviceContactDetails)
            .aerAttachments(Map.of(orgStructureEvidenceFile, "orgStructureEvidenceFileName", airOperatingCertificateFile, "airOperatingCertificateFileName"))
            .build();

        when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(AviationAccountInfoDTO.builder()
            .name("account name")
            .crcoCode("account crco code")
            .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
            .accountType(AccountType.AVIATION)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .build());

        when(emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId)).thenReturn(Optional.of(empDTO));
        when(emissionsMonitoringPlanQueryService.getEmpIdByAccountId(accountId)).thenReturn(Optional.of(empId));
        when(empVariationRequestQueryService.findApprovedVariationRequests(accountId)).thenReturn(
            List.of(createEmpVariationRequestInfo("requestId1", LocalDateTime.of(reportingYear.getValue(), 5, 17, 11, 15), 10),
                createEmpVariationRequestInfo("requestId2", LocalDateTime.of(reportingYear.getValue(), 4, 17, 11, 15), 9),
                createEmpVariationRequestInfo("requestId3", LocalDateTime.of(reportingYear.getValue(), 2, 15, 12, 20), 8),
                createEmpVariationRequestInfo("requestId4", LocalDateTime.of(reportingYear.minusYears(1).getValue(), 7, 15, 12, 20), 7),
                createEmpVariationRequestInfo("requestId5", LocalDateTime.of(reportingYear.minusYears(1).getValue(), 4, 16, 8, 25), 6)
            ));
        when(aerRequestQueryService.findEndDateOfApprovedEmpIssuanceByAccountId(accountId)).thenReturn(Optional.of(LocalDateTime.of(reportingYear.minusYears(2).getValue(), 2, 15, 12, 20)));
        when(accountContactQueryService.getServiceContactDetails(accountId)).thenReturn(Optional.of(serviceContactDetails));

        AviationAerUkEtsApplicationSubmitRequestTaskPayload requestTaskPayload =
            (AviationAerUkEtsApplicationSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertEquals(expectedRequestTaskPayload, requestTaskPayload);
    }

    @Test
    void initializePayload_when_request_payload_is_not_empty_and_emp_exists() {
        Year reportingYear = Year.now().minusYears(1);
        Long accountId = 1L;
        String empId = "empId";

        AviationAerUkEts aer = AviationAerUkEts.builder()
            .monitoringApproach(AviationAerFuelMonitoringApproach.builder().build())
            .saf(AviationAerSaf.builder().exist(Boolean.FALSE).build())
            .build();
        Map<String, List<Boolean>> aerSectionsCompleted = Map.of(
            "monitoringApproach", List.of(true),
            "saf", List.of(true));
        Map<UUID, String> aerAttachments = Map.of(UUID.randomUUID(), "attachment1");
        AviationOperatorDetails empOriginatedOperatorDetails = AviationOperatorDetails.builder()
            .operatorName("name")
            .crcoCode("crco code")
            .flightIdentification(createFlightIdentification(FlightIdentificationType.AIRCRAFT_REGISTRATION_MARKINGS, "icao designators", Set.of()))
            .airOperatingCertificate(createAirOperatingCertificate(Boolean.FALSE, "certificate number", "issuing authority", Set.of(UUID.randomUUID())))
            .operatingLicense(createOperatingLicense(Boolean.FALSE, "license number", "issuing authority"))
            .organisationStructure(createLimitedCompanyOrganisationStructure(UUID.randomUUID()))
            .build();
        AviationAerUkEtsRequestPayload requestPayload = AviationAerUkEtsRequestPayload.builder()
            .reportingRequired(Boolean.TRUE)
            .aer(aer)
            .aerSectionsCompleted(aerSectionsCompleted)
            .aerAttachments(aerAttachments)
            .empOriginatedData(EmpUkEtsOriginatedData.builder().operatorDetails(empOriginatedOperatorDetails).build())
            .build();
        Request request = Request.builder()
                .type(RequestType.AVIATION_AER_UKETS)
                .metadata(AviationAerRequestMetadata.builder().year(reportingYear).build())
                .payload(requestPayload)
                .accountId(accountId)
                .build();

        final ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder()
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
        EmissionsMonitoringPlanUkEtsDTO empDTO = EmissionsMonitoringPlanUkEtsDTO.builder()
            .empContainer(EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                    .operatorDetails(EmpOperatorDetails.builder()
                        .operatorName("operator name")
                        .crcoCode("code")
                        .flightIdentification(createFlightIdentification(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION, "icao designators", Set.of()))
                        .airOperatingCertificate(createAirOperatingCertificate(Boolean.TRUE, "certificate number", "issuing authority", Set.of(airOperatingCertificateFile)))
                        .operatingLicense(createOperatingLicense(Boolean.TRUE, "license number", "issuing authority"))
                        .organisationStructure(createLimitedCompanyOrganisationStructure(orgStructureEvidenceFile))
                        .activitiesDescription(createActivitiesDescription())
                        .build())
                    .build())
                .empAttachments(empAttachments)
                .build())
            .build();
        AviationAerUkEtsApplicationSubmitRequestTaskPayload expectedRequestTaskPayload = AviationAerUkEtsApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_SUBMIT_PAYLOAD)
                .reportingYear(reportingYear)
                .reportingRequired(Boolean.TRUE)
                .aer(aer)
                .aerSectionsCompleted(aerSectionsCompleted)
                .aerAttachments(aerAttachments)
                .aerMonitoringPlanVersions(List.of(
                        createAerMonitoringPlanVersion(empId, LocalDate.of(reportingYear.minusYears(1).getValue(), 7, 15), 7),
                        createAerMonitoringPlanVersion(empId, LocalDate.of(reportingYear.getValue(), 2, 15), 8),
                        createAerMonitoringPlanVersion(empId, LocalDate.of(reportingYear.getValue(), 4, 17), 9),
                        createAerMonitoringPlanVersion(empId, LocalDate.of(reportingYear.getValue(), 5, 17), 10)
                        ))
                .empOriginatedData(EmpUkEtsOriginatedData.builder()
                    .operatorDetails(AviationOperatorDetails.builder()
                        .operatorName("name")
                        .crcoCode("account crco code")
                        .flightIdentification(empOriginatedOperatorDetails.getFlightIdentification())
                        .airOperatingCertificate(empOriginatedOperatorDetails.getAirOperatingCertificate())
                        .operatingLicense(empOriginatedOperatorDetails.getOperatingLicense())
                        .organisationStructure(empOriginatedOperatorDetails.getOrganisationStructure())
                        .build())
                    .build())
                .serviceContactDetails(serviceContactDetails)
                .build();

        when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(AviationAccountInfoDTO.builder()
                .name("account name")
                .crcoCode("account crco code")
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .accountType(AccountType.AVIATION)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build());

        when(emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId)).thenReturn(Optional.of(empDTO));
        when(emissionsMonitoringPlanQueryService.getEmpIdByAccountId(accountId)).thenReturn(Optional.of(empId));
        when(empVariationRequestQueryService.findApprovedVariationRequests(accountId)).thenReturn(
                List.of(createEmpVariationRequestInfo("requestId1", LocalDateTime.of(reportingYear.getValue(), 5, 17, 11, 15), 10),
                        createEmpVariationRequestInfo("requestId2", LocalDateTime.of(reportingYear.getValue(), 4, 17, 11, 15), 9),
                        createEmpVariationRequestInfo("requestId3", LocalDateTime.of(reportingYear.getValue(), 2, 15, 12, 20), 8),
                        createEmpVariationRequestInfo("requestId4", LocalDateTime.of(reportingYear.minusYears(1).getValue(), 7, 15, 12, 20), 7),
                        createEmpVariationRequestInfo("requestId5", LocalDateTime.of(reportingYear.minusYears(1).getValue(), 4, 16, 8, 25), 6)
                ));
        when(aerRequestQueryService.findEndDateOfApprovedEmpIssuanceByAccountId(accountId)).thenReturn(Optional.of(LocalDateTime.of(reportingYear.minusYears(2).getValue(), 2, 15, 12, 20)));
        when(accountContactQueryService.getServiceContactDetails(accountId)).thenReturn(Optional.of(serviceContactDetails));
        AviationAerUkEtsApplicationSubmitRequestTaskPayload requestTaskPayload =
                (AviationAerUkEtsApplicationSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertEquals(expectedRequestTaskPayload, requestTaskPayload);
    }

    @Test
    void initializePayload_when_request_payload_is_empty_and_emp_not_exists() {
        Year reportingYear = Year.now().minusYears(1);
        Long accountId = 1L;

        Request request = Request.builder()
                .metadata(AviationAerRequestMetadata.builder().year(reportingYear).build())
                .payload(AviationAerUkEtsRequestPayload.builder().build())
                .accountId(accountId)
                .build();

        AviationAerUkEtsApplicationSubmitRequestTaskPayload expectedRequestTaskPayload = AviationAerUkEtsApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_SUBMIT_PAYLOAD)
                .reportingYear(reportingYear)
                .empOriginatedData(EmpUkEtsOriginatedData.builder()
                    .operatorDetails(AviationOperatorDetails.builder()
                        .operatorName("name")
                        .crcoCode("crco code")
                        .build())
                    .build())
                .build();

        when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(AviationAccountInfoDTO.builder()
                .name("name")
                .crcoCode("crco code")
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .accountType(AccountType.AVIATION)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build());

        when(emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId)).thenReturn(Optional.empty());
        when(emissionsMonitoringPlanQueryService.getEmpIdByAccountId(accountId)).thenReturn(Optional.empty());
        when(accountContactQueryService.getServiceContactDetails(accountId)).thenReturn(Optional.empty());

        RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        assertEquals(expectedRequestTaskPayload, requestTaskPayload);
        verifyNoInteractions(empVariationRequestQueryService);
        verifyNoInteractions(aerRequestQueryService);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsOnly(RequestTaskType.AVIATION_AER_UKETS_APPLICATION_SUBMIT);
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

    private AirOperatingCertificate createAirOperatingCertificate(Boolean exists, String certificateNumber, String issuingAuthority, Set<UUID> files) {
        return AirOperatingCertificate.builder()
                .certificateExist(exists)
                .certificateNumber(exists ? certificateNumber : null)
                .issuingAuthority(exists ? issuingAuthority : null)
                .certificateFiles(exists ? files : null)
                .build();
    }

    private OperatingLicense createOperatingLicense(Boolean exist, String licenseNumber, String issuingAuthority) {
        return OperatingLicense.builder()
                .licenseExist(exist)
                .licenseNumber(exist ? licenseNumber : null)
                .issuingAuthority(exist ? issuingAuthority : null)
                .build();
    }

    private ActivitiesDescription createActivitiesDescription() {
        return ActivitiesDescription.builder()
                .operatorType(OperatorType.COMMERCIAL)
                .flightTypes(Set.of(FlightType.SCHEDULED))
                .operationScopes(Set.of(OperationScope.UK_DOMESTIC))
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