package uk.gov.pmrv.api.workflow.request.flow.installation.vir.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementFollowUpResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorReviewResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.service.VirRespondToRegulatorCommentsNotificationService;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.validation.VirRespondToRegulatorCommentsValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationRespondToRegulatorCommentsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationRespondedToRegulatorCommentsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirSaveRespondToRegulatorCommentsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirSubmitRespondToRegulatorCommentsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.VirVerificationData;

@ExtendWith(MockitoExtension.class)
class VirRespondToRegulatorCommentsServiceTest {

    @InjectMocks
    private VirRespondToRegulatorCommentsService virRespondToRegulatorCommentsService;

    @Mock
    private VirRespondToRegulatorCommentsValidator virRespondToRegulatorCommentsValidator;

    @Mock
    private RequestService requestService;

    @Mock
    private VirRespondToRegulatorCommentsNotificationService virRespondToRegulatorCommentsNotificationService;

    @Test
    void applySaveAction() {
        final String reference = "A1";
        final OperatorImprovementFollowUpResponse operatorImprovementFollowUpResponse = OperatorImprovementFollowUpResponse.builder()
                .improvementCompleted(false)
                .reason("Reason")
                .build();
        final Map<String, RegulatorImprovementResponse> regulatorImprovementResponses = Map.of(
                reference, RegulatorImprovementResponse.builder().build()
        );

        final RequestTask requestTask = RequestTask.builder()
                .payload(VirApplicationRespondToRegulatorCommentsRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.VIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD)
                        .regulatorImprovementResponses(regulatorImprovementResponses)
                        .build())
                .build();
        final VirSaveRespondToRegulatorCommentsRequestTaskActionPayload actionPayload =
                VirSaveRespondToRegulatorCommentsRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.VIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD)
                        .reference(reference)
                        .operatorImprovementFollowUpResponse(operatorImprovementFollowUpResponse)
                        .virRespondToRegulatorCommentsSectionsCompleted(Map.of("A1", true))
                        .build();

        // Invoke
        virRespondToRegulatorCommentsService.applySaveAction(actionPayload, requestTask);

        // Verify
        verify(virRespondToRegulatorCommentsValidator, times(1))
                .validateReferenceOnRegulator(reference, regulatorImprovementResponses);

        assertThat(requestTask.getPayload()).isInstanceOf(VirApplicationRespondToRegulatorCommentsRequestTaskPayload.class);

        VirApplicationRespondToRegulatorCommentsRequestTaskPayload payloadSaved =
                (VirApplicationRespondToRegulatorCommentsRequestTaskPayload) requestTask.getPayload();
        assertThat(payloadSaved.getOperatorImprovementFollowUpResponses())
                .isEqualTo(Map.of(reference, operatorImprovementFollowUpResponse));
        assertThat(payloadSaved.getVirRespondToRegulatorCommentsSectionsCompleted())
                .isEqualTo(Map.of("A1", true));
    }

    @Test
    void applySubmitAction() {
        final String reference = "A1";
        final String userId = "userId";
        final Year year = Year.now();
        final long accountId = 1L;
        final String regulator = "regulator";

        final AppUser appUser = AppUser.builder().userId(userId).build();
        final VirSubmitRespondToRegulatorCommentsRequestTaskActionPayload actionPayload =
                VirSubmitRespondToRegulatorCommentsRequestTaskActionPayload.builder()
                        .reference(reference)
                        .virRespondToRegulatorCommentsSectionsCompleted(Map.of("A2", true))
                        .build();

        Map<String, RegulatorImprovementResponse> regulatorImprovementResponses = new HashMap<>();
        regulatorImprovementResponses.put(reference, RegulatorImprovementResponse.builder().build());
        regulatorImprovementResponses.put("A2", RegulatorImprovementResponse.builder().build());

        OperatorImprovementFollowUpResponse operatorImprovementFollowUpResponse = OperatorImprovementFollowUpResponse.builder()
                .improvementCompleted(false)
                .reason("Reason")
                .build();
        Map<String, OperatorImprovementFollowUpResponse> operatorImprovementFollowUpResponses = new HashMap<>();
        operatorImprovementFollowUpResponses.put(reference, operatorImprovementFollowUpResponse);
        operatorImprovementFollowUpResponses.put("A2", OperatorImprovementFollowUpResponse.builder().build());

        Map<String, OperatorImprovementResponse> operatorImprovementResponses = Map.of(
                reference, OperatorImprovementResponse.builder().build(),
                "A2", OperatorImprovementResponse.builder().build()
        );

        UncorrectedItem uncorrectedItem = UncorrectedItem.builder()
                .explanation("Explanation")
                .reference(reference)
                .materialEffect(true)
                .build();
        VirVerificationData verificationData = VirVerificationData.builder()
                .uncorrectedNonConformities(Map.of(
                        reference, uncorrectedItem,
                        "A2", UncorrectedItem.builder().build()
                ))
                .build();

        Map<UUID, String> virAttachments = Map.of(UUID.randomUUID(), "file");

        final Request request = Request.builder()
                .accountId(accountId)
                .metadata(VirRequestMetadata.builder().year(year).build())
                .payload(VirRequestPayload.builder()
                        .payloadType(RequestPayloadType.VIR_REQUEST_PAYLOAD)
                        .regulatorReviewer(regulator)
                        .verificationData(verificationData)
                        .operatorImprovementResponses(operatorImprovementResponses)
                        .regulatorReviewResponse(RegulatorReviewResponse.builder()
                                .regulatorImprovementResponses(Map.of(
                                        reference, RegulatorImprovementResponse.builder().build(),
                                        "A2", RegulatorImprovementResponse.builder().build()
                                ))
                                .build())
                        .virAttachments(virAttachments)
                        .build())
                .build();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(VirApplicationRespondToRegulatorCommentsRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.VIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD)
                        .regulatorImprovementResponses(regulatorImprovementResponses)
                        .operatorImprovementFollowUpResponses(operatorImprovementFollowUpResponses)
                        .build())
                .build();

        final VirApplicationRespondedToRegulatorCommentsRequestActionPayload respondedActionPayload =
                VirApplicationRespondedToRegulatorCommentsRequestActionPayload.builder()
                        .verifierUncorrectedItem(uncorrectedItem)
                        .virAttachments(virAttachments)
                        .reportingYear(year)
                        .operatorImprovementFollowUpResponse(operatorImprovementFollowUpResponse)
                        .payloadType(RequestActionPayloadType.VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS_PAYLOAD)
                        .operatorImprovementResponse(operatorImprovementResponses.get(reference))
                        .regulatorImprovementResponse(regulatorImprovementResponses.get(reference))
                        .build();

        // Invoke
        virRespondToRegulatorCommentsService.applySubmitAction(actionPayload, requestTask, appUser);

        // Verify
        verify(virRespondToRegulatorCommentsValidator, times(1))
                .validate(reference, operatorImprovementFollowUpResponses, regulatorImprovementResponses);
        verify(requestService, times(1))
                .addActionToRequest(request, respondedActionPayload, RequestActionType.VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS, userId);
        verify(virRespondToRegulatorCommentsNotificationService, times(1))
                .sendSubmittedResponseToRegulatorCommentsNotificationToRegulator(request);

        assertThat(requestTask.getPayload()).isInstanceOf(VirApplicationRespondToRegulatorCommentsRequestTaskPayload.class);

        VirApplicationRespondToRegulatorCommentsRequestTaskPayload payloadSaved =
                (VirApplicationRespondToRegulatorCommentsRequestTaskPayload) requestTask.getPayload();
        assertThat(payloadSaved.getOperatorImprovementFollowUpResponses()).isEqualTo(Map.of("A2", OperatorImprovementFollowUpResponse.builder().build()));
        assertThat(payloadSaved.getRegulatorImprovementResponses()).isEqualTo(Map.of("A2", RegulatorImprovementResponse.builder().build()));
        assertThat(payloadSaved.getVirRespondToRegulatorCommentsSectionsCompleted()).isEqualTo(Map.of("A2", true));
    }
}
