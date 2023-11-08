package uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.aviation.service.AviationAccountUpdateService;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.domain.AviationAccountClosure;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.domain.AviationAccountClosureRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.domain.AviationAccountClosureSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.domain.AviationAccountClosureSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.domain.AviationAccountClosureSubmittedRequestActionPayload;


@ExtendWith(MockitoExtension.class)
class RequestAviationAccountClosureServiceTest {
	
	@InjectMocks
    private RequestAviationAccountClosureService service;
	
	@Mock
    private RequestService requestService;
	
	@Mock
    private AviationAccountUpdateService aviationAccountUpdateService;
	
	@Mock
	private AviationAccountClosureValidatorService validatorService;
	
	@Test
    void applySavePayload() {

        AviationAccountClosureSubmitRequestTaskPayload taskPayload = createTaskPayload();

        AviationAccountClosureSaveRequestTaskActionPayload actionPayload = createTaskActionPayload();

        RequestTask requestTask = createRequestTask(taskPayload, null);

        service.applySavePayload(actionPayload, requestTask);

        assertThat(taskPayload.getAviationAccountClosure()).isEqualTo(actionPayload.getAviationAccountClosure());
    }
	
	@Test
    void applySubmitAction() {

		PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        AviationAccountClosureSubmitRequestTaskPayload taskPayload = createTaskPayload();
        AviationAccountClosureSubmittedRequestActionPayload actionPayload = createActionPayload();

        AviationAccountClosureRequestPayload requestPayload = AviationAccountClosureRequestPayload.builder().build();
        Request request = Request.builder().payload(requestPayload).accountId(1L).build();

        RequestTask requestTask = createRequestTask(taskPayload, request);

        service.applySubmitAction(requestTask, pmrvUser);

        verify(validatorService, times(1)).validateAviationAccountClosureObject(
        		taskPayload.getAviationAccountClosure()
            );
        verify(requestService, times(1)).addActionToRequest(
                request,
                actionPayload,
                RequestActionType.AVIATION_ACCOUNT_CLOSURE_SUBMITTED,
                "userId"
            );
        verify(aviationAccountUpdateService, times(1)).closeAviationAccount(
        		1L,
        		pmrvUser,
        		taskPayload.getAviationAccountClosure().getReason()
            );
        assertEquals(taskPayload.getAviationAccountClosure(), requestPayload.getAviationAccountClosure());    
    }
	

	@Test
    void cancel() {

		String requestId = "requestId";
		String assignee = "assignee";
		Request request = Request.builder()
            .id(requestId)
            .accountId(1L)
            .payload(AviationAccountClosureRequestPayload.builder()
            		.aviationAccountClosure(null)
            		.regulatorAssignee(assignee)
                    .build())
            .build();
		
		when(requestService.findRequestById(requestId)).thenReturn(request);

        service.cancel(requestId);

        assertEquals(RequestStatus.CANCELLED, request.getStatus());
        verify(requestService, times(1)).addActionToRequest(
        		request, null, RequestActionType.AVIATION_ACCOUNT_CLOSURE_CANCELLED, assignee);
    }

	private RequestTask createRequestTask(AviationAccountClosureSubmitRequestTaskPayload taskPayload, Request request) {
		RequestTask requestTask = RequestTask.builder()
            .id(1L)
            .type(RequestTaskType.AVIATION_ACCOUNT_CLOSURE_SUBMIT)
            .payload(taskPayload)
            .request(request)
            .build();
		return requestTask;
	}

	private AviationAccountClosureSubmitRequestTaskPayload createTaskPayload() {
		AviationAccountClosureSubmitRequestTaskPayload taskPayload =
        		AviationAccountClosureSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_ACCOUNT_CLOSURE_SUBMIT_PAYLOAD)
                .aviationAccountClosure(AviationAccountClosure.builder().reason("test reason").build())
                .build();
		return taskPayload;
	}
	
	private AviationAccountClosureSaveRequestTaskActionPayload createTaskActionPayload() {
		AviationAccountClosureSaveRequestTaskActionPayload actionPayload =
        		AviationAccountClosureSaveRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.AVIATION_ACCOUNT_CLOSURE_SAVE_APPLICATION_PAYLOAD)
                .aviationAccountClosure(AviationAccountClosure.builder().reason("test reason2").build())
                .build();
		return actionPayload;
	}
	
	private AviationAccountClosureSubmittedRequestActionPayload createActionPayload() {
		AviationAccountClosureSubmittedRequestActionPayload actionPayload =
				AviationAccountClosureSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.AVIATION_ACCOUNT_CLOSURE_SUBMITTED_PAYLOAD)
                .aviationAccountClosure(AviationAccountClosure.builder().reason("test reason").build())
                .build();
		return actionPayload;
	}
}
