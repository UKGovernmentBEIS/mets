package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCivilPenaltySaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service.NonComplianceCivilPenaltyApplyService;

@Component
@RequiredArgsConstructor
public class NonComplianceCivilPenaltySaveApplicationActionHandler
    implements RequestTaskActionHandler<NonComplianceCivilPenaltySaveApplicationRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final NonComplianceCivilPenaltyApplyService applyService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final AppUser appUser,
                        final NonComplianceCivilPenaltySaveApplicationRequestTaskActionPayload taskActionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        applyService.applySaveAction(requestTask, taskActionPayload);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.NON_COMPLIANCE_CIVIL_PENALTY_SAVE_APPLICATION);
    }
}
