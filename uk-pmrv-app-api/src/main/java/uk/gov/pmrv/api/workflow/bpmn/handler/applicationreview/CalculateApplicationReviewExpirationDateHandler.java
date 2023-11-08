package uk.gov.pmrv.api.workflow.bpmn.handler.applicationreview;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.CalculateApplicationReviewExpirationDateService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CalculateApplicationReviewExpirationDateHandler implements JavaDelegate {

    private final List<CalculateApplicationReviewExpirationDateService> reviewExpirationDateServices;
    private final RequestExpirationVarsBuilder requestExpirationVarsBuilder;
    
    @Override
    public void execute(DelegateExecution execution) {
        final RequestType requestType = RequestType.valueOf((String) execution.getVariable(BpmnProcessConstants.REQUEST_TYPE));

        reviewExpirationDateServices.stream().filter(service -> service.getTypes().contains(requestType)).findFirst()
                .ifPresentOrElse(
                        service -> service.expirationDate().ifPresent(expirationDate ->
                                execution.setVariables(requestExpirationVarsBuilder
                                        .buildExpirationVars(RequestExpirationType.APPLICATION_REVIEW, expirationDate))),
                        () -> execution.setVariables(requestExpirationVarsBuilder
                                .buildExpirationVars(RequestExpirationType.APPLICATION_REVIEW))
                );
    }
}
