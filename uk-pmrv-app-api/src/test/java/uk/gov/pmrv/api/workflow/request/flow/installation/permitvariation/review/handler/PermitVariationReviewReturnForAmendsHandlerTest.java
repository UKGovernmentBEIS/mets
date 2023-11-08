package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.domain.PermitVariationApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.PermitVariationReviewReturnForAmendsValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.PermitVariationReviewService;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitVariationReviewReturnForAmendsHandlerTest {

    @InjectMocks
    private PermitVariationReviewReturnForAmendsHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestService requestService;

    @Mock
    private PermitVariationReviewService permitVariationReviewService;

    @Mock
    private PermitVariationReviewReturnForAmendsValidator permitReviewReturnForAmendsValidator;

    @Mock
    private WorkflowService workflowService;

    @Test
    void doProcess() {
        long taskId = 1L;
        RequestTaskActionEmptyPayload emptyPayload = RequestTaskActionEmptyPayload.builder().payloadType(RequestTaskActionPayloadType.EMPTY_PAYLOAD).build();
        String userId = "userId";
        PmrvUser pmrvUser = PmrvUser.builder().userId(userId).build();

        String processTaskId = "processTaskId";

        PermitVariationReviewDecision reviewAmendDecision = PermitVariationReviewDecision.builder()
            .type(ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
            .details(ChangesRequiredDecisionDetails.builder()
                .requiredChanges(Collections.singletonList(new ReviewDecisionRequiredChange("changesRequired", Collections.emptySet()))).build())
            .build();
        PermitVariationReviewDecision reviewNoAmendDecision = PermitVariationReviewDecision.builder()
            .type(ReviewDecisionType.ACCEPTED).build();
        PermitVariationApplicationReviewRequestTaskPayload payload =
            PermitVariationApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(Map.of(
                    PermitReviewGroup.CALCULATION_CO2, reviewAmendDecision,
                    PermitReviewGroup.FALLBACK, reviewNoAmendDecision
                )).build();

        Request request = Request.builder()
            .id("2")
            .payload(PermitVariationRequestPayload.builder().build())
            .build();
        RequestTask requestTask = RequestTask.builder()
            .id(taskId)
            .processTaskId(processTaskId)
            .payload(payload)
            .request(request)
            .build();

        PermitVariationApplicationReturnedForAmendsRequestActionPayload actionPayload =
            PermitVariationApplicationReturnedForAmendsRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.PERMIT_VARIATION_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD)
                .reviewGroupDecisions(Map.of(PermitReviewGroup.CALCULATION_CO2, reviewAmendDecision))
                .build();

        PermitVariationRequestPayload newPermitVariationRequestPayload = PermitVariationRequestPayload.builder()
            .reviewGroupDecisions(Map.of(PermitReviewGroup.CALCULATION_CO2, reviewAmendDecision))
            .build();
        Request newRequest = Request.builder()
            .id("2")
            .payload(newPermitVariationRequestPayload)
            .build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);

        // Invoke
        handler.process(taskId, RequestTaskActionType.PERMIT_VARIATION_REVIEW_RETURN_FOR_AMENDS, pmrvUser, emptyPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(permitReviewReturnForAmendsValidator, times(1)).validate(payload);
        verify(permitVariationReviewService, times(1)).saveRequestReturnForAmends(requestTask, pmrvUser);
        verify(requestService, times(1))
            .addActionToRequest(newRequest, actionPayload, RequestActionType.PERMIT_VARIATION_APPLICATION_RETURNED_FOR_AMENDS, userId);
        verify(workflowService, times(1))
            .completeTask(processTaskId, Map.of(BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.AMENDS_NEEDED.name()));
    }

    @Test
    void doProcess_not_valid() {
        long taskId = 1L;
        RequestTaskActionEmptyPayload emptyPayload = RequestTaskActionEmptyPayload.builder().payloadType(RequestTaskActionPayloadType.EMPTY_PAYLOAD).build();
        String userId = "userId";
        PmrvUser pmrvUser = PmrvUser.builder().userId(userId).build();

        String processTaskId = "processTaskId";

        PermitVariationApplicationReviewRequestTaskPayload payload =
            PermitVariationApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(Map.of(
                    PermitReviewGroup.CALCULATION_CO2, PermitVariationReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).build()
                )).build();

        RequestTask requestTask = RequestTask.builder()
            .id(taskId)
            .processTaskId(processTaskId)
            .payload(payload)
            .request(Request.builder().build())
            .build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        doThrow(new BusinessException((ErrorCode.INVALID_PERMIT_REVIEW))).when(permitReviewReturnForAmendsValidator)
            .validate(payload);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
            () -> handler.process(taskId, RequestTaskActionType.PERMIT_VARIATION_REVIEW_RETURN_FOR_AMENDS, pmrvUser, emptyPayload));

        // Verify
        assertEquals(ErrorCode.INVALID_PERMIT_REVIEW, businessException.getErrorCode());
        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(permitReviewReturnForAmendsValidator, times(1)).validate(payload);
        verify(permitVariationReviewService, never()).saveRequestReturnForAmends(any(), any());
        verify(requestService, never()).addActionToRequest(any(), any(), any(), anyString());
        verify(workflowService, never()).completeTask(anyString(), anyMap());
    }
}
