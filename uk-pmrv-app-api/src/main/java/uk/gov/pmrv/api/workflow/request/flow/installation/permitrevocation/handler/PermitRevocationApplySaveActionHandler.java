package uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.service.RequestPermitRevocationService;

import java.util.List;

@RequiredArgsConstructor
@Component
public class PermitRevocationApplySaveActionHandler
    implements RequestTaskActionHandler<PermitRevocationSaveApplicationRequestTaskActionPayload> {

    private final RequestPermitRevocationService requestPermitRevocationService;
    private final RequestTaskService requestTaskService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final AppUser appUser,
                        final PermitRevocationSaveApplicationRequestTaskActionPayload actionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        requestPermitRevocationService.applySavePayload(actionPayload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_REVOCATION_SAVE_APPLICATION);
    }
}
