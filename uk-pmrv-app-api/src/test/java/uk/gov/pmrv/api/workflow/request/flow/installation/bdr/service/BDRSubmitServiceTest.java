package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service;


import org.junit.jupiter.api.Assertions;
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
import uk.gov.pmrv.api.reporting.domain.verification.OverallAssessmentType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDR;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationSubmitToVerifierRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRNotVerifiedOverallVerificationAssessment;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRVerificationData;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRVerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.validation.BDRValidationService;



import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BDRSubmitServiceTest {


    @InjectMocks
    private BDRSubmitService service;

    @Mock
    private BDRValidationService bdrValidationService;

    @Mock
    private RequestService requestService;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Test
    void applySaveAction() {

        final Map<String, Boolean> expectedSectionsCompleted = new HashMap<>();
        expectedSectionsCompleted.put("test",false);

        final BDRApplicationSubmitRequestTaskPayload expectedTaskPayload =
                BDRApplicationSubmitRequestTaskPayload
                        .builder()
                        .verificationPerformed(true)
                        .build();

        final RequestTask requestTask = RequestTask.builder()
                .payload(expectedTaskPayload)
                .build();

        final BDRApplicationSaveRequestTaskActionPayload expectedTaskActionPayload =
                BDRApplicationSaveRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.BDR_APPLICATION_SAVE_PAYLOAD)
                        .bdr(BDR.builder().isApplicationForFreeAllocation(true).hasMmp(false).build())
                        .bdrSectionsCompleted(expectedSectionsCompleted)
                        .build();

        service.applySaveAction(requestTask, expectedTaskActionPayload);

        assertEquals(expectedTaskPayload.getBdr(), expectedTaskActionPayload.getBdr());
        assertEquals(expectedTaskPayload.getBdrSectionsCompleted(), expectedTaskActionPayload.getBdrSectionsCompleted());
        assertThat(expectedTaskPayload.isVerificationPerformed()).isFalse();
    }

    @Test
    void submitToRegulator() {
        final long accountId = 1L;
        final AppUser appUser = AppUser.builder().userId("userId").build();
        final InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();

        BDR bdr = BDR.builder().build();

        BDRVerificationReport bdrVerificationReport = BDRVerificationReport.builder()
            .verificationData(BDRVerificationData
                        .builder()
                        .overallAssessment(BDRNotVerifiedOverallVerificationAssessment.builder().type(OverallAssessmentType.NOT_VERIFIED).build())
                        .build())
                .build();

        Request request = Request.builder()
                .accountId(accountId)
                .payload(BDRRequestPayload.builder()
                        .payloadType(RequestPayloadType.BDR_REQUEST_PAYLOAD)
                        .verificationReport(bdrVerificationReport)
                        .build())
                .metadata(BDRRequestMetadata.builder()
                        .type(RequestMetadataType.BDR)
                        .build())
                .build();

        BDRApplicationSubmitRequestTaskPayload taskPayload = BDRApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.BDR_APPLICATION_SUBMIT_PAYLOAD)
                .bdr(bdr)
                .verificationPerformed(true)
                .build();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        Request expectedRequest = Request.builder()
                .accountId(1L)
                .payload(BDRRequestPayload.builder()
                        .payloadType(RequestPayloadType.BDR_REQUEST_PAYLOAD)
                        .bdr(bdr)
                        .verificationPerformed(true)
                        .verificationReport(bdrVerificationReport)
                        .build())
                .metadata(BDRRequestMetadata.builder()
                        .type(RequestMetadataType.BDR)
                        .overallAssessmentType(bdrVerificationReport.getVerificationData().getOverallAssessment().getType())
                        .build())
                .build();

        BDRApplicationSubmittedRequestActionPayload actionPayload =
                BDRApplicationSubmittedRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.BDR_APPLICATION_SUBMITTED_PAYLOAD)
                        .bdr(bdr)
                        .verificationPerformed(true)
                        .verificationReport(bdrVerificationReport)
                        .installationOperatorDetails(installationOperatorDetails)
                        .build();


        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId()))
                .thenReturn(installationOperatorDetails);


        service.submitToRegulator(requestTask, appUser);

        verify(bdrValidationService, times(1)).validateBDR(bdr);
        verify(installationOperatorDetailsQueryService, times(1))
                .getInstallationOperatorDetails(request.getAccountId());
        verify(requestService, times(1)).addActionToRequest(requestTask.getRequest(),
                actionPayload, RequestActionType.BDR_APPLICATION_SENT_TO_REGULATOR, appUser.getUserId());

        assertThat(request.getPayload()).isEqualTo(expectedRequest.getPayload());
    }

    @Test
    void submitToVerifier() {
        final long accountId = 1L;
        final AppUser appUser = AppUser.builder().userId("userId").build();
        final InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();

        final BDRApplicationSubmitToVerifierRequestTaskActionPayload taskActionPayload =
                BDRApplicationSubmitToVerifierRequestTaskActionPayload.builder().build();

        BDR bdr = BDR.builder().build();

        BDRVerificationReport bdrVerificationReport = BDRVerificationReport.builder()
                .verificationData(BDRVerificationData.builder().build())
                .build();

        Request request = Request.builder()
                .accountId(accountId)
                .payload(BDRRequestPayload.builder()
                        .payloadType(RequestPayloadType.BDR_REQUEST_PAYLOAD)
                        .verificationReport(bdrVerificationReport)
                        .build())
                .metadata(BDRRequestMetadata.builder()
                        .type(RequestMetadataType.BDR)
                        .build())
                .build();


        BDRApplicationSubmitRequestTaskPayload taskPayload = BDRApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.BDR_APPLICATION_SUBMIT_PAYLOAD)
                .bdr(bdr)
                .verificationPerformed(true)
                .build();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        Request expectedRequest = Request.builder()
                .accountId(1L)
                .payload(BDRRequestPayload.builder()
                        .payloadType(RequestPayloadType.BDR_REQUEST_PAYLOAD)
                        .bdr(bdr)
                        .verificationPerformed(true)
                        .verificationReport(bdrVerificationReport)
                        .build())
                .metadata(BDRRequestMetadata.builder()
                        .type(RequestMetadataType.BDR)
                        .build())
                .build();

      BDRApplicationSubmittedRequestActionPayload actionPayload =
                BDRApplicationSubmittedRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.BDR_APPLICATION_SUBMITTED_PAYLOAD)
                        .bdr(bdr)
                        .verificationPerformed(true)
                        .verificationReport(bdrVerificationReport)
                        .installationOperatorDetails(installationOperatorDetails)
                        .build();


        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId()))
                .thenReturn(installationOperatorDetails);

        service.submitToVerifier(taskActionPayload, requestTask, appUser);

        verify(bdrValidationService, times(1)).validateBDR(bdr);
        verify(installationOperatorDetailsQueryService, times(1))
                .getInstallationOperatorDetails(request.getAccountId());
        verify(requestService, times(1)).addActionToRequest(requestTask.getRequest(),
                actionPayload, RequestActionType.BDR_APPLICATION_SENT_TO_VERIFIER, appUser.getUserId());

        assertThat(request.getPayload()).isEqualTo(expectedRequest.getPayload());
    }

    @Test
    void createApplicationSubmittedRequestActionPayload_requestTaskVerificationPerformed_setVerificationRelatedPayloadToRequestAction() {
        final long accountId = 1L;
        final UUID attachmentId = UUID.randomUUID();
        final UUID verificationAttachmentId = UUID.randomUUID();
        final InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();

        BDR bdr = BDR.builder().build();

        BDRVerificationReport bdrVerificationReport = BDRVerificationReport.builder()
                .verificationData(BDRVerificationData.builder().build())
                .build();

        BDRRequestPayload requestPayload = BDRRequestPayload.builder()
                        .payloadType(RequestPayloadType.BDR_REQUEST_PAYLOAD)
                        .verificationReport(bdrVerificationReport)
                        .verificationAttachments(Map.of(verificationAttachmentId,"test"))
                        .build();

        Request request = Request.builder()
                .accountId(accountId)
                .payload(requestPayload)
                .metadata(BDRRequestMetadata.builder()
                        .type(RequestMetadataType.BDR)
                        .build())
                .build();


        BDRApplicationSubmitRequestTaskPayload taskPayload = BDRApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.BDR_APPLICATION_SUBMIT_PAYLOAD)
                .bdr(bdr)
                .bdrAttachments(Map.of(attachmentId,"Test"))
                .verificationPerformed(true)
                .build();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        BDRApplicationSubmittedRequestActionPayload expectedRequestActionPayload = BDRApplicationSubmittedRequestActionPayload
                .builder()
                .bdr(bdr)
                .installationOperatorDetails(installationOperatorDetails)
                .bdrAttachments(Map.of(attachmentId,"Test"))
                .payloadType(RequestActionPayloadType.BDR_APPLICATION_SUBMITTED_PAYLOAD)
                .verificationAttachments(Map.of(verificationAttachmentId,"test"))
                .verificationReport(bdrVerificationReport)
                .verificationPerformed(true)
                .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId()))
                .thenReturn(installationOperatorDetails);

        BDRApplicationSubmittedRequestActionPayload actualRequestActionPayload =
                service.createApplicationSubmittedRequestActionPayload(requestTask, taskPayload, requestPayload, RequestActionPayloadType.BDR_APPLICATION_SUBMITTED_PAYLOAD);

        verify(installationOperatorDetailsQueryService, times(1))
                    .getInstallationOperatorDetails(request.getAccountId());
        assertThat(actualRequestActionPayload).isEqualTo(expectedRequestActionPayload);
        assertThat(actualRequestActionPayload.getVerificationReport()).isEqualTo(bdrVerificationReport);
        assertThat(actualRequestActionPayload.getVerificationAttachments()).isEqualTo(Map.of(verificationAttachmentId,"test"));
    }

    @Test
    void createApplicationSubmittedRequestActionPayload_requestTaskVerificationNotPerformed_doNotSetVerificationRelatedPayloadToRequestAction() {
        final long accountId = 1L;
        final UUID attachmentId = UUID.randomUUID();
        final UUID verificationAttachmentId = UUID.randomUUID();
        final InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();

        BDR bdr = BDR.builder().build();

        BDRVerificationReport bdrVerificationReport = BDRVerificationReport.builder()
                .verificationData(BDRVerificationData.builder().build())
                .build();

        BDRRequestPayload requestPayload = BDRRequestPayload.builder()
                        .payloadType(RequestPayloadType.BDR_REQUEST_PAYLOAD)
                        .verificationReport(bdrVerificationReport)
                        .verificationAttachments(Map.of(verificationAttachmentId,"test"))
                        .build();

        Request request = Request.builder()
                .accountId(accountId)
                .payload(requestPayload)
                .metadata(BDRRequestMetadata.builder()
                        .type(RequestMetadataType.BDR)
                        .build())
                .build();


        BDRApplicationSubmitRequestTaskPayload taskPayload = BDRApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.BDR_APPLICATION_SUBMIT_PAYLOAD)
                .bdr(bdr)
                .bdrAttachments(Map.of(attachmentId,"Test"))
                .verificationPerformed(false)
                .build();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        BDRApplicationSubmittedRequestActionPayload expectedRequestActionPayload = BDRApplicationSubmittedRequestActionPayload
                .builder()
                .bdr(bdr)
                .installationOperatorDetails(installationOperatorDetails)
                .bdrAttachments(Map.of(attachmentId,"Test"))
                .payloadType(RequestActionPayloadType.BDR_APPLICATION_SUBMITTED_PAYLOAD)
                .verificationPerformed(false)
                .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId()))
                .thenReturn(installationOperatorDetails);

        BDRApplicationSubmittedRequestActionPayload actualRequestActionPayload =
                service.createApplicationSubmittedRequestActionPayload(requestTask, taskPayload, requestPayload, RequestActionPayloadType.BDR_APPLICATION_SUBMITTED_PAYLOAD);

        verify(installationOperatorDetailsQueryService, times(1))
                    .getInstallationOperatorDetails(request.getAccountId());

        assertThat(actualRequestActionPayload).isEqualTo(expectedRequestActionPayload);
        assertThat(actualRequestActionPayload.getVerificationReport()).isNull();
        assertThat(actualRequestActionPayload.getVerificationAttachments()).isEmpty();
    }

    @Test
    void submitBDR() {
        final AppUser appUser = AppUser.builder().userId("userId").build();
        final long accountId = 1L;
        final UUID attachmentId = UUID.randomUUID();
        final UUID verificationAttachmentId = UUID.randomUUID();
        final InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();

        BDR bdr = BDR.builder().build();

        BDRVerificationReport bdrVerificationReport = BDRVerificationReport.builder()
                .verificationData(BDRVerificationData.builder().build())
                .build();

        BDRRequestPayload requestPayload = BDRRequestPayload.builder()
                        .payloadType(RequestPayloadType.BDR_REQUEST_PAYLOAD)
                        .verificationReport(bdrVerificationReport)
                        .verificationAttachments(Map.of(verificationAttachmentId,"test"))
                        .build();

        Request request = Request.builder()
                .accountId(accountId)
                .payload(requestPayload)
                .metadata(BDRRequestMetadata.builder()
                        .type(RequestMetadataType.BDR)
                        .build())
                .build();

        BDRApplicationSubmitRequestTaskPayload taskPayload = BDRApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.BDR_APPLICATION_SUBMIT_PAYLOAD)
                .bdr(bdr)
                .bdrAttachments(Map.of(attachmentId,"Test"))
                .verificationPerformed(true)
                .bdrSectionsCompleted(Map.of("test",true))
                .build();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        BDRApplicationSubmittedRequestActionPayload requestActionPayload = BDRApplicationSubmittedRequestActionPayload
                .builder()
                .bdr(bdr)
                .installationOperatorDetails(installationOperatorDetails)
                .bdrAttachments(Map.of(attachmentId,"Test"))
                .payloadType(RequestActionPayloadType.BDR_APPLICATION_SUBMITTED_PAYLOAD)
                .verificationAttachments(Map.of(verificationAttachmentId,"test"))
                .verificationReport(bdrVerificationReport)
                .verificationPerformed(true)
                .build();

        service.submitBDR(requestPayload,requestTask,appUser,RequestActionType.BDR_APPLICATION_SENT_TO_REGULATOR,requestActionPayload, taskPayload.getBdrSectionsCompleted());

        verify(requestService, times(1))
                    .addActionToRequest(request, requestActionPayload, RequestActionType.BDR_APPLICATION_SENT_TO_REGULATOR, appUser.getUserId());


        assertThat(requestPayload.getBdr()).isEqualTo(taskPayload.getBdr());
        assertThat(requestPayload.getBdrAttachments()).isEqualTo(taskPayload.getBdrAttachments());
        assertThat(requestPayload.getBdrSectionsCompleted()).isEqualTo(taskPayload.getBdrSectionsCompleted());
        assertThat(requestPayload.isVerificationPerformed()).isEqualTo(taskPayload.isVerificationPerformed());
    }

    @Test
    void requestPeerReview() {
        final String userId = "userId";
        final AppUser user = AppUser.builder().userId(userId).build();
        final InstallationOperatorDetails installationOperatorDetails = getInstallationOperatorDetails();
        final String selectedPeerReviewer = "selectedPeerReviewer";

        final Map<String, Boolean> sectionsCompleted = Map.of("subtask", true);
        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "test.png");

        BDR bdr = BDR.builder().build();

        Request request = Request.builder()
                .payload(BDRRequestPayload.builder()
                        .payloadType(RequestPayloadType.BDR_REQUEST_PAYLOAD)
                        .build())
                .build();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(BDRApplicationRegulatorReviewSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.BDR_APPLICATION_PEER_REVIEW_PAYLOAD)
                        .bdr(bdr)
                        .bdrSectionsCompleted(sectionsCompleted)
                        .bdrAttachments(attachments)
                        .build())
                .build();

        final BDRRequestPayload expectedPayload = BDRRequestPayload.builder()
                .payloadType(RequestPayloadType.BDR_REQUEST_PAYLOAD)
                .bdr(bdr)
                .bdrSectionsCompleted(sectionsCompleted)
                .bdrAttachments(attachments)
                .regulatorPeerReviewer(selectedPeerReviewer)
                .regulatorReviewer(user.getUserId())
                .build();

        service.requestPeerReview(requestTask, selectedPeerReviewer, user);

        BDRRequestPayload updatedPayload = (BDRRequestPayload) requestTask.getRequest().getPayload();
        Assertions.assertEquals(expectedPayload, updatedPayload);
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
}
