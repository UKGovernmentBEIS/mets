package uk.gov.pmrv.api.workflow.bpmn.handler.aer;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.utils.ExceptionUtils;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerCreationService;

/**
 * Initiates AER for </br>
 *
 * <ul>
 *     <li>LIVE accounts when the associated timer in Camunda has been executed</li>
 *     <li>OR for the provided account ids through the Camunda REST API. It is useful when some AERs have not been successfully executed
 *     when the timer kicked in.</li>
 * </ul>
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class InitiateInstallationAersHandler implements JavaDelegate {
    private final AerCreationService aerCreationService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long accountId = (Long) execution.getVariable("accountId");
        initiateAerWorkflow(accountId);
    }

    private void initiateAerWorkflow(Long accountId) {
        try {
            aerCreationService.createRequestAer(accountId, RequestType.AER);
        } catch (Exception ex) {
            log.error(() -> "Could not create AER workflow for account with id '" + accountId
                + "' failed with " + ExceptionUtils.getRootCause(ex).getMessage(), ex);
        }
    }
}
