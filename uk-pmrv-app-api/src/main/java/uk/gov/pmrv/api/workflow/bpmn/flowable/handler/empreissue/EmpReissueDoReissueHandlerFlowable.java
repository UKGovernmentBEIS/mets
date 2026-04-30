package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.empreissue;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.flowable.engine.delegate.BpmnError;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.service.EmpReissueDoReissueService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Log4j2
@Service
@RequiredArgsConstructor
public class EmpReissueDoReissueHandlerFlowable implements JavaDelegate {

    private final EmpReissueDoReissueService service;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        try {
            service.doReissue(requestId);
        } catch (Exception e) {
            log.error("EmpReissueDoReissueHandlerFlowable error for requestId {}", requestId, e);
            throw new BpmnError("REISSUE_FAILED", e.getMessage());
        }
    }
}
