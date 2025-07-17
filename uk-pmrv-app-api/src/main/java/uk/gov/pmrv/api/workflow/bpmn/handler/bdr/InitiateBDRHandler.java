package uk.gov.pmrv.api.workflow.bpmn.handler.bdr;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.utils.ExceptionUtils;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service.BDRCreationService;


@Log4j2
@Service
@RequiredArgsConstructor
public class InitiateBDRHandler implements JavaDelegate {

    private final BDRCreationService bdrCreationService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long accountId = (Long) execution.getVariable("accountId");
        initiateBDRWorkflow(accountId);
    }

    private void initiateBDRWorkflow(Long accountId) {
        try {
            bdrCreationService.createBDR(accountId);
        } catch (Exception ex) {
            log.error(() -> "Could not create BDR workflow for account with id '" + accountId
                + "' failed with " + ExceptionUtils.getRootCause(ex).getMessage(), ex);
        }
    }
}
