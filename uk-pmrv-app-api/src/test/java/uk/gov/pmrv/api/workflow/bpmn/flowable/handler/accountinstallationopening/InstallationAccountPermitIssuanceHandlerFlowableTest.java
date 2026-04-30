package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.accountinstallationopening;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.handler.InstallationAccountPermitIssuanceService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationAccountPermitIssuanceHandlerFlowableTest {

    @InjectMocks
    private InstallationAccountPermitIssuanceHandlerFlowable handler;

    @Mock
    private InstallationAccountPermitIssuanceService installationAccountPermitIssuanceService;

    @Mock
    private DelegateExecution execution;

    @Test
    void executeTest() {
        // Mock data
        final String REQUEST_ID = "1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(REQUEST_ID);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(installationAccountPermitIssuanceService, times(1)).execute(REQUEST_ID);
    }

}
