package uk.gov.pmrv.api.workflow.bpmn.handler.inspection.onsiteinspection;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestTaskTimeManagementService;

import java.time.ZoneId;
import java.util.Date;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InstallationOnsiteInspectionSetOperatorResponseTaskDueDateHandlerTest {


    @InjectMocks
    private InstallationOnsiteInspectionSetOperatorResponseTaskDueDateHandler handler;

    @Mock
    private RequestTaskTimeManagementService requestTaskTimeManagementService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {

        final String requestId = "INS00045-20";
        Date expirationDate = new Date();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.INSTALLATION_ONSITE_INSPECTION_EXPIRATION_DATE)).thenReturn(expirationDate);

        handler.execute(execution);

        verify(requestTaskTimeManagementService, times(1)).setDueDateToTasks(requestId, RequestExpirationType.INSTALLATION_ONSITE_INSPECTION,
            expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.INSTALLATION_ONSITE_INSPECTION_EXPIRATION_DATE);
    }
}
