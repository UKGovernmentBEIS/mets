package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerReviewService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.validation.AerReviewReturnForAmendsValidatorService;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerReviewReturnForAmendsHandlerTest {

    @Mock
    private RequestTaskService requestTaskService;
    @Mock
    private RequestService requestService;
    @Mock
    private AerReviewService aerReviewService;
    @Mock
    private AerReviewReturnForAmendsValidatorService aerReviewReturnForAmendsValidatorService;
    @Mock
    private WorkflowService workflowService;
    @Mock
    private AerApplicationReturnedForAmendsRequestActionPayload requestActionPayload;

    @InjectMocks
    private AerReviewReturnForAmendsHandler aerReviewReturnForAmendsHandler;

    @Test
    void process() {
        Long requestTaskId = 1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.AER_REVIEW_RETURN_FOR_AMENDS;
        RequestTaskActionEmptyPayload payload = new RequestTaskActionEmptyPayload();
        String processTaskId = "processTaskId";
        AerApplicationReviewRequestTaskPayload taskPayload =
            AerApplicationReviewRequestTaskPayload.builder()
                .reviewGroupDecisions(Map.of(AerReviewGroup.FALLBACK, AerDataReviewDecision.builder()
                    .reviewDataType(AerReviewDataType.AER_DATA)
                    .type(AerDataReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                    .details(ChangesRequiredDecisionDetails.builder().build())
                    .build()))
                .build();
        AppUser appUser = AppUser.builder().build();
        Request request = Request.builder().build();
        RequestTask requestTask = RequestTask.builder()
            .processTaskId(processTaskId)
            .request(request)
            .payload(taskPayload)
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        aerReviewReturnForAmendsHandler.process(requestTaskId, requestTaskActionType, appUser, payload);

        verify(aerReviewReturnForAmendsValidatorService).validate(taskPayload);
        verify(aerReviewService).saveRequestReturnForAmends(requestTask, appUser);
        verify(workflowService).completeTask(processTaskId,
            Map.of(BpmnProcessConstants.AER_REVIEW_OUTCOME, ReviewOutcome.AMENDS_NEEDED.name()));
    }

    @Test
    void getTypes() {
        assertEquals(List.of(RequestTaskActionType.AER_REVIEW_RETURN_FOR_AMENDS),
            aerReviewReturnForAmendsHandler.getTypes());
    }

}