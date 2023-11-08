package uk.gov.pmrv.api.workflow.request.core.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @InjectMocks
    private RequestService requestService;

    @Mock
    private RequestRepository requestRepository;
    
    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;
    
    @Test
    void addActionToRequest() {
    	final String user = "user";
    	final RequestActionType type = RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED;
        Request request = createRequest("1", "1", 1L, RequestType.INSTALLATION_ACCOUNT_OPENING);
        RequestActionPayload payload = Mockito.mock(RequestActionPayload.class);
        
        //assert before
        assertThat(request.getRequestActions()).isEmpty();
        
        when(requestActionUserInfoResolver.getUserFullName(user)).thenReturn("submitter name");
        
        //invoke
        requestService.addActionToRequest(request, 
        		payload, 
        		type, 
        		user);
        
        //verify
        verify(requestActionUserInfoResolver, times(1)).getUserFullName(user);
        
        //assert
        assertThat(request.getRequestActions()).hasSize(1);
        assertThat(request.getRequestActions()).extracting(RequestAction::getType).containsOnly(type);
        assertThat(request.getRequestActions()).extracting(RequestAction::getPayload).containsOnly(payload);
    }

    @Test
    void updateRequestStatus() {
        Request request = Request.builder().id("1").status(RequestStatus.IN_PROGRESS).build();

        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));

        // Invoke
        requestService.updateRequestStatus(request.getId(), RequestStatus.APPROVED);

        // Verify
        assertEquals(request.getStatus(), RequestStatus.APPROVED);
    }
    
    @Test
    void terminateRequest() {
    	//prepare data
        String processId = "1";
        Request request = createRequest("1", processId, 1L, RequestType.PERMIT_ISSUANCE);
        request.setPayload(PermitIssuanceRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_ISSUANCE_REQUEST_PAYLOAD)
        .build()
        );
        request.setStatus(RequestStatus.IN_PROGRESS);
        
        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));
        
        //assert before
        assertThat(request.getStatus()).isEqualTo(RequestStatus.IN_PROGRESS);
        
        //invoke
        requestService.terminateRequest(request.getId(), processId, false);
        
        //assert
        assertThat(request.getStatus()).isEqualTo(RequestStatus.COMPLETED);
        assertThat(request.getPayload()).isNull();

        //verify
        verify(requestRepository, never()).delete(request);
    }

    @Test
    void terminateRequest_system_message_notification() {
        //prepare data
        String processId = "1";
        Request request = createRequest("1", processId, 1L, RequestType.SYSTEM_MESSAGE_NOTIFICATION);
        request.setPayload(PermitIssuanceRequestPayload.builder()
            .payloadType(RequestPayloadType.SYSTEM_MESSAGE_NOTIFICATION_REQUEST_PAYLOAD)
            .build()
        );

        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));

        //invoke
        requestService.terminateRequest(request.getId(), processId, false);

        //verify
        verify(requestRepository, times(1)).delete(request);
    }
    
    @Test
    void terminateRequest_should_delete_request() {
        String processId = "1";
        Request request = createRequest("1", processId, 1L, RequestType.PERMIT_REISSUE);

        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));

        //invoke
        requestService.terminateRequest(request.getId(), processId, true);

        //verify
        verify(requestRepository, times(1)).delete(request);
    }

    @Test
    void terminateRequest_not_same_process_instance() {
        //prepare data
        String processId = "1";
        Request request = createRequest("1","2", 1L, RequestType.PERMIT_ISSUANCE);
        request.setStatus(RequestStatus.IN_PROGRESS);

        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));

        //invoke
        requestService.terminateRequest(request.getId(), processId, false);

        //verify
        verifyNoMoreInteractions(requestRepository);
    }
    
    private Request createRequest(String requestId, String processInstanceId, Long accountId, RequestType type) {
        Request request = new Request();
        request.setId(requestId);
        request.setAccountId(accountId);
        request.setProcessInstanceId(processInstanceId);
        request.setType(type);
        return request;
    }
}
