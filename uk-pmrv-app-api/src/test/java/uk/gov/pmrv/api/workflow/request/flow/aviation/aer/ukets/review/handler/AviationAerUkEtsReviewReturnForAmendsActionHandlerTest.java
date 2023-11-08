package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.mapper.AviationAerUkEtsReviewMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.service.RequestAviationAerUkEtsReviewService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.validation.RequestAviationAerUkEtsReviewValidatorService;
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
class AviationAerUkEtsReviewReturnForAmendsActionHandlerTest {

    @InjectMocks
    private AviationAerUkEtsReviewReturnForAmendsActionHandler returnForAmendsActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestAviationAerUkEtsReviewService aerUkEtsReviewService;

    @Mock
    private RequestAviationAerUkEtsReviewValidatorService aerUkEtsReviewValidatorService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private AviationAerUkEtsReviewMapper aviationAerUkEtsReviewMapper;

    @Test
    void process() {
        Long requestTaskId = 1L;
        PmrvUser user = PmrvUser.builder().userId("userId").build();
        RequestTaskActionEmptyPayload taskActionEmptyPayload = RequestTaskActionEmptyPayload.builder().build();

        Request request = Request.builder().id("REQ-ID").build();

        Map<AviationAerUkEtsReviewGroup, AerReviewDecision> reviewGroupDecisions = new HashMap<>();
        AviationAerUkEts aer = AviationAerUkEts.builder().build();
        AviationAerUkEtsApplicationReviewRequestTaskPayload requestTaskPayload =
            AviationAerUkEtsApplicationReviewRequestTaskPayload.builder()
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

        AviationAerUkEtsApplicationReturnedForAmendsRequestActionPayload returnedForAmendsRequestActionPayload =
            AviationAerUkEtsApplicationReturnedForAmendsRequestActionPayload.builder().build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(aviationAerUkEtsReviewMapper
            .toAviationAerUkEtsApplicationReturnedForAmendsRequestActionPayload(requestTaskPayload, RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD))
            .thenReturn(returnedForAmendsRequestActionPayload);

        returnForAmendsActionHandler.process(requestTaskId, RequestTaskActionType.AVIATION_AER_UKETS_REVIEW_RETURN_FOR_AMENDS, user, taskActionEmptyPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(aerUkEtsReviewValidatorService, times(1)).validateAtLeastOneReviewGroupAmendsNeeded(requestTaskPayload);
        verify(aerUkEtsReviewService, times(1)).updateRequestPayloadWithReviewOutcome(requestTask, user);
        verify(aviationAerUkEtsReviewMapper, times(1))
            .toAviationAerUkEtsApplicationReturnedForAmendsRequestActionPayload(requestTaskPayload,
                RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD
            );
        verify(aviationAerUkEtsReviewMapper, times(1))
            .toAviationAerUkEtsApplicationReturnedForAmendsRequestActionPayload(requestTaskPayload,
                RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD
            );
        verify(requestService, times(1))
            .addActionToRequest(request, returnedForAmendsRequestActionPayload, RequestActionType.AVIATION_AER_UKETS_APPLICATION_RETURNED_FOR_AMENDS, user.getUserId());
        verify(workflowService, times(1)).
            completeTask(processTaskId, Map.of(BpmnProcessConstants.AVIATION_AER_REVIEW_OUTCOME, ReviewOutcome.AMENDS_NEEDED));
    }

    @Test
    void getTypes() {
        assertThat(returnForAmendsActionHandler.getTypes()).containsOnly(RequestTaskActionType.AVIATION_AER_UKETS_REVIEW_RETURN_FOR_AMENDS);
    }
}