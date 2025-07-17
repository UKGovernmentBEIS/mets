package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.domain.EmpVariationCorsiaApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.service.EmpVariationCorsiaReviewService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.validation.EmpVariationCorsiaReviewReturnForAmendsValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaReviewReturnForAmendsHandlerTest {

    @InjectMocks
    private EmpVariationCorsiaReviewReturnForAmendsHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestService requestService;

    @Mock
    private EmpVariationCorsiaReviewService service;

    @Mock
    private EmpVariationCorsiaReviewReturnForAmendsValidatorService validatorService;

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
        AppUser appUser = AppUser.builder().userId(userId).build();

        String processTaskId = "processTaskId";

        EmpVariationReviewDecision reviewAmendDecision = EmpVariationReviewDecision.builder()
            .type(EmpVariationReviewDecisionType.OPERATOR_AMENDS_NEEDED)
            .details(ChangesRequiredDecisionDetails.builder()
                .requiredChanges(Collections.singletonList(
                    new ReviewDecisionRequiredChange("changesRequired", Collections.emptySet()))).build())
            .build();
        EmpVariationReviewDecision reviewNoAmendDecision = EmpVariationReviewDecision.builder()
            .type(EmpVariationReviewDecisionType.ACCEPTED).build();
        EmpVariationCorsiaApplicationReviewRequestTaskPayload payload =
            EmpVariationCorsiaApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(Map.of(
                    EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, reviewAmendDecision,
                    EmpCorsiaReviewGroup.ADDITIONAL_DOCUMENTS, reviewNoAmendDecision
                )).build();

        Request request = Request.builder()
            .id("2")
            .payload(EmpVariationCorsiaRequestPayload.builder().build())
            .build();
        RequestTask requestTask = RequestTask.builder()
            .id(taskId)
            .processTaskId(processTaskId)
            .payload(payload)
            .request(request)
            .build();

        EmpVariationCorsiaApplicationReturnedForAmendsRequestActionPayload actionPayload =
            EmpVariationCorsiaApplicationReturnedForAmendsRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.EMP_VARIATION_CORSIA_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD)
                .reviewGroupDecisions(Map.of(EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, reviewAmendDecision))
                .build();

        EmpVariationCorsiaRequestPayload newEmpVariationRequestPayload = EmpVariationCorsiaRequestPayload.builder()
            .reviewGroupDecisions(Map.of(EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, reviewAmendDecision))
            .build();
        Request newRequest = Request.builder()
            .id("2")
            .payload(newEmpVariationRequestPayload)
            .build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);

        // Invoke
        handler.process(taskId, RequestTaskActionType.EMP_VARIATION_CORSIA_REVIEW_RETURN_FOR_AMENDS, appUser,
            emptyPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(validatorService, times(1)).validate(payload);
        verify(service, times(1)).saveRequestReturnForAmends(requestTask, appUser);
        verify(requestService, times(1))
            .addActionToRequest(newRequest, actionPayload,
                RequestActionType.EMP_VARIATION_CORSIA_APPLICATION_RETURNED_FOR_AMENDS, userId);
        verify(workflowService, times(1))
            .completeTask(processTaskId,
                Map.of(BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.AMENDS_NEEDED.name()));
    }

    @Test
    void doProcess_not_valid() {
        long taskId = 1L;
        RequestTaskActionEmptyPayload emptyPayload = RequestTaskActionEmptyPayload
            .builder()
            .payloadType(RequestTaskActionPayloadType.EMPTY_PAYLOAD)
            .build();
        String userId = "userId";
        AppUser appUser = AppUser.builder().userId(userId).build();

        String processTaskId = "processTaskId";

        EmpVariationCorsiaApplicationReviewRequestTaskPayload payload =
            EmpVariationCorsiaApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(Map.of(
                    EmpCorsiaReviewGroup.ADDITIONAL_DOCUMENTS, EmpVariationReviewDecision
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
        doThrow(new BusinessException((MetsErrorCode.INVALID_EMP_VARIATION_REVIEW))).when(validatorService)
            .validate(payload);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
            () -> handler.process(taskId, RequestTaskActionType.EMP_VARIATION_CORSIA_REVIEW_RETURN_FOR_AMENDS, appUser,
                emptyPayload));

        // Verify
        assertEquals(MetsErrorCode.INVALID_EMP_VARIATION_REVIEW, businessException.getErrorCode());
        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(validatorService, times(1)).validate(payload);
        verify(service, never()).saveRequestReturnForAmends(any(), any());
        verify(requestService, never()).addActionToRequest(any(), any(), any(), anyString());
        verify(workflowService, never()).completeTask(anyString(), anyMap());
    }
}
