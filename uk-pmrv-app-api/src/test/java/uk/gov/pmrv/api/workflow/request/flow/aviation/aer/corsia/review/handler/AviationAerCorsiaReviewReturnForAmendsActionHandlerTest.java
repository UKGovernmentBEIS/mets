package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.mapper.AviationAerCorsiaReviewMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.service.RequestAviationAerCorsiaReviewService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.validation.RequestAviationAerCorsiaReviewValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaReviewReturnForAmendsActionHandlerTest {

    @InjectMocks
    private AviationAerCorsiaReviewReturnForAmendsActionHandler returnForAmendsActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestAviationAerCorsiaReviewService aerCorsiaReviewService;

    @Mock
    private RequestAviationAerCorsiaReviewValidatorService aerCorsiaReviewValidatorService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private AviationAerCorsiaReviewMapper aviationAerCorsiaReviewMapper;

    @Test
    void process() {
        Long requestTaskId = 1L;
        PmrvUser user = PmrvUser.builder().userId("userId").build();
        RequestTaskActionEmptyPayload taskActionEmptyPayload = RequestTaskActionEmptyPayload.builder().build();

        Request request = Request.builder().id("REQ-ID").build();

        Map<AviationAerCorsiaReviewGroup, AerReviewDecision> reviewGroupDecisions = new HashMap<>();
        AviationAerCorsia aer = AviationAerCorsia.builder().build();
        AviationAerCorsiaApplicationReviewRequestTaskPayload requestTaskPayload =
                AviationAerCorsiaApplicationReviewRequestTaskPayload.builder()
                        .aer(aer)
                        .reviewGroupDecisions(reviewGroupDecisions)
                        .build();
        String processTaskId = "processTaskId";
        RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .request(request)
                .payload(requestTaskPayload)
                .processTaskId(processTaskId)
                .build();

        AviationAerCorsiaApplicationReturnedForAmendsRequestActionPayload returnedForAmendsRequestActionPayload =
                AviationAerCorsiaApplicationReturnedForAmendsRequestActionPayload.builder().build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(aviationAerCorsiaReviewMapper
                .toAviationAerCorsiaApplicationReturnedForAmendsRequestActionPayload(requestTaskPayload, RequestActionPayloadType.AVIATION_AER_CORSIA_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD))
                .thenReturn(returnedForAmendsRequestActionPayload);

        returnForAmendsActionHandler.process(requestTaskId, RequestTaskActionType.AVIATION_AER_CORSIA_REVIEW_RETURN_FOR_AMENDS, user, taskActionEmptyPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(aerCorsiaReviewValidatorService, times(1)).validateAtLeastOneReviewGroupAmendsNeeded(requestTaskPayload);
        verify(aerCorsiaReviewService, times(1)).updateRequestPayloadWithReviewOutcome(requestTask, user);
        verify(aviationAerCorsiaReviewMapper, times(1))
                .toAviationAerCorsiaApplicationReturnedForAmendsRequestActionPayload(requestTaskPayload,
                        RequestActionPayloadType.AVIATION_AER_CORSIA_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD
                );
        verify(aviationAerCorsiaReviewMapper, times(1))
                .toAviationAerCorsiaApplicationReturnedForAmendsRequestActionPayload(requestTaskPayload,
                        RequestActionPayloadType.AVIATION_AER_CORSIA_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD
                );
        verify(requestService, times(1))
                .addActionToRequest(request, returnedForAmendsRequestActionPayload, RequestActionType.AVIATION_AER_CORSIA_APPLICATION_RETURNED_FOR_AMENDS, user.getUserId());
        verify(workflowService, times(1)).
                completeTask(processTaskId, Map.of(BpmnProcessConstants.AVIATION_AER_REVIEW_OUTCOME, ReviewOutcome.AMENDS_NEEDED));
    }

    @Test
    void getTypes() {
        assertThat(returnForAmendsActionHandler.getTypes()).containsOnly(RequestTaskActionType.AVIATION_AER_CORSIA_REVIEW_RETURN_FOR_AMENDS);
    }
}
