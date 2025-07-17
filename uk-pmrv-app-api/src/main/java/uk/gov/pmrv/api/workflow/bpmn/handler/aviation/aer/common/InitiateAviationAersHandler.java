package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.aer.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.utils.ExceptionUtils;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerCreationService;

@Log4j2
@Service
@RequiredArgsConstructor
public class InitiateAviationAersHandler implements JavaDelegate {
    private final AviationAerCreationService aviationAerCreationService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long accountId = (Long) execution.getVariable("accountId");
        initiateAviationAerWorkflow(accountId);
    }


    private void initiateAviationAerWorkflow(Long accountId) {
        try {
            aviationAerCreationService.createRequestAviationAer(accountId);
        } catch (Exception ex) {
            log.error(() -> "Could not create AVIATION_AER workflow for account with id '" + accountId
                    + "' failed with " + ExceptionUtils.getRootCause(ex).getMessage(), ex);
        }
    }
}
