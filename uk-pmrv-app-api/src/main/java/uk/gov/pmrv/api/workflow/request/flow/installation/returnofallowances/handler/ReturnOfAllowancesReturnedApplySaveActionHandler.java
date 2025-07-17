package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesReturnedSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.service.ReturnOfAllowancesReturnedService;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ReturnOfAllowancesReturnedApplySaveActionHandler
    implements RequestTaskActionHandler<ReturnOfAllowancesReturnedSaveApplicationRequestTaskActionPayload> {

    private final ReturnOfAllowancesReturnedService returnOfAllowancesReturnedService;
    private final RequestTaskService requestTaskService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final AppUser appUser,
                        final ReturnOfAllowancesReturnedSaveApplicationRequestTaskActionPayload actionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        returnOfAllowancesReturnedService.applySavePayload(actionPayload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.RETURN_OF_ALLOWANCES_RETURNED_SAVE_APPLICATION);
    }
}
