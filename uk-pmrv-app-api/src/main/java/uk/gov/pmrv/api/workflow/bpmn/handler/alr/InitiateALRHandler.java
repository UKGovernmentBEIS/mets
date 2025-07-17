package uk.gov.pmrv.api.workflow.bpmn.handler.alr;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.utils.ExceptionUtils;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRCreationService;

@Log4j2
@Service
@RequiredArgsConstructor
public class InitiateALRHandler implements JavaDelegate {
    private final ALRCreationService alrCreationService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long accountId = (Long) execution.getVariable("accountId");
        initiateALRWorkflow(accountId);
    }

    private void initiateALRWorkflow(Long accountId) {
        try {
            alrCreationService.createALR(accountId);
        } catch (Exception ex) {
            log.error(() -> "Could not create ALR workflow for account with id '" + accountId
                    + "' failed with " + ExceptionUtils.getRootCause(ex).getMessage(), ex);
        }
    }
}
