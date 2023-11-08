package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationActivityDataTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationNetCalorificValueTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationParameterType;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerSubmitParams;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.MonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.MonitoringApproachMonitoringTiers;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationActivityDataMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationNetCalorificValueMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;
import uk.gov.pmrv.api.reporting.domain.verification.OverallAssessmentType;
import uk.gov.pmrv.api.reporting.domain.verification.VerifiedSatisfactoryOverallAssessment;
import uk.gov.pmrv.api.reporting.service.AerService;
import uk.gov.pmrv.api.reporting.validation.AerValidatorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationAmendsSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationRequestVerificationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationVerificationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.MonitoringPlanVersion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestAerSubmitServiceTest {

    @InjectMocks
    private RequestAerSubmitService service;

    @Mock
    private AerValidatorService aerValidatorService;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Mock
    private RequestService requestService;

    @Mock
    private AerService aerService;

    @Test
    void sendToRegulator() {
        final long accountId = 1L;
        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        final InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();
        final List<MonitoringPlanVersion> monitoringPlanVersions = getMonitoringPlanVersions();
        final PermitOriginatedData permitOriginatedData = getPermitOriginatedData();
        AerVerificationReport aerVerificationReport = AerVerificationReport.builder()
                .verificationData(AerVerificationData.builder()
                        .overallAssessment(VerifiedSatisfactoryOverallAssessment.builder().type(OverallAssessmentType.VERIFIED_AS_SATISFACTORY).build())
                        .build())
                .build();
        Request request = Request.builder()
                .accountId(accountId)
                .payload(AerRequestPayload.builder()
                        .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                        .verificationReport(aerVerificationReport)
                        .build())
                .metadata(AerRequestMetadata.builder()
                        .type(RequestMetadataType.AER)
                        .build())
                .build();

        final AerContainer container = AerContainer.builder()
                .installationOperatorDetails(installationOperatorDetails)
                .aer(Aer.builder().abbreviations(Abbreviations.builder().exist(false).build()).build())
                .verificationReport(aerVerificationReport)
                .permitOriginatedData(permitOriginatedData)
                .build();

        AerApplicationSubmitRequestTaskPayload taskPayload = AerApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AER_APPLICATION_SUBMIT_PAYLOAD)
                .installationOperatorDetails(installationOperatorDetails)
                .aer(container.getAer())
                .permitType(PermitType.GHGE)
                .permitOriginatedData(permitOriginatedData)
                .monitoringPlanVersions(monitoringPlanVersions)
                .verificationPerformed(true)
                .build();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        Request savedRequest = Request.builder()
                .accountId(1L)
                .payload(AerRequestPayload.builder()
                        .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                        .aer(container.getAer())
                        .verificationPerformed(true)
                        .verificationReport(aerVerificationReport)
                        .monitoringPlanVersions(monitoringPlanVersions)
                        .permitOriginatedData(permitOriginatedData)
                        .build())
                .metadata(AerRequestMetadata.builder()
                        .type(RequestMetadataType.AER)
                        .emissions(BigDecimal.valueOf(12345.1234))
                        .overallAssessmentType(OverallAssessmentType.VERIFIED_AS_SATISFACTORY)
                        .build())
                .build();

        AerApplicationSubmittedRequestActionPayload actionPayload =
                AerApplicationSubmittedRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.AER_APPLICATION_SUBMITTED_PAYLOAD)
                        .aer(container.getAer())
                        .installationOperatorDetails(installationOperatorDetails)
                        .monitoringPlanVersions(monitoringPlanVersions)
                        .permitOriginatedData(permitOriginatedData)
                        .build();

        AerSubmitParams params = AerSubmitParams.builder()
                .accountId(request.getAccountId())
                .aerContainer(container)
                .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId()))
                .thenReturn(installationOperatorDetails);

        when(aerService.updateReportableEmissions(params))
                .thenReturn(BigDecimal.valueOf(12345.1234));


        // Invoke
        service.sendToRegulator(requestTask, pmrvUser);

        // Verify
        verify(aerValidatorService, times(1))
                .validate(container, accountId);
        verify(installationOperatorDetailsQueryService, times(1))
                .getInstallationOperatorDetails(request.getAccountId());
        verify(requestService, times(1)).addActionToRequest(requestTask.getRequest(),
                actionPayload, RequestActionType.AER_APPLICATION_SUBMITTED, pmrvUser.getUserId());
        verify(aerService, times(1))
                .updateReportableEmissions(params);
        assertThat(request.getPayload()).isEqualTo(savedRequest.getPayload());
        assertEquals(BigDecimal.valueOf(12345.1234), ((AerRequestMetadata) request.getMetadata()).getEmissions());
        assertEquals(OverallAssessmentType.VERIFIED_AS_SATISFACTORY,
                ((AerRequestMetadata) request.getMetadata()).getOverallAssessmentType());


    }

    @Test
    void sendAmendsToRegulator() {
        final long accountId = 1L;
        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        final InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();
        final List<MonitoringPlanVersion> monitoringPlanVersions = getMonitoringPlanVersions();
        final PermitOriginatedData permitOriginatedData = getPermitOriginatedData();
        Request request = Request.builder()
                .accountId(accountId)
                .payload(AerRequestPayload.builder()
                        .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                        .aer(Aer.builder()
                                .monitoringApproachEmissions(MonitoringApproachEmissions.builder()
                                        .monitoringApproachEmissions(Map.of(MonitoringApproachType.MEASUREMENT_CO2, MeasurementOfCO2Emissions.builder().build()))
                                        .build())
                                .build())
                        .build())
                .metadata(AerRequestMetadata.builder()
                        .type(RequestMetadataType.AER)
                        .build())
                .build();

        final AerContainer container = AerContainer.builder()
                .installationOperatorDetails(installationOperatorDetails)
                .aer(Aer.builder()
                        .abbreviations(Abbreviations.builder().exist(false).build())
                        .monitoringApproachEmissions(MonitoringApproachEmissions.builder()
                                .monitoringApproachEmissions(Map.of(MonitoringApproachType.CALCULATION_CO2, CalculationOfCO2Emissions.builder().build()))
                                .build())
                        .build())
                .permitOriginatedData(permitOriginatedData)
                .build();

        AerApplicationAmendsSubmitRequestTaskPayload taskPayload =
                AerApplicationAmendsSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.AER_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
                        .installationOperatorDetails(installationOperatorDetails)
                        .aer(container.getAer())
                        .permitType(PermitType.GHGE)
                        .permitOriginatedData(permitOriginatedData)
                        .reviewSectionsCompleted(Map.of("section", true))
                        .monitoringPlanVersions(monitoringPlanVersions)
                        .verificationPerformed(false)
                        .build();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        Request savedRequest = Request.builder()
                .accountId(1L)
                .payload(AerRequestPayload.builder()
                        .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                        .aer(container.getAer())
                        .verificationPerformed(false)
                        .monitoringPlanVersions(monitoringPlanVersions)
                        .permitOriginatedData(permitOriginatedData)
                        .aerSectionsCompleted(Map.of("section", List.of(true)))
                        .reviewSectionsCompleted(Map.of("section", true))
                        .build())
                .metadata(AerRequestMetadata.builder()
                        .type(RequestMetadataType.AER)
                        .emissions(BigDecimal.valueOf(12345.1234))
                        .overallAssessmentType(OverallAssessmentType.VERIFIED_AS_SATISFACTORY)
                        .build())
                .build();

        AerApplicationAmendsSubmittedRequestActionPayload actionPayload =
                AerApplicationAmendsSubmittedRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.AER_APPLICATION_AMENDS_SUBMITTED_PAYLOAD)
                        .aer(container.getAer())
                        .installationOperatorDetails(installationOperatorDetails)
                        .monitoringPlanVersions(monitoringPlanVersions)
                        .permitOriginatedData(permitOriginatedData)
                        .build();

        AerSubmitParams params = AerSubmitParams.builder()
                .accountId(request.getAccountId())
                .aerContainer(container)
                .build();

        AerSubmitApplicationAmendRequestTaskActionPayload requestTaskActionPayload =
                AerSubmitApplicationAmendRequestTaskActionPayload.builder()
                        .aerSectionsCompleted(Map.of("section", List.of(true)))
                        .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId()))
                .thenReturn(installationOperatorDetails);

        when(aerService.updateReportableEmissions(params))
                .thenReturn(BigDecimal.valueOf(12345.1234));


        // Invoke
        service.sendAmendsToRegulator(requestTask, requestTaskActionPayload, pmrvUser);

        // Verify
        verify(aerValidatorService, times(1))
                .validate(container, accountId);
        verify(installationOperatorDetailsQueryService, times(1))
                .getInstallationOperatorDetails(request.getAccountId());
        verify(requestService, times(1)).addActionToRequest(requestTask.getRequest(),
                actionPayload, RequestActionType.AER_APPLICATION_AMENDS_SUBMITTED, pmrvUser.getUserId());
        verify(aerService, times(1))
                .updateReportableEmissions(params);
        assertThat(request.getPayload()).isEqualTo(savedRequest.getPayload());
        assertEquals(BigDecimal.valueOf(12345.1234), ((AerRequestMetadata) request.getMetadata()).getEmissions());
        assertNull(((AerRequestMetadata) request.getMetadata()).getOverallAssessmentType());

        AerRequestPayload aerRequestPayload = (AerRequestPayload) request.getPayload();
        assertNull(aerRequestPayload.getVerificationReport());
        assertTrue(
                aerRequestPayload.getReviewGroupDecisions().entrySet()
                        .stream()
                        .noneMatch(entry -> AerReviewGroup.getVerificationDataReviewGroups(null)
                                .contains(entry.getKey())));

    }

    @Test
    void sendToRegulator_verificationPerformed_false() {
        final long accountId = 1L;
        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        final InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();
        final List<MonitoringPlanVersion> monitoringPlanVersions = getMonitoringPlanVersions();
        final PermitOriginatedData permitOriginatedData = getPermitOriginatedData();
        AerVerificationReport aerVerificationReport = AerVerificationReport.builder()
                .verificationData(AerVerificationData.builder()
                        .overallAssessment(VerifiedSatisfactoryOverallAssessment.builder().type(OverallAssessmentType.VERIFIED_AS_SATISFACTORY).build())
                        .build())
                .build();

        Request request = Request.builder()
                .accountId(accountId)
                .payload(AerRequestPayload.builder()
                        .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                        .verificationReport(aerVerificationReport)
                        .build())
                .metadata(AerRequestMetadata.builder()
                        .type(RequestMetadataType.AER)
                        .build())
                .build();

        final AerContainer container = AerContainer.builder()
                .installationOperatorDetails(installationOperatorDetails)
                .aer(Aer.builder().abbreviations(Abbreviations.builder().exist(false).build()).build())
                .permitOriginatedData(permitOriginatedData)
                .build();

        AerApplicationSubmitRequestTaskPayload taskPayload = AerApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AER_APPLICATION_SUBMIT_PAYLOAD)
                .installationOperatorDetails(installationOperatorDetails)
                .aer(container.getAer())
                .permitType(PermitType.GHGE)
                .permitOriginatedData(permitOriginatedData)
                .monitoringPlanVersions(monitoringPlanVersions)
                .verificationPerformed(false)
                .build();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        Request savedRequest = Request.builder()
                .accountId(1L)
                .payload(AerRequestPayload.builder()
                        .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                        .aer(container.getAer())
                        .verificationPerformed(false)
                        .monitoringPlanVersions(monitoringPlanVersions)
                        .permitOriginatedData(permitOriginatedData)
                        .build())
                .metadata(AerRequestMetadata.builder()
                        .type(RequestMetadataType.AER)
                        .emissions(BigDecimal.valueOf(12345.1234))
                        .build())
                .build();

        AerApplicationSubmittedRequestActionPayload actionPayload =
                AerApplicationSubmittedRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.AER_APPLICATION_SUBMITTED_PAYLOAD)
                        .aer(container.getAer())
                        .installationOperatorDetails(installationOperatorDetails)
                        .monitoringPlanVersions(monitoringPlanVersions)
                        .permitOriginatedData(permitOriginatedData)
                        .build();

        AerSubmitParams params = AerSubmitParams.builder()
                .accountId(request.getAccountId())
                .aerContainer(container)
                .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId()))
                .thenReturn(installationOperatorDetails);

        when(aerService.updateReportableEmissions(params))
                .thenReturn(BigDecimal.valueOf(12345.1234));

        // Invoke
        service.sendToRegulator(requestTask, pmrvUser);

        // Verify
        verify(aerValidatorService, times(1))
                .validate(container, accountId);
        verify(installationOperatorDetailsQueryService, times(1))
                .getInstallationOperatorDetails(request.getAccountId());
        verify(requestService, times(1)).addActionToRequest(requestTask.getRequest(),
                actionPayload, RequestActionType.AER_APPLICATION_SUBMITTED, pmrvUser.getUserId());
        verify(aerService, times(1))
                .updateReportableEmissions(params);

        assertThat(request.getPayload()).isEqualTo(savedRequest.getPayload());
        assertEquals(BigDecimal.valueOf(12345.1234), ((AerRequestMetadata) request.getMetadata()).getEmissions());
        assertNull(((AerRequestMetadata) request.getMetadata()).getOverallAssessmentType());

    }

    @Test
    void sendToVerifier() {
        final long accountId = 1L;
        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        final InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();
        final List<MonitoringPlanVersion> monitoringPlanVersions = getMonitoringPlanVersions();
        final PermitOriginatedData permitOriginatedData = getPermitOriginatedData();

        final AerApplicationRequestVerificationRequestTaskActionPayload taskActionPayload =
                AerApplicationRequestVerificationRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.AER_REQUEST_VERIFICATION_PAYLOAD)
                        .verificationSectionsCompleted(Map.of())
                        .build();

        Request request = Request.builder()
                .accountId(accountId)
                .payload(AerRequestPayload.builder()
                        .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                        .verificationSectionsCompleted(Map.of("abbreviations", List.of(true)))
                        .build())
                .build();
        final AerContainer container = AerContainer.builder()
                .installationOperatorDetails(installationOperatorDetails)
                .reportableEmissions(BigDecimal.ZERO)
                .aer(Aer.builder().abbreviations(Abbreviations.builder().exist(false).build()).build())
                .permitOriginatedData(permitOriginatedData)
                .build();
        AerApplicationSubmitRequestTaskPayload taskPayload = AerApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AER_APPLICATION_SUBMIT_PAYLOAD)
                .installationOperatorDetails(installationOperatorDetails)
                .aer(container.getAer())
                .permitType(PermitType.GHGE)
                .permitOriginatedData(permitOriginatedData)
                .monitoringPlanVersions(monitoringPlanVersions)
                .verificationPerformed(false)
                .build();
        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        Request savedRequest = Request.builder()
                .accountId(accountId)
                .payload(AerRequestPayload.builder()
                        .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                        .aer(container.getAer())
                        .monitoringPlanVersions(monitoringPlanVersions)
                        .permitOriginatedData(permitOriginatedData)
                        .verificationSectionsCompleted(Map.of())
                        .build())
                .build();

        AerApplicationSubmittedRequestActionPayload actionPayload =
                AerApplicationSubmittedRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.AER_APPLICATION_SUBMITTED_PAYLOAD)
                        .aer(container.getAer())
                        .installationOperatorDetails(installationOperatorDetails)
                        .monitoringPlanVersions(monitoringPlanVersions)
                        .permitOriginatedData(permitOriginatedData)
                        .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId()))
                .thenReturn(installationOperatorDetails);

        // Invoke
        service.sendToVerifier(taskActionPayload, requestTask, pmrvUser);

        // Verify
        verify(aerValidatorService, times(1))
                .validateAer(container);
        verify(installationOperatorDetailsQueryService, times(1))
                .getInstallationOperatorDetails(request.getAccountId());
        verify(requestService, times(1)).addActionToRequest(requestTask.getRequest(),
                actionPayload, RequestActionType.AER_APPLICATION_SENT_TO_VERIFIER, pmrvUser.getUserId());

        assertThat(request.getPayload()).isEqualTo(savedRequest.getPayload());
    }

    @Test
    void sendAmendsToVerifier() {
        final long accountId = 1L;
        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        final InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();
        final List<MonitoringPlanVersion> monitoringPlanVersions = getMonitoringPlanVersions();
        final PermitOriginatedData permitOriginatedData = getPermitOriginatedData();

        final AerApplicationRequestVerificationRequestTaskActionPayload taskActionPayload =
                AerApplicationRequestVerificationRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.AER_REQUEST_AMENDS_VERIFICATION_PAYLOAD)
                        .verificationSectionsCompleted(Map.of())
                        .build();

        Request request = Request.builder()
                .accountId(accountId)
                .payload(AerRequestPayload.builder()
                        .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                        .verificationSectionsCompleted(Map.of("abbreviations", List.of(true)))
                        .aer(Aer.builder()
                                .monitoringApproachEmissions(MonitoringApproachEmissions.builder()
                                        .monitoringApproachEmissions(Map.of(MonitoringApproachType.MEASUREMENT_CO2, MeasurementOfCO2Emissions.builder().build()))
                                        .build())
                                .build())
                        .build())
                .build();
        final AerContainer container = AerContainer.builder()
                .installationOperatorDetails(installationOperatorDetails)
                .reportableEmissions(BigDecimal.ZERO)
                .aer(Aer.builder()
                        .monitoringApproachEmissions(MonitoringApproachEmissions.builder()
                                .monitoringApproachEmissions(Map.of(MonitoringApproachType.MEASUREMENT_CO2, MeasurementOfCO2Emissions.builder().build()))
                                .build())
                        .abbreviations(Abbreviations.builder().exist(false).build()).build())
                .permitOriginatedData(permitOriginatedData)
                .build();
        AerApplicationAmendsSubmitRequestTaskPayload taskPayload =
                AerApplicationAmendsSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.AER_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
                        .installationOperatorDetails(installationOperatorDetails)
                        .aer(container.getAer())
                        .permitType(PermitType.GHGE)
                        .permitOriginatedData(permitOriginatedData)
                        .monitoringPlanVersions(monitoringPlanVersions)
                        .reviewSectionsCompleted(Map.of("section", true))
                        .aerSectionsCompleted(Map.of("section", List.of(true)))
                        .verificationPerformed(false)
                        .build();
        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        Request savedRequest = Request.builder()
                .accountId(accountId)
                .payload(AerRequestPayload.builder()
                        .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                        .aer(container.getAer())
                        .monitoringPlanVersions(monitoringPlanVersions)
                        .permitOriginatedData(permitOriginatedData)
                        .verificationSectionsCompleted(Map.of())
                        .aerSectionsCompleted(Map.of("section", List.of(true)))
                        .reviewSectionsCompleted(Map.of("section", true))
                        .build())
                .build();

        AerApplicationAmendsSubmittedRequestActionPayload actionPayload =
                AerApplicationAmendsSubmittedRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.AER_APPLICATION_AMENDS_SUBMITTED_PAYLOAD)
                        .aer(container.getAer())
                        .installationOperatorDetails(installationOperatorDetails)
                        .monitoringPlanVersions(monitoringPlanVersions)
                        .permitOriginatedData(permitOriginatedData)
                        .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId()))
                .thenReturn(installationOperatorDetails);

        // Invoke
        service.sendAmendsToVerifier(taskActionPayload, requestTask, pmrvUser);

        // Verify
        verify(aerValidatorService, times(1))
                .validateAer(container);
        verify(installationOperatorDetailsQueryService, times(1))
                .getInstallationOperatorDetails(request.getAccountId());
        verify(requestService, times(1)).addActionToRequest(requestTask.getRequest(),
                actionPayload, RequestActionType.AER_APPLICATION_AMENDS_SENT_TO_VERIFIER, pmrvUser.getUserId());

        assertThat(request.getPayload()).isEqualTo(savedRequest.getPayload());
    }

    @Test
    void sendToOperator() {
        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        final InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();
        final List<MonitoringPlanVersion> monitoringPlanVersions = getMonitoringPlanVersions();
        final PermitOriginatedData permitOriginatedData = getPermitOriginatedData();
        final Aer aer = Aer.builder().build();
        final AerVerificationReport verificationReport = AerVerificationReport.builder().build();
        final Year year = Year.of(2023);
        final UUID attachmentId = UUID.randomUUID();

        Request request = Request.builder()
                .payload(AerRequestPayload.builder().payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD).build())
                .build();

        AerApplicationVerificationSubmitRequestTaskPayload taskPayload =
                AerApplicationVerificationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.AER_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD)
                        .installationOperatorDetails(installationOperatorDetails)
                        .aer(aer)
                        .permitOriginatedData(permitOriginatedData)
                        .permitType(PermitType.GHGE)
                        .reportingYear(year)
                        .monitoringPlanVersions(monitoringPlanVersions)
                        .verificationReport(verificationReport)
                        .verificationAttachments(Map.of(attachmentId, "attachmentName"))
                        .build();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        AerApplicationVerificationSubmittedRequestActionPayload actionPayload =
                AerApplicationVerificationSubmittedRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.AER_APPLICATION_VERIFICATION_SUBMITTED_PAYLOAD)
                        .aer(aer)
                        .verificationReport(verificationReport)
                        .installationOperatorDetails(installationOperatorDetails)
                        .monitoringPlanVersions(monitoringPlanVersions)
                        .permitOriginatedData(permitOriginatedData)
                        .reportingYear(year)
                        .verificationAttachments(Map.of(attachmentId, "attachmentName"))
                        .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId()))
                .thenReturn(installationOperatorDetails);

        Request savedRequest = Request.builder()
                .accountId(1L)
                .payload(AerRequestPayload.builder()
                        .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                        .verifiedAer(aer)
                        .verificationReport(verificationReport)
                        .verificationPerformed(true)
                        .verificationSectionsCompleted(Map.of())
                        .verificationAttachments(Map.of(attachmentId, "attachmentName"))
                        .build())
                .build();

        // Invoke
        service.sendToOperator(requestTask, pmrvUser);

        // Verify
        verify(aerValidatorService, times(1))
                .validateVerificationReport(verificationReport, permitOriginatedData);
        verify(installationOperatorDetailsQueryService, times(1))
                .getInstallationOperatorDetails(request.getAccountId());
        verify(requestService, times(1)).addActionToRequest(requestTask.getRequest(),
                actionPayload, RequestActionType.AER_APPLICATION_VERIFICATION_SUBMITTED, pmrvUser.getUserId());

        assertThat(request.getPayload()).isEqualTo(savedRequest.getPayload());
    }

    private InstallationOperatorDetails getInstallationOperatorDetails() {
        return InstallationOperatorDetails.builder()
                .installationName("Account name")
                .siteName("Site name")
                .installationLocation(LocationOnShoreDTO.builder()
                        .type(LocationType.ONSHORE)
                        .gridReference("ST330000")
                        .address(AddressDTO.builder()
                                .line1("line1")
                                .city("city")
                                .country("GB")
                                .postcode("postcode")
                                .build())
                        .build())
                .operator("le")
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

    private PermitOriginatedData getPermitOriginatedData() {
        return PermitOriginatedData.builder()
                .permitMonitoringApproachMonitoringTiers(MonitoringApproachMonitoringTiers.builder()
                        .calculationSourceStreamParamMonitoringTiers(Map.of("SS-UUID-1",
                                List.of(
                                        CalculationActivityDataMonitoringTier.builder().type(CalculationParameterType.ACTIVITY_DATA).tier(CalculationActivityDataTier.TIER_3).build(),
                                        CalculationNetCalorificValueMonitoringTier.builder().type(CalculationParameterType.NET_CALORIFIC_VALUE).tier(CalculationNetCalorificValueTier.TIER_2B).build()
                                )
                        ))
                        .build())
                .permitNotificationIds(List.of("AEMN1-1", "AEMN1-4"))
                .permitType(PermitType.GHGE)
                .build();
    }

    private List<MonitoringPlanVersion> getMonitoringPlanVersions() {
        return List.of(MonitoringPlanVersion.builder().permitId("permitId").permitConsolidationNumber(1)
                .endDate(LocalDate.now().minusYears(2)).build());
    }
}
