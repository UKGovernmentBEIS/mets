package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.validation.PermitReviewReturnForAmendsValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.PermitTransferBReviewService;

@ExtendWith(MockitoExtension.class)
class PermitTransferBReviewReturnForAmendsHandlerTest {

    @InjectMocks
    private PermitTransferBReviewReturnForAmendsHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestService requestService;

    @Mock
    private PermitTransferBReviewService permitTransferBReviewService;

    @Mock
    private PermitReviewReturnForAmendsValidatorService permitReviewReturnForAmendsValidatorService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void doProcess() {

        final long taskId = 1L;
        final RequestTaskActionEmptyPayload emptyPayload = RequestTaskActionEmptyPayload.builder().payloadType(RequestTaskActionPayloadType.EMPTY_PAYLOAD).build();
        final String userId = "userId";
        final AppUser appUser = AppUser.builder().userId(userId).build();

        final String processTaskId = "processTaskId";

        final PermitIssuanceReviewDecision reviewAmendDecision = PermitIssuanceReviewDecision.builder()
            .type(ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
            .details(ChangesRequiredDecisionDetails.builder()
                .requiredChanges(Collections.singletonList(new ReviewDecisionRequiredChange("changesRequired", Collections.emptySet()))).build())
            .build();
        final PermitIssuanceReviewDecision reviewNoAmendDecision = PermitIssuanceReviewDecision.builder()
            .type(ReviewDecisionType.ACCEPTED).build();
        final PermitTransferBApplicationReviewRequestTaskPayload payload =
            PermitTransferBApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(Map.of(
                    PermitReviewGroup.CALCULATION_CO2, reviewAmendDecision,
                    PermitReviewGroup.FALLBACK, reviewNoAmendDecision
                )).build();
        final Request request = Request.builder()
            .id("2")
            .payload(PermitTransferBRequestPayload.builder().build())
            .build();
        final RequestTask requestTask = RequestTask.builder()
            .id(taskId)
            .processTaskId(processTaskId)
            .payload(payload)
            .request(request)
            .build();

        final PermitIssuanceApplicationReturnedForAmendsRequestActionPayload actionPayload =
            PermitIssuanceApplicationReturnedForAmendsRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.PERMIT_TRANSFER_B_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD)
                .reviewGroupDecisions(Map.of(PermitReviewGroup.CALCULATION_CO2, reviewAmendDecision))
                .build();

        final PermitTransferBRequestPayload permitTransferBRequestPayload = PermitTransferBRequestPayload.builder()
            .reviewGroupDecisions(Map.of(PermitReviewGroup.CALCULATION_CO2, reviewAmendDecision))
            .build();
        final Request newRequest = Request.builder()
            .id("2")
            .payload(permitTransferBRequestPayload)
            .build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);

        // Invoke
        handler.process(taskId, RequestTaskActionType.PERMIT_TRANSFER_B_REVIEW_RETURN_FOR_AMENDS, appUser, emptyPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(permitReviewReturnForAmendsValidatorService, times(1)).validate(payload);
        verify(permitTransferBReviewService, times(1)).updatePermitTransferBRequestPayload(requestTask, appUser);
        verify(requestService, times(1))
            .addActionToRequest(newRequest, actionPayload, RequestActionType.PERMIT_TRANSFER_B_APPLICATION_RETURNED_FOR_AMENDS, userId);
        verify(workflowService, times(1))
            .completeTask(processTaskId, Map.of(BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.AMENDS_NEEDED.name()));
    }

    @Test
    void doProcess_not_valid() {

        final long taskId = 1L;
        final RequestTaskActionEmptyPayload emptyPayload = RequestTaskActionEmptyPayload.builder().payloadType(RequestTaskActionPayloadType.EMPTY_PAYLOAD).build();
        final String userId = "userId";
        final AppUser appUser = AppUser.builder().userId(userId).build();

        final String processTaskId = "processTaskId";

        final PermitTransferBApplicationReviewRequestTaskPayload payload =
            PermitTransferBApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(Map.of(
                    PermitReviewGroup.CALCULATION_CO2, PermitIssuanceReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).build()
                )).build();

        final RequestTask requestTask = RequestTask.builder()
            .id(taskId)
            .processTaskId(processTaskId)
            .payload(payload)
            .request(Request.builder().build())
            .build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        doThrow(new BusinessException((MetsErrorCode.INVALID_PERMIT_REVIEW))).when(permitReviewReturnForAmendsValidatorService)
            .validate(payload);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
            () -> handler.process(taskId, RequestTaskActionType.PERMIT_TRANSFER_B_REVIEW_RETURN_FOR_AMENDS, appUser, emptyPayload));

        // Verify
        assertEquals(MetsErrorCode.INVALID_PERMIT_REVIEW, businessException.getErrorCode());
        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(permitReviewReturnForAmendsValidatorService, times(1)).validate(payload);
        verify(permitTransferBReviewService, never()).updatePermitTransferBRequestPayload(any(), any());
        verify(requestService, never()).addActionToRequest(any(), any(), any(), anyString());
        verify(workflowService, never()).completeTask(anyString(), anyMap());
    }
}
