package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.empvariation.corsia;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.service.EmpVariationCorsiaRegulatorLedApprovedGenerateDocumentsService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaRegulatorLedApprovedGenerateDocumentsHandlerFlowable implements JavaDelegate {

    private final EmpVariationCorsiaRegulatorLedApprovedGenerateDocumentsService service;

    @Override
    public void execute(DelegateExecution execution) {
        service.generateDocuments((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
