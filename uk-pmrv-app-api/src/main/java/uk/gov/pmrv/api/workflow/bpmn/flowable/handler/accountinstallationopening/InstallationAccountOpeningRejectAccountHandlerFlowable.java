package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.accountinstallationopening;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.handler.InstallationAccountOpeningRejectAccountService;

@Component
@RequiredArgsConstructor
public class InstallationAccountOpeningRejectAccountHandlerFlowable implements JavaDelegate {
    private final InstallationAccountOpeningRejectAccountService installationAccountOpeningRejectAccountService;

    @Override
    public void execute(DelegateExecution execution) {
        installationAccountOpeningRejectAccountService.execute((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
