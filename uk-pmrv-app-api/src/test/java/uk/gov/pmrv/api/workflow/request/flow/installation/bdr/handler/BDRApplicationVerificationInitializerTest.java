package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.handler;


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
import uk.gov.pmrv.api.verificationbody.domain.verificationbodydetails.VerificationBodyDetails;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDR;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRStatusApplicationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRVerificationData;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRVerificationReport;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BDRApplicationVerificationInitializerTest {


    @InjectMocks
    private BDRApplicationVerificationInitializer initializer;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Mock
    private VerificationBodyDetailsQueryService verificationBodyDetailsQueryService;

    @Test
    void initializePayload_when_vb_has_not_been_changed_initialize_the_payload_without_resetting_verification() {
        final long accountId = 1L;
        Long requestVBId = 2L;
        final UUID verificationAttachment = UUID.randomUUID();

        BDRVerificationReport verificationReport = BDRVerificationReport.builder()
        		.verificationBodyId(requestVBId)
        		.verificationBodyDetails(VerificationBodyDetails.builder()
        				.accreditationReferenceNumber("old vb details")
        				.build())
        		.verificationData(BDRVerificationData.builder().build())
        		.build();

        Map<UUID, String> verificationAttachments = Map.of(verificationAttachment, "test");

        Map<String, List<Boolean>> verificationSectionsCompleted = Map.of("test", List.of(true));

        final BDRRequestPayload requestPayload = BDRRequestPayload.builder()
                .payloadType(RequestPayloadType.BDR_REQUEST_PAYLOAD)
                .bdr(BDR.builder().hasMmp(false).isApplicationForFreeAllocation(true).statusApplicationType(BDRStatusApplicationType.HSE).build())
                .verificationReport(verificationReport)
                .verificationAttachments(verificationAttachments)
                .verificationSectionsCompleted(verificationSectionsCompleted)
                .build();

        final Request request = Request.builder()
                .accountId(accountId)
                .payload(requestPayload)
                .verificationBodyId(requestVBId)
                .metadata(BDRRequestMetadata.builder().build())
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

        BDRApplicationVerificationSubmitRequestTaskPayload result = (BDRApplicationVerificationSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertThat(requestPayload.getVerificationReport()).isNotNull();
        assertThat(requestPayload.getVerificationAttachments()).isEqualTo(verificationAttachments);
        assertThat(requestPayload.getVerificationSectionsCompleted()).isEqualTo(verificationSectionsCompleted);
        assertEquals(RequestTaskPayloadType.BDR_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD, result.getPayloadType());

        assertThat(result.getVerificationReport()).isEqualTo(BDRVerificationReport.builder()
        		.verificationBodyId(requestVBId)
        		.verificationBodyDetails(latestVerificationBodyDetails)
        		.verificationData(requestPayload.getVerificationReport().getVerificationData())
        		.build());

        verify(installationOperatorDetailsQueryService, times(1)).getInstallationOperatorDetails(accountId);
        verify(verificationBodyDetailsQueryService, times(1)).getVerificationBodyDetails(requestVBId);
    }

    @Test
    void initializePayload_when_vb_has_been_changed_initialize_the_payload_and_reset_verification() {
        final long accountId = 1L;
        Long requestVBId = 1L;
        Long reportVBId = 2L;
        final UUID verificationAttachment = UUID.randomUUID();

        BDRVerificationReport verificationReport = BDRVerificationReport.builder()
        		.verificationBodyId(reportVBId)
        		.verificationBodyDetails(VerificationBodyDetails.builder()
        				.accreditationReferenceNumber("old vb details")
        				.build())
        		.verificationData(BDRVerificationData.builder().build())
        		.build();

        Map<UUID, String> verificationAttachments = Map.of(verificationAttachment, "test");

        Map<String, List<Boolean>> verificationSectionsCompleted = Map.of("test", List.of(true));

        final BDRRequestPayload requestPayload = BDRRequestPayload.builder()
                .payloadType(RequestPayloadType.BDR_REQUEST_PAYLOAD)
                .bdr(BDR.builder().hasMmp(false).isApplicationForFreeAllocation(true).statusApplicationType(BDRStatusApplicationType.HSE).build())
                .verificationReport(verificationReport)
                .verificationAttachments(verificationAttachments)
                .verificationSectionsCompleted(verificationSectionsCompleted)
                .build();

        final Request request = Request.builder()
                .accountId(accountId)
                .payload(requestPayload)
                .verificationBodyId(requestVBId)
                .metadata(BDRRequestMetadata.builder().build())
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

        BDRApplicationVerificationSubmitRequestTaskPayload result = (BDRApplicationVerificationSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertThat(requestPayload.getVerificationReport()).isNull();
        assertThat(requestPayload.getVerificationAttachments()).isEmpty();
        assertThat(requestPayload.getVerificationSectionsCompleted()).isEmpty();
        assertEquals(RequestTaskPayloadType.BDR_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD, result.getPayloadType());

        assertThat(result.getVerificationReport()).isEqualTo(BDRVerificationReport.builder()
        		.verificationBodyId(requestVBId)
        		.verificationBodyDetails(latestVerificationBodyDetails)
        		.verificationData(BDRVerificationData.builder().build())
        		.build());

        verify(installationOperatorDetailsQueryService, times(1)).getInstallationOperatorDetails(accountId);
        verify(verificationBodyDetailsQueryService, times(1)).getVerificationBodyDetails(requestVBId);
    }


    @Test
    void getRequestTaskTypes() {
        Assertions.assertEquals(initializer.getRequestTaskTypes(),
                Set.of(RequestTaskType.BDR_APPLICATION_VERIFICATION_SUBMIT,RequestTaskType.BDR_AMEND_APPLICATION_VERIFICATION_SUBMIT));
    }
}
