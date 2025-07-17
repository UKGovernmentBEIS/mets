package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.handler;

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

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service.RequestEmpUkEtsReviewService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.validation.EmpIssuanceUkEtsReviewReturnForAmendsValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;


@ExtendWith(MockitoExtension.class)
class EmpIssuanceUkEtsReviewReturnForAmendsHandlerTest {

	@InjectMocks
    private EmpIssuanceUkEtsReviewReturnForAmendsHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestEmpUkEtsReviewService service;

    @Mock
    private EmpIssuanceUkEtsReviewReturnForAmendsValidatorService validatorService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void doProcess() {
        long taskId = 1L;
        RequestTaskActionEmptyPayload emptyPayload = RequestTaskActionEmptyPayload.builder().payloadType(RequestTaskActionPayloadType.EMPTY_PAYLOAD).build();
        String userId = "userId";
        AppUser appUser = AppUser.builder().userId(userId).build();

        String processTaskId = "processTaskId";

        EmpIssuanceReviewDecision reviewAmendDecision = EmpIssuanceReviewDecision.builder()
            .type(EmpReviewDecisionType.OPERATOR_AMENDS_NEEDED)
            .details(ChangesRequiredDecisionDetails.builder()
                .requiredChanges(Collections.singletonList(new ReviewDecisionRequiredChange("changesRequired", Collections.emptySet()))).build())
            .build();
        EmpIssuanceReviewDecision reviewNoAmendDecision = EmpIssuanceReviewDecision.builder()
            .type(EmpReviewDecisionType.ACCEPTED).build();
        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload payload =
        		EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(Map.of(
                	EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, reviewAmendDecision,
                	EmpUkEtsReviewGroup.ADDITIONAL_DOCUMENTS, reviewNoAmendDecision
                )).build();

        Request request = Request.builder()
            .id("2")
            .payload(EmpIssuanceUkEtsRequestPayload.builder().build())
            .build();
        RequestTask requestTask = RequestTask.builder()
            .id(taskId)
            .processTaskId(processTaskId)
            .payload(payload)
            .request(request)
            .build();

        EmpIssuanceUkEtsApplicationReturnedForAmendsRequestActionPayload actionPayload =
        		EmpIssuanceUkEtsApplicationReturnedForAmendsRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD)
                .reviewGroupDecisions(Map.of(EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, reviewAmendDecision))
                .build();

        EmpIssuanceUkEtsRequestPayload newEmpIssuanceRequestPayload = EmpIssuanceUkEtsRequestPayload.builder()
            .reviewGroupDecisions(Map.of(EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, reviewAmendDecision))
            .build();
        Request newRequest = Request.builder()
            .id("2")
            .payload(newEmpIssuanceRequestPayload)
            .build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);

        // Invoke
        handler.process(taskId, RequestTaskActionType.EMP_ISSUANCE_UKETS_REVIEW_RETURN_FOR_AMENDS, appUser, emptyPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(validatorService, times(1)).validate(payload);
        verify(service, times(1)).saveRequestReturnForAmends(requestTask, appUser);
        verify(requestService, times(1))
            .addActionToRequest(newRequest, actionPayload, RequestActionType.EMP_ISSUANCE_UKETS_APPLICATION_RETURNED_FOR_AMENDS, userId);
        verify(workflowService, times(1))
            .completeTask(processTaskId, Map.of(BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.AMENDS_NEEDED.name()));
    }

    @Test
    void doProcess_not_valid() {
        long taskId = 1L;
        RequestTaskActionEmptyPayload emptyPayload = RequestTaskActionEmptyPayload.builder().payloadType(RequestTaskActionPayloadType.EMPTY_PAYLOAD).build();
        String userId = "userId";
        AppUser appUser = AppUser.builder().userId(userId).build();

        String processTaskId = "processTaskId";

        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload payload =
        		EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(Map.of(
                		EmpUkEtsReviewGroup.ADDITIONAL_DOCUMENTS, EmpIssuanceReviewDecision
                		.builder()
                		.type(EmpReviewDecisionType.ACCEPTED)
                		.build()
                )).build();

        RequestTask requestTask = RequestTask.builder()
            .id(taskId)
            .processTaskId(processTaskId)
            .payload(payload)
            .request(Request.builder().build())
            .build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        doThrow(new BusinessException((MetsErrorCode.INVALID_EMP_REVIEW))).when(validatorService)
            .validate(payload);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
            () -> handler.process(taskId, RequestTaskActionType.EMP_ISSUANCE_UKETS_REVIEW_RETURN_FOR_AMENDS, appUser, emptyPayload));

        // Verify
        assertEquals(MetsErrorCode.INVALID_EMP_REVIEW, businessException.getErrorCode());
        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(validatorService, times(1)).validate(payload);
        verify(service, never()).saveRequestReturnForAmends(any(), any());
        verify(requestService, never()).addActionToRequest(any(), any(), any(), anyString());
        verify(workflowService, never()).completeTask(anyString(), anyMap());
    }
}
