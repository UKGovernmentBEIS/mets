package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.domain.PermitVariationApplicationSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.service.PermitVariationAmendService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermitVariationSaveApplicationAmendActionHandler implements
    RequestTaskActionHandler<PermitVariationApplicationSaveApplicationAmendRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final PermitVariationAmendService permitVariationAmendService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        PermitVariationApplicationSaveApplicationAmendRequestTaskActionPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        permitVariationAmendService.amendPermitVariation(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_VARIATION_SAVE_APPLICATION_AMEND);
    }
}
