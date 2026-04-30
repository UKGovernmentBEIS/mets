package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.wasteqdr;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WasteQDRCompletedHandlerFlowable implements JavaDelegate {

    @Override
    public void execute(DelegateExecution delegateExecution) {
        //Just do nothing
    }
}
