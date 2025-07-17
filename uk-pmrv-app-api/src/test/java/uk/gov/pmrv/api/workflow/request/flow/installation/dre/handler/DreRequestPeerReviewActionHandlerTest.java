package uk.gov.pmrv.api.workflow.request.flow.installation.dre.handler;

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

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.service.DreApplyService;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.service.DreRequestPeerReviewValidator;

@ExtendWith(MockitoExtension.class)
class DreRequestPeerReviewActionHandlerTest {

	@InjectMocks
    private DreRequestPeerReviewActionHandler cut;

    @Mock
    private RequestTaskService requestTaskService;
    
    @Mock
    private DreApplyService dreApplyService;
    
    @Mock
    private RequestService requestService;
    
    @Mock
    private WorkflowService workflowService;
    
    @Mock
    private DreRequestPeerReviewValidator dreRequestPeerReviewValidator;
    
    @Test
	void process() {
    	String requestId = "1";
    	Long requestTaskId = 2L;
    	RequestTaskActionType requestTaskActionType = RequestTaskActionType.DRE_REQUEST_PEER_REVIEW;
    	AppUser appUser = AppUser.builder().userId("user").build();
    	PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
				.payloadType(RequestTaskActionPayloadType.DRE_REQUEST_PEER_REVIEW_PAYLOAD)
				.peerReviewer("reg2")
				.build();
		Request request = Request.builder().id(requestId).build();
		
		RequestTask requestTask = RequestTask.builder()
	            .id(requestTaskId)
	            .processTaskId("processTaskId")
	            .request(request)
	            .build();
		
		when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
		
		cut.process(requestTaskId, requestTaskActionType,  appUser, taskActionPayload);
		
		verify(requestTaskService, times(1)).findTaskById(requestTaskId);
		verify(dreRequestPeerReviewValidator, times(1)).validate(requestTask, taskActionPayload, appUser);
		verify(dreApplyService, times(1)).requestPeerReview(requestTask, "reg2", appUser);
		verify(requestService, times(1))
			.addActionToRequest(request, null, RequestActionType.DRE_APPLICATION_PEER_REVIEW_REQUESTED, appUser.getUserId());
		
        verify(workflowService, times(1)).completeTask(requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
            		BpmnProcessConstants.DRE_SUBMIT_OUTCOME, DreSubmitOutcome.PEER_REVIEW_REQUIRED));
    }
    
    @Test
	void getTypes() {
    	assertThat(cut.getTypes()).containsExactly(RequestTaskActionType.DRE_REQUEST_PEER_REVIEW);
    }
}
