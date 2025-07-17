package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALR;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestMetaData;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRVerificationData;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRVerificationReport;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ALRApplicationVerificationInitializerTest {

    @InjectMocks
    private ALRApplicationVerificationInitializer initializer;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Mock
    private VerificationBodyDetailsQueryService verificationBodyDetailsQueryService;

    @Test
    void initializePayload_when_vb_has_not_been_changed_initialize_the_payload_without_resetting_verification() {
        final long accountId = 1L;
        final Long vbId = 2L;
        final UUID attachmentId = UUID.randomUUID();

        var initialVBDetails = buildVBDetails("old vb details");
        var latestVBDetails = buildVBDetails("accr_ref_number");

        ALRRequestPayload requestPayload = buildRequestPayload(vbId, initialVBDetails, attachmentId, true);

        Request request = Request.builder()
                .accountId(accountId)
                .payload(requestPayload)
                .verificationBodyId(vbId)
                .metadata(ALRRequestMetaData.builder().build())
                .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
                .thenReturn(buildInstallationOperatorDetails());
        when(verificationBodyDetailsQueryService.getVerificationBodyDetails(vbId))
                .thenReturn(Optional.of(latestVBDetails));

        ALRApplicationVerificationSubmitRequestTaskPayload result =
                (ALRApplicationVerificationSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertThat(requestPayload.getVerificationReport()).isNotNull();
        assertThat(requestPayload.getVerificationAttachments()).isEqualTo(Map.of(attachmentId, "test"));
        assertThat(requestPayload.getVerificationSectionsCompleted()).isEqualTo(Map.of("test", List.of(true)));

        assertEquals(RequestTaskPayloadType.ALR_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD, result.getPayloadType());

        assertThat(result.getVerificationReport()).isEqualTo(ALRVerificationReport.builder()
                .verificationBodyId(vbId)
                .verificationBodyDetails(latestVBDetails)
                .verificationData(ALRVerificationData.builder().build())
                .build());

        verify(installationOperatorDetailsQueryService).getInstallationOperatorDetails(accountId);
        verify(verificationBodyDetailsQueryService).getVerificationBodyDetails(vbId);
    }

    @Test
    void initializePayload_when_vb_has_been_changed_initialize_the_payload_and_reset_verification() {
        final long accountId = 1L;
        final Long requestVBId = 1L;
        final Long reportVBId = 2L;
        final UUID attachmentId = UUID.randomUUID();

        var initialVBDetails = buildVBDetails("old vb details");
        var latestVBDetails = buildVBDetails("accr_ref_number");

        ALRRequestPayload requestPayload = buildRequestPayload(reportVBId, initialVBDetails, attachmentId, true);

        Request request = Request.builder()
                .accountId(accountId)
                .payload(requestPayload)
                .verificationBodyId(requestVBId)
                .metadata(ALRRequestMetaData.builder().build())
                .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
                .thenReturn(buildInstallationOperatorDetails());
        when(verificationBodyDetailsQueryService.getVerificationBodyDetails(requestVBId))
                .thenReturn(Optional.of(latestVBDetails));

        ALRApplicationVerificationSubmitRequestTaskPayload result =
                (ALRApplicationVerificationSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertThat(requestPayload.getVerificationReport()).isNull();
        assertThat(requestPayload.getVerificationAttachments()).isEmpty();
        assertThat(requestPayload.getVerificationSectionsCompleted()).isEmpty();

        assertEquals(RequestTaskPayloadType.ALR_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD, result.getPayloadType());

        assertThat(result.getVerificationReport()).isEqualTo(ALRVerificationReport.builder()
                .verificationBodyId(requestVBId)
                .verificationBodyDetails(latestVBDetails)
                .verificationData(ALRVerificationData.builder().build())
                .build());

        verify(installationOperatorDetailsQueryService).getInstallationOperatorDetails(accountId);
        verify(verificationBodyDetailsQueryService).getVerificationBodyDetails(requestVBId);
    }

    @Test
    void getRequestTaskTypes() {
        assertEquals(
                Set.of(
                        RequestTaskType.ALR_APPLICATION_VERIFICATION_SUBMIT),
                initializer.getRequestTaskTypes()
        );
    }

    private InstallationOperatorDetails buildInstallationOperatorDetails() {
        AddressDTO address = AddressDTO.builder()
                .line1("line1")
                .city("city")
                .country("GB")
                .postcode("postcode")
                .build();

        return InstallationOperatorDetails.builder()
                .installationName("Account name")
                .siteName("Site name")
                .installationLocation(LocationOnShoreDTO.builder()
                        .type(LocationType.ONSHORE)
                        .gridReference("ST330000")
                        .address(address)
                        .build())
                .operator("le")
                .operatorType(LegalEntityType.LIMITED_COMPANY)
                .companyReferenceNumber("408812")
                .operatorDetailsAddress(address)
                .build();
    }

    private VerificationBodyDetails buildVBDetails(String ref) {
        return VerificationBodyDetails.builder()
                .accreditationReferenceNumber(ref)
                .build();
    }

    private ALRRequestPayload buildRequestPayload(Long vbId, VerificationBodyDetails vbDetails,
                                                  UUID attachmentId, boolean populateVerificationData) {
        ALRVerificationData data = populateVerificationData ? ALRVerificationData.builder().build() : null;

        return ALRRequestPayload.builder()
                .payloadType(RequestPayloadType.ALR_REQUEST_PAYLOAD)
                .alr(ALR.builder().build())
                .verificationReport(ALRVerificationReport.builder()
                        .verificationBodyId(vbId)
                        .verificationBodyDetails(vbDetails)
                        .verificationData(data)
                        .build())
                .verificationAttachments(Map.of(attachmentId, "test"))
                .verificationSectionsCompleted(Map.of("test", List.of(true)))
                .build();
    }
}
