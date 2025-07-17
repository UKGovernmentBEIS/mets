package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.handler;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.handler.InstallationInspectionOperatorRespondInitializer;

import java.util.Set;

@Service
public class InstallationAuditOperatorRespondInitializer
        extends InstallationInspectionOperatorRespondInitializer {
    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.INSTALLATION_AUDIT_OPERATOR_RESPOND_TO_FOLLOWUP_ACTIONS);
    }
}
