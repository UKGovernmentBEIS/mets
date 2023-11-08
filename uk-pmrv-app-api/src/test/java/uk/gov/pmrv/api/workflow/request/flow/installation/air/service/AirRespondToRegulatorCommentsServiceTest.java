package uk.gov.pmrv.api.workflow.request.flow.installation.air.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationRespondToRegulatorCommentsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationRespondedToRegulatorCommentsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovementCalculationCO2;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirSaveRespondToRegulatorCommentsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirSubmitRespondToRegulatorCommentsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.OperatorAirImprovementFollowUpResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.OperatorAirImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.OperatorAirImprovementYesResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.RegulatorAirImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.RegulatorAirReviewResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.validation.AirRespondToRegulatorCommentsValidator;

@ExtendWith(MockitoExtension.class)
class AirRespondToRegulatorCommentsServiceTest {

    @InjectMocks
    private AirRespondToRegulatorCommentsService service;

    @Mock
    private AirRespondToRegulatorCommentsValidator respondToRegulatorCommentsValidator;

    @Mock
    private RequestService requestService;

    @Mock
    private AirRespondToRegulatorCommentsNotificationService respondToRegulatorCommentsNotificationService;

    @Test
    void applySaveAction() {

        final Integer reference = 1;
        final OperatorAirImprovementFollowUpResponse operatorImprovementFollowUpResponse =
            OperatorAirImprovementFollowUpResponse.builder()
                .improvementCompleted(false)
                .reason("Reason")
                .build();
        final Map<Integer, RegulatorAirImprovementResponse> regulatorImprovementResponses = Map.of(
            reference, RegulatorAirImprovementResponse.builder().build()
        );

        final RequestTask requestTask = RequestTask.builder()
            .payload(AirApplicationRespondToRegulatorCommentsRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD)
                .regulatorImprovementResponses(regulatorImprovementResponses)
                .build())
            .build();
        final AirSaveRespondToRegulatorCommentsRequestTaskActionPayload actionPayload =
            AirSaveRespondToRegulatorCommentsRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.AIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD)
                .reference(reference)
                .operatorImprovementFollowUpResponse(operatorImprovementFollowUpResponse)
                .airRespondToRegulatorCommentsSectionsCompleted(Map.of(1, true))
                .build();

        // Invoke
        service.applySaveAction(actionPayload, requestTask);

        // Verify
        verify(respondToRegulatorCommentsValidator, times(1))
            .validateReferenceOnRegulator(reference, regulatorImprovementResponses);

        assertThat(requestTask.getPayload()).isInstanceOf(
            AirApplicationRespondToRegulatorCommentsRequestTaskPayload.class);

        AirApplicationRespondToRegulatorCommentsRequestTaskPayload payloadSaved =
            (AirApplicationRespondToRegulatorCommentsRequestTaskPayload) requestTask.getPayload();
        assertThat(payloadSaved.getOperatorImprovementFollowUpResponses())
            .isEqualTo(Map.of(reference, operatorImprovementFollowUpResponse));
        assertThat(payloadSaved.getAirRespondToRegulatorCommentsSectionsCompleted())
            .isEqualTo(Map.of(1, true));
    }

    @Test
    void applySubmitAction() {

        final Integer reference = 1;
        final String userId = "userId";
        final Year year = Year.now();
        final long accountId = 1L;
        final String regulator = "regulator";

        final PmrvUser pmrvUser = PmrvUser.builder().userId(userId).build();
        final AirSubmitRespondToRegulatorCommentsRequestTaskActionPayload actionPayload =
            AirSubmitRespondToRegulatorCommentsRequestTaskActionPayload.builder()
                .reference(reference)
                .airRespondToRegulatorCommentsSectionsCompleted(Map.of(2, true))
                .build();

        final  Map<Integer, RegulatorAirImprovementResponse> regulatorImprovementResponses = new HashMap<>();
        regulatorImprovementResponses.put(reference, RegulatorAirImprovementResponse.builder().build());
        regulatorImprovementResponses.put(2, RegulatorAirImprovementResponse.builder().build());

        final UUID file = UUID.randomUUID();
        final OperatorAirImprovementFollowUpResponse operatorImprovementFollowUpResponse =
            OperatorAirImprovementFollowUpResponse.builder()
                .improvementCompleted(false)
                .reason("Reason")
                .files(Set.of(file))
                .build();
        final  Map<Integer, OperatorAirImprovementFollowUpResponse> operatorImprovementFollowUpResponses = new HashMap<>();
        operatorImprovementFollowUpResponses.put(reference, operatorImprovementFollowUpResponse);
        operatorImprovementFollowUpResponses.put(2, OperatorAirImprovementFollowUpResponse.builder().build());

        final  Map<Integer, OperatorAirImprovementResponse> operatorImprovementResponses = Map.of(
            reference, OperatorAirImprovementYesResponse.builder().build(),
            2, OperatorAirImprovementYesResponse.builder().build()
        );

        final  Map<UUID, String> airAttachments = Map.of(file, "file");

        final Request request = Request.builder()
            .accountId(accountId)
            .metadata(AirRequestMetadata.builder().year(year).build())
            .payload(AirRequestPayload.builder()
                .payloadType(RequestPayloadType.AIR_REQUEST_PAYLOAD)
                .regulatorReviewer(regulator)
                .airImprovements(Map.of(1, AirImprovementCalculationCO2.builder().build()))
                .operatorImprovementResponses(operatorImprovementResponses)
                .regulatorReviewResponse(RegulatorAirReviewResponse.builder()
                    .regulatorImprovementResponses(Map.of(
                        reference, RegulatorAirImprovementResponse.builder().build(),
                        2, RegulatorAirImprovementResponse.builder().build()
                    ))
                    .build())
                .airAttachments(airAttachments)
                .build())
            .build();

        final RequestTask requestTask = RequestTask.builder()
            .request(request)
            .payload(AirApplicationRespondToRegulatorCommentsRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD)
                .operatorImprovementResponses(Map.of(1, OperatorAirImprovementYesResponse.builder().build()))
                .regulatorImprovementResponses(regulatorImprovementResponses)
                .operatorImprovementFollowUpResponses(operatorImprovementFollowUpResponses)
                .airAttachments(airAttachments)
                .build())
            .build();

        final AirApplicationRespondedToRegulatorCommentsRequestActionPayload respondedActionPayload =
            AirApplicationRespondedToRegulatorCommentsRequestActionPayload.builder()
                .operatorImprovementResponse(OperatorAirImprovementYesResponse.builder().build())
                .reference(1)
                .airAttachments(airAttachments)
                .reportingYear(year)
                .operatorImprovementFollowUpResponse(operatorImprovementFollowUpResponse)
                .payloadType(RequestActionPayloadType.AIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS_PAYLOAD)
                .operatorImprovementResponse(operatorImprovementResponses.get(reference))
                .regulatorImprovementResponse(regulatorImprovementResponses.get(reference))
                .build();

        // Invoke
        service.applySubmitAction(actionPayload, requestTask, pmrvUser);

        // Verify
        verify(respondToRegulatorCommentsValidator, times(1))
            .validate(reference, operatorImprovementFollowUpResponses, regulatorImprovementResponses);
        verify(requestService, times(1))
            .addActionToRequest(request, respondedActionPayload,
                RequestActionType.AIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS, userId);
        verify(respondToRegulatorCommentsNotificationService, times(1))
            .sendSubmittedResponseToRegulatorCommentsNotificationToRegulator(request);

        assertThat(requestTask.getPayload()).isInstanceOf(
            AirApplicationRespondToRegulatorCommentsRequestTaskPayload.class);

        AirApplicationRespondToRegulatorCommentsRequestTaskPayload payloadSaved =
            (AirApplicationRespondToRegulatorCommentsRequestTaskPayload) requestTask.getPayload();
        assertThat(payloadSaved.getRespondedItems()).isEqualTo(Set.of(1));
        assertThat(payloadSaved.getAirRespondToRegulatorCommentsSectionsCompleted()).isEqualTo(Map.of(2, true));
    }
}
