package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.AER_AMEND_APPLICATION_VERIFICATION_SUBMIT;

import java.time.Year;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;
import uk.gov.pmrv.api.reporting.domain.verification.OpinionStatement;
import uk.gov.pmrv.api.reporting.domain.verification.VerificationTeamDetails;
import uk.gov.pmrv.api.verificationbody.domain.verificationbodydetails.VerificationBodyDetails;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init.aerverificationreport.AerVerificationCombustionSourcesInitializationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init.aerverificationreport.AerVerificationProcessSourcesInitializationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init.aerverificationreport.AerVerificationRegulatedActivitiesInitializationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init.aerverificationreport.AerVerificationReportSectionInitializationService;

@ExtendWith(MockitoExtension.class)
class AerApplicationVerificationSubmitInitializerTest {

    @InjectMocks
    private AerApplicationVerificationSubmitInitializer initializer;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Mock
    private VerificationBodyDetailsQueryService verificationBodyDetailsQueryService;

    @Spy
    private ArrayList<AerVerificationReportSectionInitializationService> aerVerificationReportSectionInitializationServices;

    @Mock
    private AerVerificationCombustionSourcesInitializationService aerVerificationCombustionSourcesInitializationService;

    @Mock
    private AerVerificationProcessSourcesInitializationService aerVerificationProcessSourcesInitializationService;

    @Mock
    private AerVerificationRegulatedActivitiesInitializationService aerVerificationRegulatedActivitiesInitializationService;

    @BeforeEach
    void setUp() {
        aerVerificationReportSectionInitializationServices.add(aerVerificationCombustionSourcesInitializationService);
        aerVerificationReportSectionInitializationServices.add(aerVerificationProcessSourcesInitializationService);
        aerVerificationReportSectionInitializationServices.add(aerVerificationRegulatedActivitiesInitializationService);
    }
    
    @Test
    void initializePayload_no_vb_change() {
        final long accountId = 1L;
        Long requestVBId = 2L;
        final Year reportingYear = Year.now().minusYears(1);
        
        AerVerificationReport requestVerificationReport = AerVerificationReport.builder()
        		.verificationBodyId(requestVBId)
        		.verificationBodyDetails(VerificationBodyDetails.builder()
        				.accreditationReferenceNumber("old vb details")
        				.build())
        		.verificationData(AerVerificationData.builder()
        				.verificationTeamDetails(VerificationTeamDetails.builder()
        						.authorisedSignatoryName("auth")
        						.build())
        				.build())
        		.build();
        
        final AerRequestPayload requestPayload = AerRequestPayload.builder()
                .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                .aer(Aer.builder().abbreviations(Abbreviations.builder().exist(false).build()).build())
                .verificationReport(requestVerificationReport)
                .build();
        
        final Request request = Request.builder()
                .accountId(accountId)
                .payload(requestPayload)
                .verificationBodyId(requestVBId)
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
        
        VerificationBodyDetails latestVerificationBodyDetails = VerificationBodyDetails.builder()
                .accreditationReferenceNumber("accr_ref_number")
                .build();


        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
                .thenReturn(installationOperatorDetails);
        when(verificationBodyDetailsQueryService.getVerificationBodyDetails(requestVBId))
        	.thenReturn(Optional.of(latestVerificationBodyDetails));

        // Invoke
        RequestTaskPayload result = initializer.initializePayload(request);
        
        assertThat(requestPayload.getVerificationReport()).isNotNull();
        assertEquals(RequestTaskPayloadType.AER_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD, result.getPayloadType());

        AerApplicationVerificationSubmitRequestTaskPayload resultPayload = (AerApplicationVerificationSubmitRequestTaskPayload) result;
        assertThat(resultPayload.getVerificationReport()).isEqualTo(AerVerificationReport.builder()
        		.verificationBodyId(requestVBId)
        		.verificationBodyDetails(latestVerificationBodyDetails)
        		.verificationData(requestPayload.getVerificationReport().getVerificationData())
        		.build());
        assertThat(resultPayload.getReportingYear()).isEqualTo(reportingYear);
        assertThat(resultPayload.getInstallationOperatorDetails()).isEqualTo(installationOperatorDetails);

        // Verify
        verify(installationOperatorDetailsQueryService, times(1)).getInstallationOperatorDetails(accountId);
        verify(verificationBodyDetailsQueryService, times(1)).getVerificationBodyDetails(requestVBId);
    }
    
    @Test
    void initializePayload_vb_change() {
        final long accountId = 1L;
        Long requestVBId = 1L;
        Long reportVBId = 2L;
        final Year reportingYear = Year.now().minusYears(1);
        
        AerVerificationReport requestVerificationReport = AerVerificationReport.builder()
        		.verificationBodyId(reportVBId)
        		.verificationBodyDetails(VerificationBodyDetails.builder()
        				.accreditationReferenceNumber("old vb details")
        				.build())
        		.verificationData(AerVerificationData.builder()
        				.verificationTeamDetails(VerificationTeamDetails.builder()
        						.authorisedSignatoryName("auth")
        						.build())
        				.build())
        		.build();
        
        final AerRequestPayload requestPayload = AerRequestPayload.builder()
                .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                .aer(Aer.builder().abbreviations(Abbreviations.builder().exist(false).build()).build())
                .verificationReport(requestVerificationReport)
                .build();
        
        final Request request = Request.builder()
                .accountId(accountId)
                .payload(requestPayload)
                .verificationBodyId(requestVBId)
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
        
        VerificationBodyDetails latestVerificationBodyDetails = VerificationBodyDetails.builder()
                .accreditationReferenceNumber("accr_ref_number")
                .build();


        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
                .thenReturn(installationOperatorDetails);
        when(verificationBodyDetailsQueryService.getVerificationBodyDetails(requestVBId))
        	.thenReturn(Optional.of(latestVerificationBodyDetails));

        // Invoke
        RequestTaskPayload result = initializer.initializePayload(request);
        
        assertThat(requestPayload.getVerificationReport()).isNull();
        assertThat(requestPayload.getVerificationSectionsCompleted()).isEmpty();
        assertThat(requestPayload.getVerificationAttachments()).isEmpty();
        assertEquals(RequestTaskPayloadType.AER_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD, result.getPayloadType());

        AerApplicationVerificationSubmitRequestTaskPayload resultPayload = (AerApplicationVerificationSubmitRequestTaskPayload) result;
        assertThat(resultPayload.getVerificationReport()).isEqualTo(AerVerificationReport.builder()
        		.verificationBodyId(requestVBId)
        		.verificationBodyDetails(latestVerificationBodyDetails)
        		.verificationData(AerVerificationData.builder()
						.opinionStatement(OpinionStatement.builder().build())
						.build())
        		.build());
        assertThat(resultPayload.getReportingYear()).isEqualTo(reportingYear);
        assertThat(resultPayload.getInstallationOperatorDetails()).isEqualTo(installationOperatorDetails);
        

        // Verify
        verify(installationOperatorDetailsQueryService, times(1)).getInstallationOperatorDetails(accountId);
        verify(verificationBodyDetailsQueryService, times(1)).getVerificationBodyDetails(requestVBId);
    }

    @Test
    void getRequestTaskTypes() {
        Assertions.assertEquals(initializer.getRequestTaskTypes(),
                Set.of(RequestTaskType.AER_APPLICATION_VERIFICATION_SUBMIT, AER_AMEND_APPLICATION_VERIFICATION_SUBMIT));
    }
}
