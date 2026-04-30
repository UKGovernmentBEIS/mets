package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.inspection.onsiteinspection;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.service.InstallationOnsiteInspectionOfficialNoticeService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InstallationOnsiteInspectionSubmittedSendOfficialNoticeHandlerFlowableTest {

    @InjectMocks
    private InstallationOnsiteInspectionSubmittedSendOfficialNoticeHandlerFlowable handler;

    @Mock
    private DelegateExecution execution;

    @Mock
    private InstallationOnsiteInspectionOfficialNoticeService installationInspectionOfficialNoticeService;

    @Test
    void execute() {
        final String requestId = "1";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(installationInspectionOfficialNoticeService, times(1))
                .sendOfficialNotice(requestId);
    }
}
