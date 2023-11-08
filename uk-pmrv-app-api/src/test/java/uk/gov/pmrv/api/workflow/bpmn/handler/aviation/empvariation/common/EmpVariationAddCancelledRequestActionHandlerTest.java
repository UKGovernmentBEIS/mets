package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.empvariation.common;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.service.EmpVariationAddCancelledRequestActionService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class EmpVariationAddCancelledRequestActionHandlerTest {

	@InjectMocks
    private EmpVariationAddCancelledRequestActionHandler handler;

    @Mock
    private EmpVariationAddCancelledRequestActionService service;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        String requestId = "1";
        RoleType userRole = RoleType.OPERATOR;
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.REQUEST_INITIATOR_ROLE_TYPE)).thenReturn(userRole);

        handler.execute(execution);
        
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_INITIATOR_ROLE_TYPE);
        verify(service, times(1)).add(requestId, userRole);
    }
}
