package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.handler;

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

import java.util.Collections;
import java.util.Map;

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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.domain.EmpVariationUkEtsApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.service.EmpVariationUkEtsReviewService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.validation.EmpVariationUkEtsReviewReturnForAmendsValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsReviewReturnForAmendsHandlerTest {

	@InjectMocks
    private EmpVariationUkEtsReviewReturnForAmendsHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestService requestService;

    @Mock
    private EmpVariationUkEtsReviewService service;

    @Mock
    private EmpVariationUkEtsReviewReturnForAmendsValidatorService validatorService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void doProcess() {
        long taskId = 1L;
        RequestTaskActionEmptyPayload emptyPayload = RequestTaskActionEmptyPayload
        		.builder()
        		.payloadType(RequestTaskActionPayloadType.EMPTY_PAYLOAD)
        		.build();
        String userId = "userId";
        PmrvUser pmrvUser = PmrvUser.builder().userId(userId).build();

        String processTaskId = "processTaskId";

        EmpVariationReviewDecision reviewAmendDecision = EmpVariationReviewDecision.builder()
            .type(EmpVariationReviewDecisionType.OPERATOR_AMENDS_NEEDED)
            .details(ChangesRequiredDecisionDetails.builder()
                .requiredChanges(Collections.singletonList(new ReviewDecisionRequiredChange("changesRequired", Collections.emptySet()))).build())
            .build();
        EmpVariationReviewDecision reviewNoAmendDecision = EmpVariationReviewDecision.builder()
            .type(EmpVariationReviewDecisionType.ACCEPTED).build();
        EmpVariationUkEtsApplicationReviewRequestTaskPayload payload =
        		EmpVariationUkEtsApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(Map.of(
                	EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, reviewAmendDecision,
                	EmpUkEtsReviewGroup.ADDITIONAL_DOCUMENTS, reviewNoAmendDecision
                )).build();

        Request request = Request.builder()
            .id("2")
            .payload(EmpVariationUkEtsRequestPayload.builder().build())
            .build();
        RequestTask requestTask = RequestTask.builder()
            .id(taskId)
            .processTaskId(processTaskId)
            .payload(payload)
            .request(request)
            .build();

        EmpVariationUkEtsApplicationReturnedForAmendsRequestActionPayload actionPayload =
        		EmpVariationUkEtsApplicationReturnedForAmendsRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.EMP_VARIATION_UKETS_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD)
                .reviewGroupDecisions(Map.of(EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, reviewAmendDecision))
                .build();

        EmpVariationUkEtsRequestPayload newEmpVariationRequestPayload = EmpVariationUkEtsRequestPayload.builder()
            .reviewGroupDecisions(Map.of(EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, reviewAmendDecision))
            .build();
        Request newRequest = Request.builder()
            .id("2")
            .payload(newEmpVariationRequestPayload)
            .build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);

        // Invoke
        handler.process(taskId, RequestTaskActionType.EMP_VARIATION_UKETS_REVIEW_RETURN_FOR_AMENDS, pmrvUser, emptyPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(validatorService, times(1)).validate(payload);
        verify(service, times(1)).saveRequestReturnForAmends(requestTask, pmrvUser);
        verify(requestService, times(1))
            .addActionToRequest(newRequest, actionPayload, RequestActionType.EMP_VARIATION_UKETS_APPLICATION_RETURNED_FOR_AMENDS, userId);
        verify(workflowService, times(1))
            .completeTask(processTaskId, Map.of(BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.AMENDS_NEEDED.name()));
    }

    @Test
    void doProcess_not_valid() {
        long taskId = 1L;
        RequestTaskActionEmptyPayload emptyPayload = RequestTaskActionEmptyPayload
        		.builder()
        		.payloadType(RequestTaskActionPayloadType.EMPTY_PAYLOAD)
        		.build();
        String userId = "userId";
        PmrvUser pmrvUser = PmrvUser.builder().userId(userId).build();

        String processTaskId = "processTaskId";

        EmpVariationUkEtsApplicationReviewRequestTaskPayload payload =
        		EmpVariationUkEtsApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(Map.of(
                		EmpUkEtsReviewGroup.ADDITIONAL_DOCUMENTS, EmpVariationReviewDecision
                		.builder()
                		.type(EmpVariationReviewDecisionType.ACCEPTED)
                		.build()
                )).build();

        RequestTask requestTask = RequestTask.builder()
            .id(taskId)
            .processTaskId(processTaskId)
            .payload(payload)
            .request(Request.builder().build())
            .build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        doThrow(new BusinessException((ErrorCode.INVALID_EMP_VARIATION_REVIEW))).when(validatorService)
            .validate(payload);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
            () -> handler.process(taskId, RequestTaskActionType.EMP_VARIATION_UKETS_REVIEW_RETURN_FOR_AMENDS, pmrvUser, emptyPayload));

        // Verify
        assertEquals(ErrorCode.INVALID_EMP_VARIATION_REVIEW, businessException.getErrorCode());
        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(validatorService, times(1)).validate(payload);
        verify(service, never()).saveRequestReturnForAmends(any(), any());
        verify(requestService, never()).addActionToRequest(any(), any(), any(), anyString());
        verify(workflowService, never()).completeTask(anyString(), anyMap());
    }
}
