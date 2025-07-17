package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.*;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.*;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation.ALRValidationService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ALRSubmitServiceTest {

    @InjectMocks
    private ALRSubmitService service;

    @Mock
    private ALRValidationService alrValidationService;

    @Mock
    private RequestService requestService;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    private final long accountId = 1L;
    private final UUID attachmentId = UUID.randomUUID();
    private final UUID verificationAttachmentId = UUID.randomUUID();
    private final AppUser appUser = AppUser.builder().userId("userId").build();

    @Test
    void applySaveAction() {

        final Map<String, Boolean> expectedSectionsCompleted = new HashMap<>();
        expectedSectionsCompleted.put("test",false);

        final ALRApplicationSubmitRequestTaskPayload expectedTaskPayload =
                ALRApplicationSubmitRequestTaskPayload
                        .builder()
                        .verificationPerformed(true)
                        .build();

        final RequestTask requestTask = RequestTask.builder()
                .payload(expectedTaskPayload)
                .build();

        final ALRApplicationSaveRequestTaskActionPayload expectedTaskActionPayload =
                ALRApplicationSaveRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.ALR_APPLICATION_SAVE_PAYLOAD)
                        .alr(ALR.builder().build())
                        .alrSectionsCompleted(expectedSectionsCompleted)
                        .build();

        service.applySaveAction(requestTask, expectedTaskActionPayload);

        assertEquals(expectedTaskPayload.getAlr(), expectedTaskActionPayload.getAlr());
        assertEquals(expectedTaskPayload.getAlrSectionsCompleted(), expectedTaskActionPayload.getAlrSectionsCompleted());
    }

    @Test
    void submitToVerifier() {
        UUID alrFile = UUID.randomUUID();
        UUID verificationAttachmentId = UUID.randomUUID();

        ALR alr = ALR.builder().alrFile(alrFile).build();
        ALRVerificationReport report = buildVerificationReport();

        Map<UUID, String> verificationAttachments = Map.of(verificationAttachmentId, "test");

        ALRRequestPayload payload = ALRRequestPayload.builder()
                .payloadType(RequestPayloadType.ALR_REQUEST_PAYLOAD)
                .verificationReport(report)
                .verificationAttachments(verificationAttachments)
                .build();

        Request request = buildRequest(payload);

        ALRApplicationSubmitRequestTaskPayload taskPayload = ALRApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.ALR_SUBMIT_PAYLOAD)
                .alr(alr)
                .verificationPerformed(true)
                .build();

        RequestTask requestTask = buildRequestTask(request, taskPayload);

        Request expectedRequest = Request.builder()
                .accountId(accountId)
                .payload(ALRRequestPayload.builder()
                        .payloadType(RequestPayloadType.ALR_REQUEST_PAYLOAD)
                        .alr(alr)
                        .verificationPerformed(true)
                        .verificationReport(report)
                        .verificationAttachments(verificationAttachments)
                        .build())
                .metadata(ALRRequestMetaData.builder().type(RequestMetadataType.ALR).build())
                .build();

        InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();

        ALRApplicationSubmittedRequestActionPayload expectedActionPayload = ALRApplicationSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.ALR_APPLICATION_SUBMITTED_PAYLOAD)
                .alr(alr)
                .verificationPerformed(true)
                .verificationReport(report)
                .installationOperatorDetails(installationOperatorDetails)
                .verificationAttachments(verificationAttachments)
                .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
                .thenReturn(installationOperatorDetails);

        service.submitToVerifier(ALRApplicationSubmitToVerifierRequestTaskActionPayload.builder().build(), requestTask, appUser);

        verify(alrValidationService).validateALR(alr);
        verify(installationOperatorDetailsQueryService).getInstallationOperatorDetails(accountId);
        verify(requestService).addActionToRequest(eq(request), eq(expectedActionPayload),
                eq(RequestActionType.ALR_APPLICATION_SENT_TO_VERIFIER), eq(appUser.getUserId()));

        assertThat(request.getPayload()).isEqualTo(expectedRequest.getPayload());
    }

    @Test
    void createApplicationSubmittedRequestActionPayload_verificationPerformed_setsVerificationFields() {
        ALR alr = ALR.builder().build();
        ALRVerificationReport report = buildVerificationReport();
        ALRRequestPayload requestPayload = buildRequestPayload(null, report, false);
        Request request = buildRequest(requestPayload);
        ALRApplicationSubmitRequestTaskPayload taskPayload = buildTaskPayload(alr, true, false);
        RequestTask requestTask = buildRequestTask(request, taskPayload);
        InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();

        ALRApplicationSubmittedRequestActionPayload expectedPayload = ALRApplicationSubmittedRequestActionPayload.builder()
                .alr(alr)
                .installationOperatorDetails(installationOperatorDetails)
                .alrAttachments(Map.of(attachmentId, "Test"))
                .payloadType(RequestActionPayloadType.ALR_APPLICATION_SUBMITTED_PAYLOAD)
                .verificationPerformed(true)
                .verificationReport(report)
                .verificationAttachments(Map.of(verificationAttachmentId, "test"))
                .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
                .thenReturn(installationOperatorDetails);

        ALRApplicationSubmittedRequestActionPayload result = service.createApplicationSubmittedRequestActionPayload(
                requestTask, taskPayload, requestPayload, RequestActionPayloadType.ALR_APPLICATION_SUBMITTED_PAYLOAD);

        assertThat(result).isEqualTo(expectedPayload);
    }

    @Test
    void createApplicationSubmittedRequestActionPayload_verificationNotPerformed_doesNotSetVerificationFields() {
        ALR alr = ALR.builder().build();
        ALRVerificationReport report = buildVerificationReport();
        ALRRequestPayload requestPayload = buildRequestPayload(null, report, false);
        Request request = buildRequest(requestPayload);
        ALRApplicationSubmitRequestTaskPayload taskPayload = buildTaskPayload(alr, false, false);
        RequestTask requestTask = buildRequestTask(request, taskPayload);
        InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();

        ALRApplicationSubmittedRequestActionPayload expectedPayload = ALRApplicationSubmittedRequestActionPayload.builder()
                .alr(alr)
                .installationOperatorDetails(installationOperatorDetails)
                .alrAttachments(Map.of(attachmentId, "Test"))
                .payloadType(RequestActionPayloadType.ALR_APPLICATION_SUBMITTED_PAYLOAD)
                .verificationPerformed(false)
                .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
                .thenReturn(installationOperatorDetails);

        ALRApplicationSubmittedRequestActionPayload result = service.createApplicationSubmittedRequestActionPayload(
                requestTask, taskPayload, requestPayload, RequestActionPayloadType.ALR_APPLICATION_SUBMITTED_PAYLOAD);

        assertThat(result).isEqualTo(expectedPayload);
        assertThat(result.getVerificationReport()).isNull();
        assertThat(result.getVerificationAttachments()).isEmpty();
    }

    @Test
    void submitALR() {
        ALR alr = ALR.builder().build();
        ALRVerificationReport report = buildVerificationReport();
        ALRRequestPayload requestPayload = buildRequestPayload(null, report, false);
        Request request = buildRequest(requestPayload);
        ALRApplicationSubmitRequestTaskPayload taskPayload = buildTaskPayload(alr, true, true);
        RequestTask requestTask = buildRequestTask(request, taskPayload);
        InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();

        ALRApplicationSubmittedRequestActionPayload requestActionPayload = ALRApplicationSubmittedRequestActionPayload.builder()
                .alr(alr)
                .installationOperatorDetails(installationOperatorDetails)
                .alrAttachments(Map.of(attachmentId, "Test"))
                .payloadType(RequestActionPayloadType.ALR_APPLICATION_SUBMITTED_PAYLOAD)
                .verificationAttachments(Map.of(verificationAttachmentId, "test"))
                .verificationReport(report)
                .verificationPerformed(true)
                .build();

        service.submitALR(requestPayload, requestTask, appUser,
                RequestActionType.ALR_APPLICATION_SENT_TO_VERIFIER,
                requestActionPayload, taskPayload.getAlrSectionsCompleted());

        verify(requestService).addActionToRequest(request, requestActionPayload,
                RequestActionType.ALR_APPLICATION_SENT_TO_VERIFIER, appUser.getUserId());

        assertThat(requestPayload.getAlr()).isEqualTo(alr);
        assertThat(requestPayload.getAlrAttachments()).isEqualTo(taskPayload.getAlrAttachments());
        assertThat(requestPayload.getAlrSectionsCompleted()).isEqualTo(taskPayload.getAlrSectionsCompleted());
        assertThat(requestPayload.isVerificationPerformed()).isTrue();
    }

    private InstallationOperatorDetails getInstallationOperatorDetails() {
        AddressDTO address = AddressDTO.builder()
                .line1("line1")
                .city("city")
                .country("GB")
                .postcode("postcode")
                .build();

        return InstallationOperatorDetails.builder()
                .installationName("Install")
                .siteName("Site")
                .installationLocation(LocationOnShoreDTO.builder()
                        .type(LocationType.ONSHORE)
                        .gridReference("GR")
                        .address(address)
                        .build())
                .operator("operator")
                .operatorType(LegalEntityType.LIMITED_COMPANY)
                .companyReferenceNumber("123456")
                .operatorDetailsAddress(address)
                .build();
    }

    private ALRVerificationReport buildVerificationReport() {
        return ALRVerificationReport.builder()
                .verificationData(ALRVerificationData.builder().build())
                .build();
    }

    private ALRRequestPayload buildRequestPayload(ALR alr, ALRVerificationReport report, boolean performed) {
        return ALRRequestPayload.builder()
                .payloadType(RequestPayloadType.ALR_REQUEST_PAYLOAD)
                .alr(alr)
                .verificationPerformed(performed)
                .verificationReport(report)
                .verificationAttachments(Map.of(verificationAttachmentId, "test"))
                .build();
    }

    private ALRApplicationSubmitRequestTaskPayload buildTaskPayload(ALR alr, boolean performed, boolean withSections) {
        var builder = ALRApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.ALR_SUBMIT_PAYLOAD)
                .alr(alr)
                .alrAttachments(Map.of(attachmentId, "Test"))
                .verificationPerformed(performed);

        if (withSections)
            builder.alrSectionsCompleted(Map.of("test", true));

        return builder.build();
    }

    private Request buildRequest(ALRRequestPayload payload) {
        return Request.builder()
                .accountId(accountId)
                .payload(payload)
                .metadata(ALRRequestMetaData.builder().type(RequestMetadataType.ALR).build())
                .build();
    }

    private RequestTask buildRequestTask(Request request, ALRApplicationSubmitRequestTaskPayload taskPayload) {
        return RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();
    }
}
