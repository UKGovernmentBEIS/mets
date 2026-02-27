package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationSubmitToVerifierRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ContinueApplicationForFreeAllocationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2Files;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2GuardQuestions;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.validation.BDRS2ValidationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BDRS2SubmitServiceTest {

    @InjectMocks
    private BDRS2SubmitService service;

    @Mock
    private RequestService requestService;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Mock
    private BDRS2ValidationService bdrs2ValidationService;

    @Test
    void applySaveAction() {
        final Map<String, Boolean> expectedSectionsCompleted = new HashMap<>();
        expectedSectionsCompleted.put("baseline", false);

        final BDRS2GuardQuestions guardQuestions = BDRS2GuardQuestions.builder()
                .continueApplicationForFreeAllocationType(BDRS2ContinueApplicationForFreeAllocationType.CONTINUE_AS_MAIN_SCHEME_PARTICIPANT)
                .covidAdjustments(Boolean.FALSE)
                .inEiteSector(Boolean.TRUE)
                .build();

        final BDRS2 bdrs2 = BDRS2.builder()
                .bdrs2guardQuestions(guardQuestions)
                .build();

        final BDRS2ApplicationSubmitRequestTaskPayload expectedTaskPayload =
                BDRS2ApplicationSubmitRequestTaskPayload
                        .builder()
                        .build();

        final RequestTask requestTask = RequestTask.builder()
                .payload(expectedTaskPayload)
                .build();

        final BDRS2ApplicationSaveRequestTaskActionPayload expectedTaskActionPayload =
                BDRS2ApplicationSaveRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.BDRS2_APPLICATION_SAVE_PAYLOAD)
                        .bdrs2(bdrs2)
                        .bdrs2SectionsCompleted(expectedSectionsCompleted)
                        .build();

        service.applySaveAction(requestTask, expectedTaskActionPayload);

        assertEquals(expectedTaskPayload.getBdrs2(), expectedTaskActionPayload.getBdrs2());
        assertEquals(expectedTaskPayload.getBdrs2SectionsCompleted(), expectedTaskActionPayload.getBdrs2SectionsCompleted());
    }

    @Test
    void submitToRegulator() {
        final String userId = "userId";
        final Long accountId = 1L;
        final AppUser user = AppUser.builder().userId(userId).build();
        final Map<String, Boolean> sectionsCompleted = Map.of("baseline", true);
        final UUID fileUuid = UUID.randomUUID();
        final Map<UUID, String> attachments = new HashMap<>();
        attachments.put(fileUuid, "BDRS2-00001-2025-v1-uploaded by Operator-Test.pdf");

        final BDRS2Files bdrs2Files = BDRS2Files.builder()
                .file(fileUuid)
                .build();

        final BDRS2GuardQuestions guardQuestions = BDRS2GuardQuestions.builder()
                .continueApplicationForFreeAllocationType(BDRS2ContinueApplicationForFreeAllocationType.CONTINUE_AS_MAIN_SCHEME_PARTICIPANT)
                .covidAdjustments(Boolean.FALSE)
                .inEiteSector(Boolean.TRUE)
                .build();

        final BDRS2 bdrs2 = BDRS2.builder()
                .bdrs2guardQuestions(guardQuestions)
                .bdrs2Files(bdrs2Files)
                .build();

        final BDRS2RequestPayload requestPayload = BDRS2RequestPayload.builder().bdrs2(bdrs2).build();
        final Request request = Request.builder()
                .id("requestId")
                .accountId(accountId)
                .payload(requestPayload)
                .build();

        final BDRS2ApplicationSubmitRequestTaskPayload taskPayload = BDRS2ApplicationSubmitRequestTaskPayload.builder()
                .bdrs2(bdrs2)
                .bdrs2SectionsCompleted(sectionsCompleted)
                .bdrs2Attachments(attachments)
                .verificationPerformed(false)
                .build();

        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        final InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder().build();
        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
                .thenReturn(installationOperatorDetails);

        service.submitToRegulator(requestTask, user);

        assertEquals(bdrs2, requestPayload.getBdrs2());
        assertEquals(sectionsCompleted, requestPayload.getBdrs2SectionsCompleted());
        assertEquals(attachments, requestPayload.getBdrs2Attachments());
        assertFalse(requestPayload.isVerificationPerformed());

        verify(bdrs2ValidationService, times(1)).validateBDRS2(bdrs2);
        verify(bdrs2ValidationService, times(1)).validateBDRS2FileName(attachments.get(fileUuid));
        verify(requestService, times(1)).addActionToRequest(
                eq(request),
                any(BDRS2ApplicationSubmittedRequestActionPayload.class),
                eq(RequestActionType.BDRS2_APPLICATION_SENT_TO_REGULATOR),
                eq(userId));
        verify(installationOperatorDetailsQueryService, times(1))
                .getInstallationOperatorDetails(accountId);
    }

    @Test
    void submitToVerifier() {
        final String userId = "userId";
        final Long accountId = 1L;
        final AppUser user = AppUser.builder().userId(userId).build();
        final Map<String, Boolean> sectionsCompleted = Map.of("baseline", true);
        final UUID fileUuid = UUID.randomUUID();
        final Map<UUID, String> attachments = new HashMap<>();
        attachments.put(fileUuid, "BDRS2-00001-2025-v1-uploaded by Operator-Test.pdf");
        final Map<String, List<Boolean>> verificationSectionsCompleted = Map.of("verification", List.of(true));

        final BDRS2Files bdrs2Files = BDRS2Files.builder()
                .file(fileUuid)
                .build();

        final BDRS2GuardQuestions guardQuestions = BDRS2GuardQuestions.builder()
                .continueApplicationForFreeAllocationType(BDRS2ContinueApplicationForFreeAllocationType.CONTINUE_AS_MAIN_SCHEME_PARTICIPANT)
                .covidAdjustments(Boolean.FALSE)
                .inEiteSector(Boolean.TRUE)
                .build();

        final BDRS2 bdrs2 = BDRS2.builder()
                .bdrs2guardQuestions(guardQuestions)
                .bdrs2Files(bdrs2Files)
                .build();

        final BDRS2RequestPayload requestPayload = BDRS2RequestPayload.builder().bdrs2(bdrs2).build();
        final Request request = Request.builder()
                .id("requestId")
                .accountId(accountId)
                .payload(requestPayload)
                .build();

        final BDRS2ApplicationSubmitRequestTaskPayload taskPayload = BDRS2ApplicationSubmitRequestTaskPayload.builder()
                .bdrs2(bdrs2)
                .bdrs2SectionsCompleted(sectionsCompleted)
                .bdrs2Attachments(attachments)
                .verificationPerformed(false)
                .build();

        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        final BDRS2ApplicationSubmitToVerifierRequestTaskActionPayload actionPayload =
                BDRS2ApplicationSubmitToVerifierRequestTaskActionPayload.builder()
                        .verificationSectionsCompleted(verificationSectionsCompleted)
                        .build();

        final InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder().build();
        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
                .thenReturn(installationOperatorDetails);

        service.submitToVerifier(actionPayload, requestTask, user);

        assertEquals(bdrs2, requestPayload.getBdrs2());
        assertEquals(sectionsCompleted, requestPayload.getBdrs2SectionsCompleted());
        assertEquals(attachments, requestPayload.getBdrs2Attachments());
        assertEquals(verificationSectionsCompleted, requestPayload.getVerificationSectionsCompleted());

        verify(bdrs2ValidationService, times(1)).validateBDRS2(bdrs2);
        verify(bdrs2ValidationService, times(1)).validateBDRS2FileName(attachments.get(fileUuid));
        verify(requestService, times(1)).addActionToRequest(
                eq(request),
                any(BDRS2ApplicationSubmittedRequestActionPayload.class),
                eq(RequestActionType.BDRS2_APPLICATION_SENT_TO_VERIFIER),
                eq(userId));
        verify(installationOperatorDetailsQueryService, times(1))
                .getInstallationOperatorDetails(accountId);
    }

    @Test
    void submitBDRS2() {
        final String userId = "userId";
        final Long accountId = 1L;
        final AppUser user = AppUser.builder().userId(userId).build();
        final Map<String, Boolean> sectionsCompleted = Map.of("baseline", true);
        final UUID fileUuid = UUID.randomUUID();
        final Map<UUID, String> attachments = new HashMap<>();
        attachments.put(fileUuid, "BDRS2-00001-2025-v1-uploaded by Operator-Test.pdf");

        final BDRS2GuardQuestions guardQuestions = BDRS2GuardQuestions.builder()
                .continueApplicationForFreeAllocationType(BDRS2ContinueApplicationForFreeAllocationType.CONTINUE_AS_MAIN_SCHEME_PARTICIPANT)
                .covidAdjustments(Boolean.FALSE)
                .inEiteSector(Boolean.TRUE)
                .build();

        final BDRS2 bdrs2 = BDRS2.builder()
                .bdrs2guardQuestions(guardQuestions)
                .build();

        final BDRS2RequestPayload requestPayload = BDRS2RequestPayload.builder().bdrs2(BDRS2.builder().build()).build();
        final Request request = Request.builder()
                .id("requestId")
                .accountId(accountId)
                .payload(requestPayload)
                .build();

        final BDRS2ApplicationSubmitRequestTaskPayload taskPayload = BDRS2ApplicationSubmitRequestTaskPayload.builder()
                .bdrs2(bdrs2)
                .bdrs2SectionsCompleted(sectionsCompleted)
                .bdrs2Attachments(attachments)
                .verificationPerformed(true)
                .build();

        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        final RequestActionPayload actionPayload = BDRS2ApplicationSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.BDRS2_APPLICATION_AMENDS_SUBMITTED_PAYLOAD)
                .bdrs2(bdrs2)
                .build();

        service.submitBDRS2(requestPayload, requestTask, user, RequestActionType.BDRS2_APPLICATION_AMENDS_SENT_TO_VERIFIER, actionPayload, sectionsCompleted);

        assertEquals(bdrs2, requestPayload.getBdrs2());
        assertEquals(sectionsCompleted, requestPayload.getBdrs2SectionsCompleted());
        assertEquals(attachments, requestPayload.getBdrs2Attachments());
        assertEquals(true, requestPayload.isVerificationPerformed());

        verify(requestService, times(1)).addActionToRequest(
                eq(request),
                eq(actionPayload),
                eq(RequestActionType.BDRS2_APPLICATION_AMENDS_SENT_TO_VERIFIER),
                eq(userId));
    }

    @Test
    void createApplicationSubmittedRequestActionPayload() {
        final Long accountId = 1L;
        final UUID fileUuid = UUID.randomUUID();
        final Map<UUID, String> attachments = new HashMap<>();
        attachments.put(fileUuid, "BDRS2-00001-2025-v1-uploaded by Operator-Test.pdf");

        final BDRS2 bdrs2 = BDRS2.builder().build();

        final BDRS2RequestPayload requestPayload = BDRS2RequestPayload.builder()
                .verificationReport(null)
                .build();
        final Request request = Request.builder()
                .id("requestId")
                .accountId(accountId)
                .payload(requestPayload)
                .build();

        final BDRS2ApplicationSubmitRequestTaskPayload taskPayload = BDRS2ApplicationSubmitRequestTaskPayload.builder()
                .bdrs2(bdrs2)
                .bdrs2Attachments(attachments)
                .verificationPerformed(false)
                .build();

        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        final InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder().build();
        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
                .thenReturn(installationOperatorDetails);

        BDRS2ApplicationSubmittedRequestActionPayload result = service.createApplicationSubmittedRequestActionPayload(
                requestTask, taskPayload, requestPayload, RequestActionPayloadType.BDRS2_APPLICATION_AMENDS_SUBMITTED_PAYLOAD);

        assertEquals(RequestActionPayloadType.BDRS2_APPLICATION_AMENDS_SUBMITTED_PAYLOAD, result.getPayloadType());
        assertEquals(bdrs2, result.getBdrs2());
        assertEquals(attachments, result.getBdrs2Attachments());
        assertEquals(installationOperatorDetails, result.getInstallationOperatorDetails());

        verify(installationOperatorDetailsQueryService, times(1))
                .getInstallationOperatorDetails(accountId);
    }
}
