package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service.WithholdingOfAllowancesService;

import java.util.List;

@RequiredArgsConstructor
@Component
public class WithholdingOfAllowancesApplySaveActionHandler
    implements RequestTaskActionHandler<WithholdingOfAllowancesSaveApplicationRequestTaskActionPayload> {

    private final WithholdingOfAllowancesService withHoldingOfAllowancesService;
    private final RequestTaskService requestTaskService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final AppUser appUser,
                        final WithholdingOfAllowancesSaveApplicationRequestTaskActionPayload actionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        withHoldingOfAllowancesService.applySavePayload(actionPayload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.WITHHOLDING_OF_ALLOWANCES_SAVE_APPLICATION);
    }
}
