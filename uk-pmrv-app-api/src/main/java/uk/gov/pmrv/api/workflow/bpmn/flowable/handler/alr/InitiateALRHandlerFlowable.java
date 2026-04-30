package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.alr;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.utils.ExceptionUtils;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRCreationService;

import java.util.Date;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class InitiateALRHandlerFlowable implements JavaDelegate {
    private final ALRCreationService alrCreationService;

    @Override
    public void execute(DelegateExecution execution) {
        Long accountId = (Long) execution.getVariable(BpmnProcessConstants.ACCOUNT_ID);
        Optional<Date> expirationDateOpt =
                Optional.ofNullable((Date) execution.getVariable(BpmnProcessConstants.ALR_EXPIRATION_DATE));
        initiateALRWorkflow(accountId, (Boolean) execution.getVariable(BpmnProcessConstants.ALR_FINAL), expirationDateOpt);
    }

    private void initiateALRWorkflow(Long accountId, boolean isFinal, Optional<Date> alrExpirationDateOpt) {
        try {
            alrCreationService.createALRInNewTransaction(accountId, isFinal, alrExpirationDateOpt);
        } catch (Exception ex) {
            log.error(() -> "Could not create ALR workflow for account with id '" + accountId
                    + "' failed with " + ExceptionUtils.getRootCause(ex).getMessage(), ex);
        }
    }
}
