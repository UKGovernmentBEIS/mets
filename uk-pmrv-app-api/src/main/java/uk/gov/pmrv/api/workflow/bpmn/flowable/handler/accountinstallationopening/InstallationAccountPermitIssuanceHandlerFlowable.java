package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.accountinstallationopening;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.handler.InstallationAccountPermitIssuanceService;

@Component
@RequiredArgsConstructor
public class InstallationAccountPermitIssuanceHandlerFlowable implements JavaDelegate {
    private final InstallationAccountPermitIssuanceService installationAccountPermitIssuanceService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        installationAccountPermitIssuanceService.execute((String) delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
