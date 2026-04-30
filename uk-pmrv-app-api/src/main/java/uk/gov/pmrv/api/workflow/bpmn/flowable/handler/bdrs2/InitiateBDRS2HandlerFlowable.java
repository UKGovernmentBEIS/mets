package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.bdrs2;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.utils.ExceptionUtils;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service.BDRS2CreationService;

@Log4j2
@Service
@RequiredArgsConstructor
public class InitiateBDRS2HandlerFlowable implements JavaDelegate {

    private final BDRS2CreationService bdrs2CreationService;

    @Override
    public void execute(DelegateExecution execution) {
        Long accountId = (Long) execution.getVariable("accountId");
        initiateBDRS2Workflow(accountId);
    }

    private void initiateBDRS2Workflow(Long accountId) {
        try {
            bdrs2CreationService.createBDRS2(accountId);
        } catch (Exception ex) {
            log.error("Could not create BDRS2 workflow for account with id '{}' failed with {}",
                    accountId, ExceptionUtils.getRootCause(ex).getMessage());
        }
    }
}
