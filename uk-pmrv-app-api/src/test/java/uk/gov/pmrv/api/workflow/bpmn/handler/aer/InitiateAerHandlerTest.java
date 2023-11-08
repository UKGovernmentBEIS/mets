package uk.gov.pmrv.api.workflow.bpmn.handler.aer;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerCreationService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.AerInitiatorRequest;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Date;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InitiateAerHandlerTest {

    @InjectMocks
    private InitiateAerHandler initiateAerHandler;

    @Mock
    private RequestService requestService;

    @Mock
    private AerCreationService aerCreationService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final String requestId = "1";
        final RequestType requestType = RequestType.PERMIT_REVOCATION;
        final Long accountId = 1L;
        final LocalDateTime submissionDateTime = LocalDateTime.now();
        final Request request = Request.builder().accountId(accountId).type(requestType).submissionDate(submissionDateTime).build();
        final Date dueDate = new Date();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.AER_EXPIRATION_DATE)).thenReturn(dueDate);
        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        initiateAerHandler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.AER_EXPIRATION_DATE);
        verify(requestService, times(1)).findRequestById(requestId);
		verify(aerCreationService, times(1)).createRequestAerForYear(accountId, Year.now(), dueDate,
				AerInitiatorRequest.builder().type(requestType).submissionDateTime(submissionDateTime).build());
    }
}
