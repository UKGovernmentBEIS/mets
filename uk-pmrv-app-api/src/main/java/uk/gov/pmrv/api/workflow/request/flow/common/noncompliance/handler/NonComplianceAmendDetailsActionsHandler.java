package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceAmendDetailsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service.NonComplianceApplyService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NonComplianceAmendDetailsActionsHandler
        implements RequestTaskActionHandler<NonComplianceAmendDetailsRequestTaskActionPayload> {


    private final RequestTaskService requestTaskService;
    private final NonComplianceApplyService nonComplianceApplyService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final AppUser appUser,
                        final NonComplianceAmendDetailsRequestTaskActionPayload taskActionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        nonComplianceApplyService.amendDetails(requestTask, taskActionPayload, appUser);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.NON_COMPLIANCE_AMEND_DETAILS);
    }
}
