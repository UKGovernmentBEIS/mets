package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspection;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class InstallationInspectionSubmittedDocumentTemplateWorkflowCommonParamsProvider {

    public static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d MMMM yyyy");
    public Map<String, Object> constructParams(InstallationInspection installationInspection) {

        final Map<String, Object> vars = new HashMap<>();

        vars.put("additionalInformation",installationInspection.getDetails().getAdditionalInformation());
        vars.put("followupActions",installationInspection.getFollowUpActions());
        vars.put("followUpActionsRequired",installationInspection.getFollowUpActionsRequired());
        vars.put("followUpActionsOmissionJustification",installationInspection.getFollowUpActionsOmissionJustification());

        if (!ObjectUtils.isEmpty(installationInspection.getResponseDeadline())) {
            vars.put("responseDeadline",installationInspection.getResponseDeadline().format(FORMATTER));
        } else {
            vars.put("responseDeadline", "");
        }


        return vars;
    }
}
