package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aer;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.utils.ExceptionUtils;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerCreationService;

/**
 * Initiates AER for </br>
 *
 * <ul>
 *     <li>LIVE accounts when the associated timer in BPMN has been executed</li>
 *     <li>OR for the provided account ids through the BPMN REST API. It is useful when some AERs have not been successfully executed
 *     when the timer kicked in.</li>
 * </ul>
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class InitiateInstallationAersHandlerFlowable implements JavaDelegate {
    private final AerCreationService aerCreationService;

    @Override
    public void execute(DelegateExecution execution) {
        Long accountId = (Long) execution.getVariable("accountId");
        initiateAerWorkflow(accountId);
    }

    private void initiateAerWorkflow(Long accountId) {
        try {
            aerCreationService.createRequestAerInNewTransaction(accountId, RequestType.AER);
        } catch (Exception ex) {
            log.error(() -> "Could not create AER workflow for account with id '" + accountId
                + "' failed with " + ExceptionUtils.getRootCause(ex).getMessage(), ex);
        }
    }
}
