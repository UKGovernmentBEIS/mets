package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationAmendsSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service.BDRAmendsSubmitService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BDRApplicationAmendsSaveActionHandler implements
        RequestTaskActionHandler<BDRApplicationAmendsSaveRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final BDRAmendsSubmitService submitService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        BDRApplicationAmendsSaveRequestTaskActionPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        submitService.saveAmends(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.BDR_APPLICATION_AMENDS_SAVE);
    }

}
