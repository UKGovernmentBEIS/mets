package uk.gov.pmrv.api.workflow.bpmn.handler.wasteqdr;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.utils.ExceptionUtils;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service.WasteQDRCreationService;

@Log4j2
@Service
@RequiredArgsConstructor
public class InitiateWasteQDRHandler implements JavaDelegate {

    private final WasteQDRCreationService wasteQDRCreationService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long accountId = (Long) execution.getVariable(BpmnProcessConstants.ACCOUNT_ID);
        initiateWasteQDRWorkflow(accountId);
    }

    private void initiateWasteQDRWorkflow(Long accountId) {
        try {
            wasteQDRCreationService.createWasteQDR(accountId);
        } catch (Exception ex) {
            log.error(() -> "Could not create Waste QDR workflow for account with id '" + accountId
                    + "' failed with " + ExceptionUtils.getRootCause(ex).getMessage(), ex);
        }
    }
}
