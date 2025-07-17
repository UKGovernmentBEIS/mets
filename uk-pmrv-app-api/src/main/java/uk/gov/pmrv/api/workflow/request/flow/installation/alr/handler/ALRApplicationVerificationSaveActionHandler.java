package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationVerificationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRVerificationSubmitService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ALRApplicationVerificationSaveActionHandler implements RequestTaskActionHandler<ALRApplicationVerificationSaveRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final ALRVerificationSubmitService alrVerificationSubmitService;

    @Override
    public void process(Long requestTaskId,
                        RequestTaskActionType requestTaskActionType,
                        AppUser appUser,
                        ALRApplicationVerificationSaveRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        alrVerificationSubmitService.applySaveAction(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.ALR_SAVE_APPLICATION_VERIFICATION);
    }
}
