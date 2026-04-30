package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.emp.ukets;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service.EmpIssuanceGrantedGenerateDocumentsService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class EmpIssuanceGrantedGenerateDocumentsHandlerFlowable implements JavaDelegate {

    private final EmpIssuanceGrantedGenerateDocumentsService empIssuanceGrantedGenerateDocumentsService;

    @Override
    public void execute(DelegateExecution execution) {
        empIssuanceGrantedGenerateDocumentsService.generateDocuments((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
