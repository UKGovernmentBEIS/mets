package uk.gov.pmrv.api.workflow.bpmn.handler.permitvariation;

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
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.PermitVariationAddCancelledRequestActionService;

@ExtendWith(MockitoExtension.class)
public class PermitVariationAddCancelledRequestActionHandlerTest {

	@InjectMocks
    private PermitVariationAddCancelledRequestActionHandler cut;

    @Mock
    private PermitVariationAddCancelledRequestActionService service;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        String requestId = "1";
        RoleType userRole = RoleType.OPERATOR;
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.REQUEST_INITIATOR_ROLE_TYPE)).thenReturn(userRole);

        cut.execute(execution);
        
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_INITIATOR_ROLE_TYPE);
        verify(service, times(1)).add(requestId, userRole);
    }
}
