package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationReminderService;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service.InstallationInspectionExpirationDateService;


@Service
public class InstallationAuditExpirationDateService
        extends InstallationInspectionExpirationDateService {

    public InstallationAuditExpirationDateService(RequestService requestService,
                                                 RequestAccountContactQueryService requestAccountContactQueryService,
                                                 RequestExpirationReminderService requestExpirationReminderService) {
        super(requestService,requestAccountContactQueryService,requestExpirationReminderService);
    }

    @Override
    protected RequestTaskType getRequestTaskType() {
        return RequestTaskType.INSTALLATION_AUDIT_APPLICATION_SUBMIT;
    }
}
