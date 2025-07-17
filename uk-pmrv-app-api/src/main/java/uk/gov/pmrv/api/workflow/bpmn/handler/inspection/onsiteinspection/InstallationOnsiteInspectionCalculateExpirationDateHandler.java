package uk.gov.pmrv.api.workflow.bpmn.handler.inspection.onsiteinspection;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.service.InstallationOnsiteInspectionExpirationDateService;

import java.util.Date;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class InstallationOnsiteInspectionCalculateExpirationDateHandler implements JavaDelegate {

    private final RequestExpirationVarsBuilder requestExpirationVarsBuilder;
    private final InstallationOnsiteInspectionExpirationDateService installationOnsiteInspectionExpirationDateService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Date dueDate = installationOnsiteInspectionExpirationDateService.calculateExpirationDate(requestId);
        final Map<String, Object> expirationVars =
            requestExpirationVarsBuilder.buildExpirationVars(
                    RequestExpirationType.INSTALLATION_ONSITE_INSPECTION,
                    dueDate);

        execution.setVariables(expirationVars);
    }
}
