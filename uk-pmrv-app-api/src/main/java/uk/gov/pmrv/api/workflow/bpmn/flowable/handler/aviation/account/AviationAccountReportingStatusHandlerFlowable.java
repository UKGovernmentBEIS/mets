package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.account;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.aviation.service.reportingstatus.AviationAccountReportingStatusPopulationService;

@Service
@RequiredArgsConstructor
public class AviationAccountReportingStatusHandlerFlowable implements JavaDelegate {

    private final AviationAccountReportingStatusPopulationService aviationAccountReportingStatusPopulationService;

    @Override
    @Transactional
    public void execute(DelegateExecution delegateExecution) {
        aviationAccountReportingStatusPopulationService.populateReportingStatusesForNewYear();
    }
}