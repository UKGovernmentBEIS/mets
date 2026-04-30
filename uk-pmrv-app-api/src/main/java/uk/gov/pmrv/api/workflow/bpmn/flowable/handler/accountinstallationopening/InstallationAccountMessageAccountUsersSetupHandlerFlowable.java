package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.accountinstallationopening;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.handler.InstallationAccountMessageAccountUsersSetupService;

import static uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants.REQUEST_ID;

@Component
@RequiredArgsConstructor
public class InstallationAccountMessageAccountUsersSetupHandlerFlowable implements JavaDelegate {
    private final InstallationAccountMessageAccountUsersSetupService installationAccountMessageAccountUsersSetupService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        installationAccountMessageAccountUsersSetupService.execute((String) delegateExecution.getVariable(REQUEST_ID));
    }
}
