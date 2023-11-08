package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NerApplyReviewService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation.NerReviewReturnForAmendsValidator;

@ExtendWith(MockitoExtension.class)
class NerReviewReturnForAmendsActionHandlerTest {

    @InjectMocks
    private NerReviewReturnForAmendsActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestService requestService;

    @Mock
    private NerApplyReviewService applyReviewService;

    @Mock
    private NerReviewReturnForAmendsValidator amendsValidator;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {

        final PmrvUser pmrvUser = PmrvUser.builder().build();
        final String processTaskId = "processTaskId";
        final NerApplicationReviewRequestTaskPayload taskPayload = NerApplicationReviewRequestTaskPayload.builder().build();
        final Request request = Request.builder().build();
        final RequestTask requestTask = RequestTask.builder()
            .id(1L)
            .payload(taskPayload)
            .request(request)
            .processTaskId(processTaskId)
            .build();
        final NerApplicationReturnedForAmendsRequestActionPayload actionPayload = 
            NerApplicationReturnedForAmendsRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.NER_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD)
                .build();
        
        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        //invoke
        handler.process(requestTask.getId(),
            RequestTaskActionType.NER_REVIEW_RETURN_FOR_AMENDS,
            pmrvUser,
            RequestTaskActionEmptyPayload.builder().build());

        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(amendsValidator, times(1)).validate(taskPayload);
        verify(applyReviewService, times(1)).updateRequestPayload(requestTask, pmrvUser);
        verify(requestService, times(1)).addActionToRequest(
            request,
            actionPayload,
            RequestActionType.NER_APPLICATION_RETURNED_FOR_AMENDS,
            pmrvUser.getUserId()
        );
        verify(workflowService, times(1)).completeTask(processTaskId,
            Map.of(BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.AMENDS_NEEDED.name()));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.NER_REVIEW_RETURN_FOR_AMENDS);
    }
}