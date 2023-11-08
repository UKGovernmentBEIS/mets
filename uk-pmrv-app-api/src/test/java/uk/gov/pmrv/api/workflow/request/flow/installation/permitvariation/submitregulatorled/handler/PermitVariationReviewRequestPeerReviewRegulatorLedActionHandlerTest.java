package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType.PERMIT_VARIATION_PEER_REVIEW_REQUESTED;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationDeemedWithdrawnDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.handler.PermitVariationReviewRequestPeerReviewRegulatorLedActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.service.PermitVariationRegulatorLedService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.validation.PermitVariationReviewRequestPeerReviewRegulatorLedValidator;

@ExtendWith(MockitoExtension.class)
class PermitVariationReviewRequestPeerReviewRegulatorLedActionHandlerTest {

	@InjectMocks
    private PermitVariationReviewRequestPeerReviewRegulatorLedActionHandler cut;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PermitVariationRegulatorLedService permitVariationRegulatorLedService;
    
    @Mock
    private RequestService requestService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private PermitVariationReviewRequestPeerReviewRegulatorLedValidator permitVariationReviewRequestPeerReviewRegulatorLedValidator;


    @Test
    void process() {
        Long requestTaskId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        String selectedPeerReviewer = "selectedPeerReviewer";
        PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
            .peerReviewer(selectedPeerReviewer)
            .build();
        PermitVariationApplicationReviewRequestTaskPayload requestTaskPayload = PermitVariationApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
            .determination(PermitVariationDeemedWithdrawnDetermination.builder().reason("reason").type(DeterminationType.DEEMED_WITHDRAWN).build())
            .build();
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .request(Request.builder().id("2").build())
            .type(RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW)
            .payload(requestTaskPayload)
            .processTaskId("processTaskId")
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        cut.process(
            requestTaskId,
            RequestTaskActionType.PERMIT_VARIATION_REQUEST_PEER_REVIEW,
            pmrvUser,
            taskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(permitVariationReviewRequestPeerReviewRegulatorLedValidator, times(1))
            .validate(requestTask, taskActionPayload, pmrvUser);
        verify(permitVariationRegulatorLedService, times(1))
            .saveRequestPeerReviewActionRegulatorLed(requestTask, selectedPeerReviewer, "userId");
        verify(requestService, times(1))
            .addActionToRequest(requestTask.getRequest(), null, PERMIT_VARIATION_PEER_REVIEW_REQUESTED, pmrvUser.getUserId());
        verify(workflowService, times(1)).completeTask(
            requestTask.getProcessTaskId(),
            Map.of(
            		BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                    BpmnProcessConstants.PERMIT_VARIATION_SUBMIT_OUTCOME, PermitVariationSubmitOutcome.SUBMITTED,
                    BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.PEER_REVIEW_REQUIRED)
        );
    }

    @Test
    void getTypes() {
        assertThat(cut.getTypes()).containsExactly(RequestTaskActionType.PERMIT_VARIATION_REQUEST_PEER_REVIEW_REGULATOR_LED);
    }
}
