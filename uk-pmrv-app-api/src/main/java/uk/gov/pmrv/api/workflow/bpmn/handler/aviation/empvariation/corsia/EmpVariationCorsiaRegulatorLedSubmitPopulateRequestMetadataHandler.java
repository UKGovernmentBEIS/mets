package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.empvariation.corsia;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.service.EmpVariationCorsiaRegulatorLedSubmittedPopulateRequestMetadataService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaRegulatorLedSubmitPopulateRequestMetadataHandler implements JavaDelegate {

    private final EmpVariationCorsiaRegulatorLedSubmittedPopulateRequestMetadataService service;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        
        service.populateRequestMetadata((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}