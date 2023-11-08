package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;

import org.junit.jupiter.api.Assertions;
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
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationActivityDataTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationNetCalorificValueTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationParameterType;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.MonitoringApproachMonitoringTiers;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationActivityDataMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationNetCalorificValueMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;
import uk.gov.pmrv.api.verificationbody.domain.verificationbodydetails.VerificationBodyDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerApplicationReviewInitializerTest {

    @InjectMocks
    private AerApplicationReviewInitializer initializer;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Mock
    private RequestVerificationService<AerVerificationReport> requestVerificationService;

    @Test
    void initializePayload() {
        final long accountId = 1L;
        final long vbId = 2L;
        final Year reportingYear = Year.now().minusYears(1);
        final PermitOriginatedData permitOriginatedData = PermitOriginatedData.builder()
                .permitMonitoringApproachMonitoringTiers(MonitoringApproachMonitoringTiers.builder()
                        .calculationSourceStreamParamMonitoringTiers(Map.of("SS-UUID-1",
                                List.of(
                                        CalculationActivityDataMonitoringTier.builder().type(CalculationParameterType.ACTIVITY_DATA).tier(CalculationActivityDataTier.TIER_3).build(),
                                        CalculationNetCalorificValueMonitoringTier.builder().type(CalculationParameterType.NET_CALORIFIC_VALUE).tier(CalculationNetCalorificValueTier.TIER_2B).build()
                                )
                        ))
                        .build())
                .permitType(PermitType.GHGE)
                .permitNotificationIds(List.of("AEMN1-1", "AEMN1-4"))
                .build();
        final AerRequestPayload requestPayload = AerRequestPayload.builder()
                .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                .aer(Aer.builder().abbreviations(Abbreviations.builder().exist(false).build()).build())
                .aerAttachments(Map.of(UUID.randomUUID(), "file"))
                .verificationReport(AerVerificationReport.builder().build())
                .verificationPerformed(true)
                .permitOriginatedData(permitOriginatedData)
                .build();
        final Request request = Request.builder()
                .accountId(accountId)
                .payload(requestPayload)
                .verificationBodyId(vbId)
                .metadata(AerRequestMetadata.builder().year(reportingYear).build())
                .build();
        final InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder()
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
        final VerificationBodyDetails bodyDetails = VerificationBodyDetails.builder()
                .name("name2")
                .accreditationReferenceNumber("accRefNum2")
                .address(AddressDTO.builder()
                        .line1("line2")
                        .city("city2")
                        .country("GR")
                        .postcode("postcode2")
                        .build())
                .emissionTradingSchemes(Set.of(EmissionTradingScheme.UK_ETS_INSTALLATIONS))
                .build();

        final AerApplicationReviewRequestTaskPayload expected = AerApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AER_APPLICATION_REVIEW_PAYLOAD)
                .installationOperatorDetails(installationOperatorDetails)
                .aer(requestPayload.getAer())
                .aerAttachments(requestPayload.getAerAttachments())
                .verificationReport(AerVerificationReport.builder()
                        .verificationBodyDetails(bodyDetails)
                        .build())
                .permitOriginatedData(permitOriginatedData)
                .reportingYear(reportingYear)
                .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
                .thenReturn(installationOperatorDetails);
        when(requestVerificationService.getVerificationBodyDetails(new AerVerificationReport(), request.getVerificationBodyId()))
                .thenReturn(bodyDetails);

        // Invoke
        RequestTaskPayload actual = initializer.initializePayload(request);

        // Verify
        verify(installationOperatorDetailsQueryService, times(1))
                .getInstallationOperatorDetails(accountId);
        verify(requestVerificationService, times(1))
                .getVerificationBodyDetails(any(), eq(request.getVerificationBodyId()));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void initializePayload_no_verification_report() {
        final long accountId = 1L;
        final Year reportingYear = Year.now().minusYears(1);
        final AerRequestPayload requestPayload = AerRequestPayload.builder()
                .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                .aer(Aer.builder().abbreviations(Abbreviations.builder().exist(false).build()).build())
                .aerAttachments(Map.of(UUID.randomUUID(), "file"))
                .build();
        final Request request = Request.builder()
                .accountId(accountId)
                .payload(requestPayload)
                .metadata(AerRequestMetadata.builder().year(reportingYear).build())
                .build();
        final InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder()
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

        final AerApplicationReviewRequestTaskPayload expected = AerApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AER_APPLICATION_REVIEW_PAYLOAD)
                .installationOperatorDetails(installationOperatorDetails)
                .aer(requestPayload.getAer())
                .aerAttachments(requestPayload.getAerAttachments())
                .reportingYear(reportingYear)
                .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
                .thenReturn(installationOperatorDetails);

        // Invoke
        RequestTaskPayload actual = initializer.initializePayload(request);

        // Verify
        verify(installationOperatorDetailsQueryService, times(1))
                .getInstallationOperatorDetails(accountId);
        verify(requestVerificationService, never()).getVerificationBodyDetails(any(), anyLong());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getRequestTaskTypes() {
        Assertions.assertEquals(initializer.getRequestTaskTypes(),
                Set.of(RequestTaskType.AER_APPLICATION_REVIEW));
    }
}
